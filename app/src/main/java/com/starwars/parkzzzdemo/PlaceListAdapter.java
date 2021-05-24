package com.starwars.parkzzzdemo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

public class PlaceListAdapter extends RecyclerView.Adapter<PlaceListAdapter.ViewHolder> {

    Context context;
    ArrayList<String> latitude;
    ArrayList<String> longitude;
    ArrayList<String> rating;
    ArrayList<String> descripition;
    ArrayList<Integer> colourVal;



    public PlaceListAdapter(Context context, ArrayList<String> latitude, ArrayList<String> longitude, ArrayList<String> rating, ArrayList<String> descripition, ArrayList<Integer> colourVal){
        this.context = context;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rating = rating;
        this.descripition = descripition;
        this.colourVal = colourVal;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.place_listitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.latitudeTextView.setText(latitude.get(position));
        holder.latitudeTextView.setTextColor(context.getResources().getColor(colourVal.get(position)));
        holder.longitudeTextView.setText(longitude.get(position));
        holder.longitudeTextView.setTextColor(context.getResources().getColor(colourVal.get(position)));
        holder.position = position;
    }

    @Override
    public int getItemCount() {
        return colourVal.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView latitudeTextView, longitudeTextView, descripitionTextView;
        int position;
        RatingBar ratingBar;
        boolean responceTextDisplayed=false, ratingTextDisplayed=false;
        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            latitudeTextView = itemView.findViewById(R.id.latitude);
            longitudeTextView = itemView.findViewById(R.id.longitude);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            descripitionTextView = itemView.findViewById(R.id.descripition);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!responceTextDisplayed) {
                        descripitionTextView.setText(descripition.get(position));
                        responceTextDisplayed=true;
                    }
                    else{
                        descripitionTextView.setText("");
                        responceTextDisplayed=false;
                    }
                }
            });
            itemView.findViewById(R.id.placeResponceBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!ratingTextDisplayed) {
                        ratingBar.setVisibility(itemView.VISIBLE);
                        ratingBar.setRating(Float.parseFloat(rating.get(position))%5);
                        ratingTextDisplayed=true;
                    }
                    else{
                        ratingBar.setRating(0);
                        ratingBar.setVisibility(itemView.INVISIBLE);
                        ratingTextDisplayed=false;
                    }
                }
            });
            itemView.findViewById(R.id.placeLocationBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String uri = String.format(Locale.ENGLISH, "geo:%f,%f", Double.parseDouble(latitude.get(position)), Double.parseDouble(longitude.get(position)));
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    itemView.getContext().startActivity(intent);
                }
            });
        }
    }
}
