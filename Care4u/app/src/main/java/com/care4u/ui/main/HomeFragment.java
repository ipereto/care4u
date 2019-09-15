package com.care4u.ui.main;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.care4u.R;
import com.care4u.data.model.ProductResponse;
import com.care4u.service.APIService;
import com.care4u.service.PredictProductServiceClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private TextView textView;
    private APIService apiService;
    private static final String TAG = "Home:Fragment";

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);
        textView = root.findViewById(R.id.section_label);

        FloatingActionButton fabSos = root.findViewById(R.id.fabSos);
        fabSos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Call ambulance", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                String phone = "911";
                Intent intentSos = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                startActivity(intentSos);
            }
        });

        FloatingActionButton fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Take photo", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                dispatchTakePictureIntent();
            }
        });
        return root;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) Objects.requireNonNull(extras).get("data");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Objects.requireNonNull(imageBitmap).compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] imageBytes = stream.toByteArray();
            apiService = PredictProductServiceClient.request();
            sendPost(imageBytes);
            Bitmap bmp = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            final ImageView imageView = getView().findViewById(R.id.imageView);
            imageView.setImageBitmap(bmp);
        }
    }

    public void sendPost(byte[] imageBytes) {
        textView.setText("");
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imageBytes);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", "image.jpg", requestFile);
        Call<ProductResponse> call = apiService.saveImage(body);
        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {

                if(response.isSuccessful()) {
                    showResponse(response.body().getClasses());
                    Log.i(TAG, "post submitted to API." + response.body().toString());
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                Log.e(TAG, "Unable to submit post to API. ", t);
                textView.setText("Error: Unable to submit post to API");
            }
        });
    }

    public void showResponse(String response) {
        if(textView.getVisibility() == View.GONE) {
            textView.setVisibility(View.VISIBLE);
        }
        String msg = "Probabilidad: "+ response.toUpperCase() + ".\nCon base a su historial medico, " +
                "observamos que sufre de DIABETES, GASTRITIS.\nLe recomendamos NO CONSUMIR ESTE/ESTOS PRODUCTO/S, o consumirlo/s bajo su responsabilidad. Porque este alimento es una bebida muy ácida y el ph del estómago tiene que mantener su rango\n" +
                "Care4u se activa en caso de una emergencia. Se sincronizará con las ambulancias mas cercanas o usuarios con carro que usen Care4u.";
        textView.setText(msg);
    }
}