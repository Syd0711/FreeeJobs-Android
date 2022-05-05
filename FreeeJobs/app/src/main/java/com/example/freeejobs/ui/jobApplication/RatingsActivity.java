package com.example.freeejobs.ui.jobApplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.freeejobs.MySingleton;
import com.example.freeejobs.R;
import com.example.freeejobs.constant.Constants;
import com.example.freeejobs.constant.JobListingStatusEnum;
import com.example.freeejobs.data.model.JobApplicantsModel;
import com.example.freeejobs.data.model.JobApplicationModel;
import com.example.freeejobs.data.model.JobListingModel;
import com.example.freeejobs.ui.common.ErrorDialogFragment;
import com.example.freeejobs.ui.jobListing.ApplicantDetailsActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class RatingsActivity extends AppCompatActivity {


    EditText ratingReviewTitle;
    EditText ratingReview;
    RatingBar ratingStar;
    Button ratingButton;
    Logger log = LoggerFactory.getLogger(ApplicantDetailsActivity.class);

    long userId;

    final String ratingUrl = Constants.freeeJobsURL+Constants.ratingAPIPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);
        ratingReviewTitle = findViewById(R.id.RatingTitleValueTextView);
        ratingReview = findViewById(R.id.RatingReviewValueTextView);
        ratingStar = findViewById(R.id.ratingBar);
        ratingButton = findViewById(R.id.RatingButton);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
//            long jobListingId = extras.getLong("jobListingId");
//            userId = extras.getLong("userId");
//            userId = extras.getLong("userId");
            userId = 1;


        }

        ratingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long jobId = extras.getLong("jobListingId");
                long targetId = extras.getLong("targetId");

                submitRating(jobId, userId, targetId);
            }
        });

    }

    private void submitRating(long jobId, long reviewerId, long targetId) {
        String url =ratingUrl+"/create";
        JSONObject jsonObj = new JSONObject();

        try {
            jsonObj.put("jobId", jobId);
            jsonObj.put("reviewerId", reviewerId);
            jsonObj.put("targetId",targetId);
            jsonObj.put("reviewTitle", ratingReviewTitle.getText());
            jsonObj.put("review", ratingReview.getText());
            System.out.println("Rating Value : " + ratingStar.getRating());
            int intRatingStar = (int) ratingStar.getRating();
            System.out.println("Rating int Value : " + intRatingStar);
            jsonObj.put("ratingScale", intRatingStar);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONObject ratingObject = response.getJSONObject("data");
                        System.out.println("responseApplyJob:"+response);


                    } catch (JSONException e) {
                        log.error(this.getClass().getSimpleName()+ ": Exception - "+e.toString());
                        FragmentManager fm = getSupportFragmentManager();
                        ErrorDialogFragment alertDialog = ErrorDialogFragment.newInstance(Constants.ERROR_RESPONSE_MSG);
                        alertDialog.show(fm, "error_alert");
                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("response:"+error.toString());
                    // TODO: Handle error
                    log.error(this.getClass().getSimpleName()+ ": Response Error - "+error.toString());
                    FragmentManager fm = getSupportFragmentManager();
                    ErrorDialogFragment alertDialog = ErrorDialogFragment.newInstance(Constants.ERROR_RESPONSE_MSG);
                    alertDialog.show(fm, "error_alert");

                }
            });

            MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
        } catch (JSONException e) {
            log.error(this.getClass().getSimpleName()+ ": Exception - "+e.toString());
            FragmentManager fm = getSupportFragmentManager();
            ErrorDialogFragment alertDialog = ErrorDialogFragment.newInstance(Constants.ERROR_RESPONSE_MSG);
            alertDialog.show(fm, "error_alert");
        }



    }




}