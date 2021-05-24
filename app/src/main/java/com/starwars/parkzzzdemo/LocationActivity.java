package com.starwars.parkzzzdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Locale;

public class LocationActivity extends AppCompatActivity {

    TextView latitude, longitude, responceText, noDoc;
    ImageButton responceBtn, locationBtn;
    Button parkMe;
    double latitudeVal, longitudeVal;
    String latidudeStrVal, longitudeStrVal, docToRead, responceMsg="";
    boolean presentInDB = false, responceTextDisplayed = false;
    int scoreVal = 0, legalVal = 0, illegalVal = 0;
    double oldRating = 0.0;

    ArrayList<String> placesLatitude= new ArrayList<>();
    ArrayList<String> placeLongitude= new ArrayList<>();
    ArrayList<String> placeRating= new ArrayList<>();
    ArrayList<String> placeDescripition= new ArrayList<>();
    ArrayList<Integer> colourValue= new ArrayList<>();
    RecyclerView placeList;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        getSupportActionBar().hide();

        latitude = findViewById(R.id.latitude);
        longitude = findViewById(R.id.longitude);
        responceText = findViewById(R.id.responceText);
        noDoc=findViewById(R.id.noDocText);
        placeList = findViewById(R.id.placeList);
        responceBtn = findViewById(R.id.placeResponceBtn);
        locationBtn = findViewById(R.id.placeLocationBtn);
        parkMe = findViewById(R.id.parkMe);

        Intent dataFromMainAct = getIntent();
        latitudeVal = dataFromMainAct.getDoubleExtra("LatitudeVal", 404);
        longitudeVal = dataFromMainAct.getDoubleExtra("LongitudeVal", 404);
        latidudeStrVal = String.valueOf(latitudeVal);
        while(latidudeStrVal.length() < 10)
            latidudeStrVal = latidudeStrVal + "0";
        longitudeStrVal = String.valueOf(longitudeVal);
        while(longitudeStrVal.length() < 10)
            longitudeStrVal += "0";
        latitudeVal = Double.parseDouble(latidudeStrVal);
        longitudeVal = Double.parseDouble(longitudeStrVal);

        latitude.setText(latidudeStrVal);
        longitude.setText(longitudeStrVal);

        docToRead = latidudeStrVal.substring(0, 8)+","+longitudeStrVal.substring(0,8);

        DocumentReference doc = db.collection("GROUPS").document(docToRead);
        doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    for(Object i : documentSnapshot.getData().values()){
                        final DocumentReference ref = (DocumentReference) i;
                        ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if(!presentInDB && latidudeStrVal.substring(0,9).equals(documentSnapshot.getString("Latitude").substring(0,9)) && longitudeStrVal.substring(0,9).equals(documentSnapshot.getString("Longitude").substring(0,9))){
                                    presentInDB=true;
                                    latitude.setText(latidudeStrVal);
                                    longitude.setText(longitudeStrVal);
                                    if(documentSnapshot.getLong("IlLegalScore") + documentSnapshot.getLong("LegalScore") < 0.10*documentSnapshot.getLong("Score")) {
                                        latitude.setTextColor(getResources().getColor(R.color.greenPrime));
                                        longitude.setTextColor(getResources().getColor(R.color.greenPrime));
                                    }
                                    else {
                                        latitude.setTextColor(getResources().getColor(R.color.redPrime));
                                        longitude.setTextColor(getResources().getColor(R.color.redPrime));
                                    }
                                    scoreVal = (int) (long) documentSnapshot.getLong("Score");
                                    illegalVal = (int) (long) documentSnapshot.getLong("IlLegalScore");
                                    legalVal = (int) (long) documentSnapshot.get("LegalScore");
                                    oldRating = (double) documentSnapshot.get("Rating");
                                    responceMsg = "This place has "+documentSnapshot.get("Score")+" feedback received with "+documentSnapshot.get("Rating")+" star rating along with "+documentSnapshot.get("IlLegalScore")+" illeagal and "+ documentSnapshot.get("LegalScore")+" leagal issues recorded";
                                }
                                else {
                                    placesLatitude.add(documentSnapshot.getString("Latitude"));
                                    placeLongitude.add(documentSnapshot.getString("Longitude"));
                                    placeRating.add(""+documentSnapshot.get("Rating"));
                                    placeDescripition.add(""+documentSnapshot.getLong("Score")+" Feedback Received with " +documentSnapshot.getLong("IlLegalScore")+" illeagal and "+ documentSnapshot.getLong("LegalScore")+" leagal issues recorded");
                                    if(documentSnapshot.getLong("IlLegalScore") + documentSnapshot.getLong("LegalScore") < 0.10*documentSnapshot.getLong("Score"))
                                        colourValue.add(R.color.greenPrime);
                                    else
                                        colourValue.add(R.color.redPrime);
                                }
                            }
                        }).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                PlaceListAdapter placeListAdapter = new PlaceListAdapter(LocationActivity.this, placesLatitude, placeLongitude, placeRating, placeDescripition, colourValue);
                                placeList.setAdapter(placeListAdapter);
                                placeList.setLayoutManager(new LinearLayoutManager(LocationActivity.this));
                            }
                        });
                    }
                    if(!presentInDB){
                        latitude.setText(latidudeStrVal);
                        longitude.setText(longitudeStrVal);
                        responceMsg = "Sorry we dont have information about this place.";
                    }
                }
                else{
                    latitude.setText(latidudeStrVal);
                    longitude.setText(longitudeStrVal);
                    responceMsg = "Humm...Looks like we are new to this place.";
                    noDoc.setText("We Dont Serve that place yet.");
                }
            }
        });

        responceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!responceTextDisplayed) {
                    responceText.setText(responceMsg);
                    responceTextDisplayed = true;
                }
                else{
                    responceText.setText("");
                    responceTextDisplayed = false;
                }
            }
        });

        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uri = String.format(Locale.ENGLISH, "geo:%f,%f", latitudeVal, longitudeVal);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);
            }
        });



        parkMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent parkMeIntent = new Intent(LocationActivity.this, ParkMeActivity.class);
                parkMeIntent.putExtra("Latitude", Double.parseDouble(latidudeStrVal));
                parkMeIntent.putExtra("Longitude", Double.parseDouble(longitudeStrVal));
                parkMeIntent.putExtra("Rating", oldRating);
                parkMeIntent.putExtra("Score", scoreVal);
                parkMeIntent.putExtra("LegalScore", legalVal);
                parkMeIntent.putExtra("IlLegalScore", illegalVal);
                startActivity(parkMeIntent);
            }
        });
    }
}
