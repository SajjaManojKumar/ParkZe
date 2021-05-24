package com.starwars.parkzzzdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class ParkMeActivity extends AppCompatActivity {

    private static final int NOTIFICATION = 1;
    TextView latiVal, longiVal;
    int scoreVal=0, legalVal=0, illegalVal=0;
    double latitude=0, longitude=0, oldRating=0;
    Button feedbackBtn;
    NotificationManagerCompat notificationManagerCompat;

    private static final String CHANNEL_ID =
            "simplified_coding";
    private static final String CHANNEL_NAME =
            "Simplified Coding";
    private static final String CHANNEL_DESC =
            "Simplified Coding Notifications";

    NotificationCompat.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park_me);
        getSupportActionBar().hide();


        latiVal = findViewById(R.id.latiVal);
        longiVal = findViewById(R.id.longiVal);
        feedbackBtn = findViewById(R.id.feedback);

        Intent dataFromLocAct = getIntent();
        latitude = dataFromLocAct.getDoubleExtra("Latitude", latitude);
        longitude = dataFromLocAct.getDoubleExtra("Longitude", longitude);
        oldRating = dataFromLocAct.getDoubleExtra("Rating", oldRating);
        scoreVal = dataFromLocAct.getIntExtra("Score", scoreVal);
        legalVal = dataFromLocAct.getIntExtra("LegalScore", legalVal);
        illegalVal = dataFromLocAct.getIntExtra("IlLegalScore", illegalVal);

        latiVal.setText(""+latitude);
        longiVal.setText(""+longitude);

        if(Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.O){
            NotificationChannel channel = new
                    NotificationChannel(CHANNEL_ID,CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESC);
            NotificationManager manager =
                    getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        findViewById(R.id.pleaseClickMeToOpenInMap).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uri = String.format(Locale.ENGLISH, "geo:%f,%f", latitude, longitude);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);

            }
        });

        builder = displayNotification("Latitude : "+latitude+"\tLongitude : "+longitude);
        notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(NOTIFICATION, builder.build());

        feedbackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notificationManagerCompat.cancel(NOTIFICATION);
                Intent userFeedbackIntent = new Intent(ParkMeActivity.this, FeedbackActivity.class);
                userFeedbackIntent.putExtra("Latitude", latitude);
                userFeedbackIntent.putExtra("Longitude", longitude);
                userFeedbackIntent.putExtra("Rating", oldRating);
                userFeedbackIntent.putExtra("Score", scoreVal);
                userFeedbackIntent.putExtra("LegalScore", legalVal);
                userFeedbackIntent.putExtra("IlLegalScore", illegalVal);
                startActivity(userFeedbackIntent);
            }
        });

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Toast.makeText(getApplicationContext(), "Ah there you are!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(getApplicationContext(), "Looks like something went wrong!", Toast.LENGTH_LONG).show();
        notificationManagerCompat.cancel(NOTIFICATION);
    }

    private NotificationCompat.Builder displayNotification(String msg){
        Intent intent = new Intent(ParkMeActivity.this, ParkMeActivity.class);
        intent.putExtra("Latitude", latitude);
        intent.putExtra("Longitude", longitude);
        intent.putExtra("Rating", oldRating);
        intent.putExtra("Score", scoreVal);
        intent.putExtra("LegalScore", legalVal);
        intent.putExtra("IlLegalScore", illegalVal);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_parkze)
                .setContentTitle("Parking Info")
                .setContentText(msg)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(false)
                .setOngoing(true);

        return builder;
    }

}
