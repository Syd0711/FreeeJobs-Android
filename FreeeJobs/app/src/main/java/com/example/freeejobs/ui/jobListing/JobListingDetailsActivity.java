package com.example.freeejobs.ui.jobListing;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.freeejobs.MySingleton;
import com.example.freeejobs.R;
import com.example.freeejobs.constant.JobListingStatusEnum;
import com.example.freeejobs.data.model.JobListingModel;
import com.example.freeejobs.constant.Constants;
import com.example.freeejobs.ui.common.CommonUtils;
import com.example.freeejobs.ui.common.ErrorDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.stream.Collectors;

public class JobListingDetailsActivity extends AppCompatActivity {

    FloatingActionButton backButton;
    Button applyJobButton;
    TextView jobTitle;
    TextView jobStatus;
    TextView jobDetails;
    TextView jobRate;
    Button viewApplicantButton;
    Button deleteJobButton;

    JobListingModel listing;
    private long userId;
    private String userAppStatus = "";

    final String jobListingUrl = Constants.freeeJobsURL+Constants.jobListingAPIPath;
    final String jobAppUrl = Constants.freeeJobsURL+Constants.jobApplicationAPIPath;

    Logger log = LoggerFactory.getLogger(JobListingDetailsActivity.class);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_listing_details);
        backButton = findViewById(R.id.JobListingDetailsBackButton);
        applyJobButton = findViewById(R.id.ApplyJobButton);
        jobTitle = findViewById(R.id.JobTitleTextView);
        jobStatus= findViewById(R.id.JobStatusTextView);;
        jobDetails= findViewById(R.id.JobDetailsTextView);;
        jobRate= findViewById(R.id.JobRateTextView);
        viewApplicantButton = findViewById(R.id.ViewApplicantsButton);
        deleteJobButton = findViewById(R.id.DeleteJobButton);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Long jobListingId = extras.getLong("jobListingId");
            if(jobListingId==null|| !CommonUtils.isNumber(String.valueOf(jobListingId))){
                errorDialog(": Missing job listing id");
            }
            //get current userId
            SharedPreferences pref = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
            String sharedPrefId = pref.getString("id", null);
            if(sharedPrefId!=null){
                userId = Long.parseLong(sharedPrefId);
            }

            //userId = 4;
            getJobDetails(jobListingId);

            System.out.println("jobListingId:"+jobListingId);
        }else{
            errorDialog(": Missing Values (Intent)");
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        viewApplicantButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Activity activity = JobListingDetailsActivity.this;
                Intent intent = new Intent(activity, JobApplicantsActivity.class);
                intent.putExtra("jobListingId", extras.getLong("jobListingId"));
                activity.startActivity(intent);
            }
        });

        applyJobButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Activity activity = JobListingDetailsActivity.this;
                Intent intent = new Intent(activity, ApplyJobActivity.class);
                intent.putExtra("jobListingId", extras.getLong("jobListingId"));
                intent.putExtra("applicantId", userId);
//                intent.putExtra("applicantId", 1L);
                intent.putExtra("jobName", jobTitle.getText().toString());
                //TODO remove hardcoded value
                intent.putExtra("clientName", "Samuel");
                activity.startActivity(intent);
            }
        });

        deleteJobButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setJobStatus(extras.getLong("jobListingId"));
            }
        });

    }

    private void setJobStatus(long jobId) {
        String url =jobListingUrl+"/"+ jobId + "/updateJobListingStatus";
        JSONObject jsonObj = new JSONObject();

        try {
            jsonObj.put("status",JobListingStatusEnum.JOB_LISTING_STATUS_REMOVED.getCode());

            StringRequest stringRequest = new StringRequest(Request.Method.PUT, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObj = new JSONObject(response);
                                System.out.println("Response setJobStatus " + response);
                                System.out.println("Response setJobStatus " + jsonObj.getJSONObject("data").getString("status"));
                                JSONObject status = jsonObj.getJSONObject("status");
                                if(Integer.toString((Integer)status.get("statusCode")).equalsIgnoreCase(Constants.API_SUCCESS_CODE)){
                                    if(jsonObj.getJSONObject("data").getString("status").equals(JobListingStatusEnum.JOB_LISTING_STATUS_REMOVED.getCode())) {
                                        finish();
                                    }
                                }else{
                                    Toast.makeText(getApplicationContext(), "Failed to Delete Job", Toast.LENGTH_LONG).show();
                                    errorDialog(": Status Code is "+status.get("statusCode")+" | "+status);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                errorDialog(": Response Error - "+e.toString());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //System.out.println("response:"+error.toString());
                            //Toast.makeText(JobListingDetailsActivity.this, error.toString(),Toast.LENGTH_LONG).show();
                            // TODO: Handle error
                            errorDialog(": Response Error - "+error.toString());

                        }
                    }){
                @Override
                public byte[] getBody() throws AuthFailureError {
                    String httpPutBody= JobListingStatusEnum.JOB_LISTING_STATUS_REMOVED.getCode();

                    return httpPutBody.getBytes();
                }

            };

            MySingleton.getInstance(this).addToRequestQueue(stringRequest);
        } catch (JSONException e) {
            //e.printStackTrace();
            errorDialog(": Response Error - "+e.toString());
        }


    }

    private void getJobDetails(long jobListingId) {
        String url =jobListingUrl+"/getJobListing/?listingId="+jobListingId;
        List<String> errors = new ArrayList<String>();

        JsonObjectRequest jsonAObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject jobListingDetailsObject = response.getJSONObject("data");
                            System.out.println("response:"+response);
                            JSONObject status = response.getJSONObject("status");
                            if(Integer.toString((Integer)status.get("statusCode")).equalsIgnoreCase(Constants.API_SUCCESS_CODE)){
                                if(jobListingDetailsObject!=null||jobListingDetailsObject.length()>0){
                                    JSONObject listingDetail = jobListingDetailsObject;
                                    listing = new JobListingModel();
                                    // fetch variables and store it in JobListing object
                                    if(CommonUtils.isNumber(listingDetail.getString("authorId"))){
                                        listing.setAuthorId(Integer.parseInt(listingDetail.getString("authorId")));
                                    }else{
                                        errors.add("Invalid Author Id");
                                    }
                                    if(CommonUtils.isNumber(listingDetail.getString("id"))){
                                        listing.setId(Integer.parseInt(listingDetail.getString("id")));
                                    }else{
                                        errors.add("Invalid Id");
                                    }

                                    listing.setTitle(CommonUtils.removeSpecialCharacters(listingDetail.getString("title")));
                                    listing.setDetails(CommonUtils.removeSpecialCharacters(listingDetail.getString("details")));
                                    if(CommonUtils.isNumber(listingDetail.getString("rate"))){
                                        listing.setRate(listingDetail.getString("rate"));
                                    }else{
                                        errors.add("Invalid rate");
                                    }
                                    if(CommonUtils.isRateType(listingDetail.getString("rateType"))){
                                        listing.setRateType(listingDetail.getString("rateType"));
                                    }else{
                                        errors.add("Invalid rate type");
                                    }
                                    if(!JobListingStatusEnum.Constants.JOB_LISTING_STATUS_LIST.contains(listingDetail.getString("status"))) {
                                        errors.add("Invalid status value");
                                    }else{
                                        listing.setStatus(listingDetail.getString("status"));
                                    }

                                    if(errors.isEmpty()) {
                                        //listing.setDateCreated(new SimpleDateFormat("MM-dd-yyyy").parse(listingDetail.getString("dateCreated")));
                                        jobTitle.setText(listing.getTitle());
                                        jobStatus.setText(JobListingStatusEnum.getEnumByCode(listingDetail.getString("status")).getDescription());
                                        jobDetails.setText(listing.getDetails());
                                        jobRate.setText(listing.getRateType() + ": $" + listing.getRate());
                                        if (isUserClient(listing.getAuthorId(), jobListingId)) {
                                            isListingOpen(listing.getStatus());
                                        }
                                    }else{
                                        String listOfErrors = TextUtils.join(",", errors);
                                        errorDialog(": " +listOfErrors);
                                    }

                                }else{
                                    errorDialog(": Record Not Found.");
                                }
                            }else{
                                Toast.makeText(getApplicationContext(), "Failed to Get Job Details", Toast.LENGTH_LONG).show();
                                errorDialog(": Status Code is "+status.get("statusCode")+" | "+status);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            errorDialog(": Exception - "+e.toString());
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //System.out.println("response:"+error.toString());
                        errorDialog(": Response Error - "+error.toString());

                    }
                });

        // Add a request (in this example, called stringRequest) to your RequestQueue.
        MySingleton.getInstance(this).addToRequestQueue(jsonAObjectRequest);
    }

    private void getApplicantStatus(long jobListingId) {
        String url =jobAppUrl+"/getUserApplicationStatus?jobId="+jobListingId+"&userId=" + userId;
        System.out.println("getApplicantStatus URL :" + url);

        JsonObjectRequest jsonAObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject applicantStatusObject = response.getJSONObject("data");
                            System.out.println("response applicantStatusObject:"+response);
                            JSONObject status = response.getJSONObject("status");
                            if(Integer.toString((Integer)status.get("statusCode")).equalsIgnoreCase(Constants.API_SUCCESS_CODE)){
                                if(applicantStatusObject!=null){
                                    hasApplicantApplied(applicantStatusObject.getString("status"));
                                }else{
                                    //TODO: set no records found
                                    errorDialog(": No Records Found.");
                                }
                            }else{
                                Toast.makeText(getApplicationContext(), "Failed to Get Applicant Status", Toast.LENGTH_LONG).show();
                                errorDialog(": Status Code is "+status.get("statusCode")+" | "+status);
                            }


                        } catch (JSONException e) {
                            errorDialog(": Exception - "+e.toString());
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //System.out.println("response:"+error.toString());
                        //Toast.makeText(JobListingDetailsActivity.this, error.toString(),Toast.LENGTH_LONG).show();
                        // TODO: Handle error
                        errorDialog(": Response Error - "+error.toString());

                    }
                });

        // Add a request (in this example, called stringRequest) to your RequestQueue.
        MySingleton.getInstance(this).addToRequestQueue(jsonAObjectRequest);

    }

    private boolean isUserClient(long authorId, long jobListingId) {

        if(userId == authorId)
        {
            viewApplicantButton.setVisibility(View.VISIBLE);
            applyJobButton.setVisibility(View.GONE);
            deleteJobButton.setVisibility(View.VISIBLE);
            return true;

        }
        else {
            viewApplicantButton.setVisibility(View.GONE);
            applyJobButton.setVisibility(View.VISIBLE);
            deleteJobButton.setVisibility(View.GONE);
            getApplicantStatus(jobListingId);
            return false;
        }
    }

    private void isListingOpen(String status) {
        System.out.println("listing status " + status);
        if (status.equals(JobListingStatusEnum.JOB_LISTING_STATUS_PENDING_COMPLETION.getCode())) {
            viewApplicantButton.setVisibility(View.GONE);
            deleteJobButton.setVisibility(View.GONE);
        }
        else if (status.equals(JobListingStatusEnum.JOB_LISTING_STATUS_REMOVED.getCode())) {
            viewApplicantButton.setVisibility(View.GONE);
            deleteJobButton.setVisibility(View.GONE);
        }
        else {
            viewApplicantButton.setVisibility(View.VISIBLE);
            deleteJobButton.setVisibility(View.VISIBLE);
        }
    }

    private void hasApplicantApplied(String status) {

        if(!status.equals(""))
        {
            System.out.println("status: " + status);
            applyJobButton.setVisibility(View.GONE);
        }
        else {
            System.out.println("status2: " + status);
            applyJobButton.setVisibility(View.VISIBLE);
        }
    }
    private void errorDialog(String error){
        log.error(this.getClass().getSimpleName()+ error);
        FragmentManager fm = getSupportFragmentManager();
        ErrorDialogFragment alertDialog = ErrorDialogFragment.newInstance(Constants.ERROR_RESPONSE_MSG);
        alertDialog.show(fm, "error_alert");
    }
}