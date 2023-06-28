package com.example.deneme1;

import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class DocumentName {
    private FirebaseFirestore mFirestore;

    public DocumentName() {
    }
    public void getDomumentName(FirebaseResults firebaseResults) {
        mFirestore= FirebaseFirestore.getInstance();

        mFirestore.collection("Puanlar").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                ArrayList<String> docunames=new ArrayList<>();
                List<DocumentSnapshot> snapshotList = task.getResult().getDocuments();
                for (DocumentSnapshot snapshot:snapshotList){
                    docunames.add(snapshot.getId());
                }
                firebaseResults.AllDocuments(docunames);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    interface FirebaseResults{
        void AllDocuments(ArrayList<String> names);
    }
}
