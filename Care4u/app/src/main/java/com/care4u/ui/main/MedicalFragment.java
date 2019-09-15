package com.care4u.ui.main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.care4u.R;
import com.care4u.data.model.Disease;
import com.care4u.data.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collections;

public class MedicalFragment extends Fragment {

    private static final String TAG = "Medical:Fragment";
    DocumentReference docRef;

    public static MedicalFragment newInstance() {
        return new MedicalFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        docRef = FirebaseFirestore.getInstance().document("care4u/user");
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_medical, container, false);

        FloatingActionButton fabSave = root.findViewById(R.id.fabsave);
        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Preparing to save", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                saveMedicalHistory(view);
            }
        });
        return root;
    }

    private void saveMedicalHistory(final View view) {
        Disease disease = new Disease();
        disease.setName("algo");
        disease.setDescription("se produce por el ph.");
        User user = new User();
        user.setUid("algo");
        user.setDiseases(Collections.singletonList(disease));
       docRef.collection("diseases").add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                Snackbar.make(view, "Saved successfully", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            Log.w(TAG, "Error adding document", e);
        }});
    }
}
