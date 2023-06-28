package com.example.deneme1;

import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

public class game_end extends AppCompatActivity {
    private FirebaseFirestore mFirestore;
    private DocumentReference mDocRef;
    private Task<QuerySnapshot> query;
    private String b;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_end);

        Intent i = getIntent();

        mFirestore= FirebaseFirestore.getInstance();

        int skor=i.getIntExtra("Skor",0);


        final long[] k = new long[1];



        TextView textView1=(TextView) findViewById(R.id.t1);
        TextView textView2=(TextView) findViewById(R.id.t2);
        TextView textView3=(TextView) findViewById(R.id.t3);
        TextView textView4=(TextView) findViewById(R.id.t4);
        TextView textView5=(TextView) findViewById(R.id.t5);
        TextView textView6=(TextView) findViewById(R.id.t6);
        TextView textView7=(TextView) findViewById(R.id.t7);
        TextView textView8=(TextView) findViewById(R.id.t8);
         List<Long> puanlar=new ArrayList<>();


        //mDocRef=mFirestore.collection("Puanlar")

        new DocumentName().getDomumentName(new DocumentName.FirebaseResults() {
            @Override
            public void AllDocuments(ArrayList<String> names) {
                //System.out.println(names.size());
                //System.out.println(names.get(j));


                for (int j=0;j<names.size();j++) {
                    System.out.println("girdim");


                    mDocRef = mFirestore.collection("Puanlar").document(names.get(j));

                    mDocRef.get()
                            .addOnSuccessListener(game_end.this, new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot.exists()) {
                                        //System.out.println(documentSnapshot.getData().get("Puan"));
                                        k[0] = (long) documentSnapshot.getData().get("Puan");

                                        puanlar.add(k[0]);


                                        //System.out.println(documentSnapshot.getData());




                                    }
                                }
                            }).addOnFailureListener(game_end.this, new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull @NotNull Exception e) {
                                    System.out.println("başarısız");
                                }
                            });



                }


            }
        });







        textView1.setText("Skor:"+skor);
        textView2.setText("1.Skor:"+10);
        textView3.setText("2.Skor:"+8);
        textView4.setText("3.Skor:"+5);
        textView5.setText("4.Skor:"+4);
        textView6.setText("5.Skor:"+0);

        //mDocRef=mFirestore.collection("Puanlar").document("Puan");

    }

}
