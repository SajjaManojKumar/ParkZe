package com.starwars.parkzzzdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class FeedbackActivity extends AppCompatActivity {

    RatingBar rating;
    ToggleButton legal, illegal;
    Button submit;
    double latitude, longitude;
    FirebaseFirestore db;
    DocumentReference docRef;
    String latilongiDirectory, subDirectory;

    String Latitude, Longitude;
    int totalScore=0, legalScore=0, illegalScore=0;
    Double Rating,oldRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        getSupportActionBar().hide();


        Intent dataFromLocAct = getIntent();
        latitude = dataFromLocAct.getDoubleExtra("Latitude", 0);
        longitude = dataFromLocAct.getDoubleExtra("Longitude", 0);
        oldRating = dataFromLocAct.getDoubleExtra("Rating", 0);
        totalScore = dataFromLocAct.getIntExtra("Score", totalScore);
        legalScore = dataFromLocAct.getIntExtra("LegalScore", legalScore);
        illegalScore = dataFromLocAct.getIntExtra("IlLegalScore", illegalScore);


        rating = findViewById(R.id.ratingBar);
        legal = findViewById(R.id.toggleButton1);
        illegal = findViewById(R.id.toggleButton2);
        submit = findViewById(R.id.submit);
        db = FirebaseFirestore.getInstance();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Latitude = String.valueOf(latitude);
                Longitude = String.valueOf(longitude);
                latilongiDirectory="";
                subDirectory="";
                if(getStatusOfToggleBtn(legal)){legalScore++;}
                if(getStatusOfToggleBtn(illegal)){illegalScore++;}
                Rating = calculateRating(rating.getRating(), oldRating, totalScore);
                totalScore++;

                Map<String, Object> placeValues = new HashMap<>();
                placeValues.put("Latitude", Latitude);
                placeValues.put("Longitude", Longitude);
                placeValues.put("Rating", Rating);
                placeValues.put("Score", totalScore);
                placeValues.put("LegalScore", legalScore);
                placeValues.put("IlLegalScore", illegalScore);


                latilongiDirectory = latilongiDirectory + Latitude + "," + Longitude;
                try {
                    int latiEnd = Latitude.charAt(2) == '.' ? 8 : 7;
                    int longiEnd = Longitude.charAt(2) == '.' ? 8 : 7;
                    subDirectory = subDirectory + Latitude.substring(0, latiEnd) + "," + Longitude.substring(0, longiEnd);
                } catch (Exception e) {
                    subDirectory = "ERROR";
                }

                db.collection("PLACES")
                        .document(latilongiDirectory)
                        .set(placeValues, SetOptions.merge())
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
                                                Toast.makeText(getApplicationContext(), "FEEDBACK RECEIVED", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(getApplicationContext(), "Error adding document", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(FeedbackActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                });

            }
        });

    }

    private boolean getStatusOfToggleBtn(ToggleButton t){
        if(t.getText() == t.getTextOn()){
            return true;
        }
        return false;
    }

    private double calculateRating(float currentRatingVal, double oldRatingVal, int scoreVal){
        double tempDoble = ((oldRatingVal*scoreVal)+currentRatingVal)/(scoreVal+1);
        tempDoble = tempDoble - (tempDoble%0.5);
        return tempDoble;
    }
}