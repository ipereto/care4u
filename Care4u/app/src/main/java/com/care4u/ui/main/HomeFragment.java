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
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private FirebaseAuth auth;
    final List<String> diseasesNames = new ArrayList<>();
    private Query query;
    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser() != null && auth.getCurrentUser().getDisplayName() != null
                && textView != null) {
            textView.setText(String.format("Welcome! %s", auth.getCurrentUser().getDisplayName()));
        }
        DocumentReference docRef = FirebaseFirestore.getInstance().document("care4u/user");
        CollectionReference diseasesRef = docRef.collection("diseases");
        query = diseasesRef.whereEqualTo("uid", "mau");

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                for (QueryDocumentSnapshot snap : querySnapshot) {
                    Log.d(TAG, snap.getId() + " => " + snap.getData());
                    List<HashMap<String, Object>> diseasesList = (List<HashMap<String, Object>>) snap.getData().get("diseases");
                    for (int i = 0; i < (diseasesList != null ? diseasesList.size() : 0); i++) {
                        HashMap<String, Object> diseaseVar = diseasesList.get(i);
                        String name = (String) diseaseVar.get("name");
                        Boolean enable = (Boolean) diseaseVar.get("enable");
                        if(enable) {
                            diseasesNames.add(name);
                        }

                    }
                }
            }
        });
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

    public void showResponse(final String response) {
        if(textView.getVisibility() == View.GONE) {
            textView.setVisibility(View.VISIBLE);
        }
        diseasesNames.clear();
        String msg = "Probability: "+ response.toUpperCase() + ".\nBased on your medical history, " +
                "We observe that you do not suffer from anything.\n" +
                "Care4u is activated in case of an emergency. Synchronize with the nearest ambulances or car users who use Care4u.";

        textView.setText(msg);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                for (QueryDocumentSnapshot snap : querySnapshot) {
                    Log.d(TAG, snap.getId() + " => " + snap.getData());
                    List<HashMap<String, Object>> diseasesList = (List<HashMap<String, Object>>) snap.getData().get("diseases");
                    for (int i = 0; i < (diseasesList != null ? diseasesList.size() : 0); i++) {
                        HashMap<String, Object> diseaseVar = diseasesList.get(i);
                        String name = (String) diseaseVar.get("name");
                        Boolean enable = (Boolean) diseaseVar.get("enable");
                        if(enable) {
                            diseasesNames.add(name);

                            StringBuilder csvBuilder = new StringBuilder();
                            for(String city : diseasesNames){
                                csvBuilder.append(city);
                                csvBuilder.append(",");
                            }
                            String csv = csvBuilder.toString();
                            csv = csv.substring(0, csv.length() - ",".length());
                            String msg = "Probability: "+ response.toUpperCase() + ".\nBased on your medical history, " +
                                    "we observe that he suffers from " + csv + ".\nWe recommend NOT TO CONSUME THIS/THESE PRODUCT/S, or consume it/them under its responsibility.\n" +
                                    "Care4u is activated in case of an emergency. Synchronize with the nearest ambulances or car users who use Care4u.";
                            textView.setText(msg);
                        }

                    }
                }
            }
        });
        query.get().addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                String msg = "Probability: "+ response.toUpperCase() + ".\nBased on your medical history, " +
                        "We observe that you do not suffer from anything.\n" +
                        "Care4u is activated in case of an emergency. Synchronize with the nearest ambulances or car users who use Care4u.";

                textView.setText(msg);
            }
        });
    }
}