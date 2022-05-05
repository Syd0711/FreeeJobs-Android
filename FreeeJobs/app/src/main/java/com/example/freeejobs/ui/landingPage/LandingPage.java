package com.example.freeejobs.ui.landingPage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.freeejobs.R;
import com.example.freeejobs.constant.Constants;
import com.example.freeejobs.databinding.ActivityLandingPageBinding;
import com.example.freeejobs.databinding.ActivityLoginBinding;
import com.example.freeejobs.ui.jobApplication.JobApplicationActivity;
import com.example.freeejobs.ui.jobListing.JobListing;
import com.example.freeejobs.ui.jobListing.MyJobsActivity;
import com.example.freeejobs.ui.login.LoginActivity;
import com.example.freeejobs.ui.postedJobListing.PostedJobActivity;
import com.example.freeejobs.ui.profile.ProfileActivity;

public class LandingPage extends AppCompatActivity {

    private ActivityLandingPageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);

        final Button findJobButton = findViewById(R.id.findJobButton);
        final Button jobApplicationStatusButton = findViewById(R.id.jobApplicationStatusButton);
        final Button postedJobButton = findViewById(R.id.postedJobButton);
        final Button myProfileButton = findViewById(R.id.myProfileButton);
        final Button logoutButton = findViewById(R.id.logoutButton);

        findJobButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("test ", "Hello World");
                openJobListings();
            }
        });
        jobApplicationStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openJobApplications();
            }
        });
        postedJobButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openMyJobs();
            }
        });
        myProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openProfile();
            }
        });
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

    }

    private void openProfile() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    private void openMyJobs() {
        Intent intent = new Intent(this, PostedJobActivity.class);
        startActivity(intent);
    }

    private void openJobApplications() {
        Intent intent = new Intent(this, JobApplicationActivity.class);
        startActivity(intent);
    }

    private void openJobListings() {
        Intent intent = new Intent(this, JobListing.class);
        startActivity(intent);
    }

    private void logout() {
        clearSharedPref();

        //dataSource.logout();
    }
    private void clearSharedPref(){
        SharedPreferences settings = getApplicationContext().getSharedPreferences(Constants.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();

        editor.commit();

        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }


}