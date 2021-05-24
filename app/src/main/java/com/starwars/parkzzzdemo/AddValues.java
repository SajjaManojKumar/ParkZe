package com.starwars.parkzzzdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class AddValues extends AppCompatActivity {

    EditText newLati, newLongi, newRate, newScore, newLeagal, newIlLeagal;
    double score;
    int leagalScore, illeagalScore;
    double rating;
    FirebaseFirestore db;
    String latiStrValue, longiStrValue;
    String latilongiDirectory, subDirectory;
    Button button;
    DocumentReference docRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_values);

        newLati=findViewById(R.id.newLatitude);
        newLongi=findViewById(R.id.newLongitude);
        newRate=findViewById(R.id.newRating);
        newScore=findViewById(R.id.newScore);
        newLeagal=findViewById(R.id.newLeagal);
        newIlLeagal=findViewById(R.id.newIlLeagal);
        button=findViewById(R.id.button);

        Intent dataFromLocAct = getIntent();
        newLati.setText(String.valueOf(dataFromLocAct.getDoubleExtra("Lati", 404)));
        newLongi.setText(String.valueOf(dataFromLocAct.getDoubleExtra("Longi", 404)));


        db = FirebaseFirestore.getInstance();


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                latiStrValue = newLati.getText().toString();
                longiStrValue = newLongi.getText().toString();
                score=Integer.parseInt(newScore.getText().toString());
                leagalScore=Integer.parseInt(newLeagal.getText().toString());
                illeagalScore=Integer.parseInt(newIlLeagal.getText().toString());
                rating=Double.parseDouble(newRate.getText().toString());
                latilongiDirectory="";
                subDirectory="";


                Map<String, Object> placeValues = new HashMap<>();
                placeValues.put("Latitude", latiStrValue);
                placeValues.put("Longitude", longiStrValue);
                placeValues.put("Rating", rating);
                placeValues.put("Score", score);
                placeValues.put("LegalScore", leagalScore);
                placeValues.put("IlLegalScore", illeagalScore);

                latilongiDirectory = latilongiDirectory + latiStrValue + "," + longiStrValue;
                try {
                    int latiEnd = latiStrValue.charAt(2) == '.' ? 8 : 7;
                    int longiEnd = latiStrValue.charAt(2) == '.' ? 8 : 7;
                    subDirectory = subDirectory + latiStrValue.substring(0, latiEnd) + "," + longiStrValue.substring(0, longiEnd);
                } catch (Exception e) {
                    subDirectory = "ERROR";
                }
                Toast.makeText(getApplicationContext(), latilongiDirectory, Toast.LENGTH_SHORT).show();

                db.collection("PLACES")
                        .document(latilongiDirectory)
                        .set(placeValues)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                docRef = db.collection("PLACES").document(latilongiDirectory);
                                Map<String, Object> values = new HashMap<>();
                                values.put(latilongiDirectory, docRef);
                                db.collection("GROUPS")
                                        .document(subDirectory)
                                        .set(values, SetOptions.merge())
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(getApplicationContext(), "ADDED "+subDirectory, Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getApplicationContext(), "ERROR" + e, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
//                        Log.w(TAG, "Error writing document", e);
                                Toast.makeText(getApplicationContext(), "Error adding document", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(AddValues.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
            }
        });
    }
}
