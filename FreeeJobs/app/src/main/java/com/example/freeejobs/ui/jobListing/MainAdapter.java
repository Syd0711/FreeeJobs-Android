package com.example.freeejobs.ui.jobListing;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.freeejobs.R;
import com.example.freeejobs.data.model.JobListingModel;

import java.util.ArrayList;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {
    //initialise variable
    private ArrayList<JobListingModel> jobListings;
    private Activity activity;
    private long userId;
     public MainAdapter(Activity activity, ArrayList<JobListingModel> jobListings){
         this.activity =activity;
         this.jobListings=jobListings;
         System.out.println("MainAdapter:");
     }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
         View view = LayoutInflater
                 .from(parent.getContext())
                 .inflate(R.layout.list_job_listing_row_main, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
         JobListingModel listing = jobListings.get(position);
         holder.jobTitle.setText(listing.getTitle());
         holder.ratesAndType.setText(listing.getRateType()+": $"+listing.getRate());
         //userId = 1;

    }

    @Override
    public int getItemCount() {
        return jobListings.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //initialize variables
        TextView jobTitle;
        TextView ratesAndType;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            jobTitle = itemView.findViewById(R.id.textViewJobTitle);
            ratesAndType = itemView.findViewById(R.id.textViewJobRating);

            jobTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.out.println("MainAdapter:" + ""+getAdapterPosition());
                    JobListingModel listing = jobListings.get(getAdapterPosition());
                    Intent intent = new Intent(activity, JobListingDetailsActivity.class);
                    //Log.d("select Job  ", "" + listing.getId());
                    intent.putExtra("jobListingId", listing.getId());
                    //intent.putExtra("userId", userId);
                    activity.startActivity(intent);
                }
            });
        }
    }
}
