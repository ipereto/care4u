package com.care4u.ui.main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.care4u.R;
import com.care4u.data.model.Disease;
import com.care4u.data.model.User;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MedicalFragment extends Fragment {

    private static final String TAG = "Medical:Fragment";
    private DocumentReference docRef;
    private FirebaseAuth auth;
    private Switch swcholesterol;
    private Switch swdiabetes;
    private Switch swgastritis;
    private Switch swirritable;
    private Switch swlactose;
    private Switch swhypertension;
    private Query query;

    public static MedicalFragment newInstance() {
        return new MedicalFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_medical, container, false);
        swcholesterol = root.findViewById(R.id.cholesterol);
        swdiabetes = root.findViewById(R.id.diabetes);
        swgastritis = root.findViewById(R.id.gastritis);
        swirritable = root.findViewById(R.id.irritable);
        swlactose = root.findViewById(R.id.lactose);
        swhypertension = root.findViewById(R.id.hypertension);
        FloatingActionButton fabSave = root.findViewById(R.id.fabsave);
        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Preparing to save", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                saveMedicalHistory(view);
            }
        });
        auth = FirebaseAuth.getInstance();
        docRef = FirebaseFirestore.getInstance().document("care4u/user");
        CollectionReference diseasesRef = docRef.collection("diseases");
        query = diseasesRef.whereEqualTo("uid", "mau");

        final List<Switch> switches = Arrays.asList(swcholesterol, swdiabetes, swgastritis,
                swirritable, swlactose, swhypertension);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                for (QueryDocumentSnapshot snap : querySnapshot) {
                    Log.d(TAG, snap.getId() + " => " + snap.getData());
                    List<HashMap<String, Object>> diseasesList = (List<HashMap<String, Object>>) snap.getData().get("diseases");
                    for (int i = 0; i < (diseasesList != null ? diseasesList.size() : 0); i++) {
                        HashMap<String, Object> diseaseVar = diseasesList.get(i);
                        Switch diseaseItem = switches.get(i);
                        String name = (String) diseaseVar.get("name");
                        Boolean enable = (Boolean) diseaseVar.get("enable");
                        if(name.equalsIgnoreCase(diseaseItem.getText().toString())) {
                            diseaseItem.setChecked(enable);
                        }
                    }
                }
            }
        });
        return root;
    }

    private void saveMedicalHistory(final View view) {

        Disease disease;
        List<Disease> diseases = new ArrayList<>();
        List<Switch> switches = Arrays.asList(swcholesterol, swdiabetes, swgastritis,
                swirritable, swlactose, swhypertension);
        for (Switch diseaseItem: switches) {
            disease = new Disease();
            disease.setName(diseaseItem.getText().toString());
            disease.setEnable(diseaseItem.isChecked());
            diseases.add(disease);
        }
        User user = new User();
        user.setUid(auth.getUid() != null ?  auth.getUid() : "mau");
        user.setDiseases(diseases);
        docRef.collection("diseases").document("mau").set(user);
        Snackbar.make(view, "Saved successfully", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}
