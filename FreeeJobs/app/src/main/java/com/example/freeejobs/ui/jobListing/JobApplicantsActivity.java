package com.example.freeejobs.ui.jobListing;

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
import com.example.freeejobs.data.model.JobListingModel;
import com.example.freeejobs.data.model.UserModel;
import com.example.freeejobs.ui.common.CommonUtils;
import com.example.freeejobs.ui.common.ErrorDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JobApplicantsActivity extends AppCompatActivity {

    FloatingActionButton backButton;
    TextView jobApplicantTitle;
    private long userId;
    ApplicantsAdapter applicantsAdapter;
    NestedScrollView nestedScrollView;
    RecyclerView recyclerView;
    ArrayList<JobApplicantsModel> jobApplicants = new ArrayList<>();

    final String jobListingUrl = Constants.freeeJobsURL+Constants.jobListingAPIPath;
    final String jobAppUrl = Constants.freeeJobsURL+Constants.jobApplicationAPIPath;
    final String iamUrl = Constants.freeeJobsURL+Constants.iamAPIPath;

    Logger log = LoggerFactory.getLogger(JobApplicantsActivity.class);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_applicants);
        backButton = findViewById(R.id.JobApplicantsBackButton);
        jobApplicantTitle = findViewById(R.id.jobApplicantsTitle);

        nestedScrollView = findViewById(R.id.nestedScrollViewJobApplicants);
        recyclerView = findViewById(R.id.recyclerViewJobApplicants);

        applicantsAdapter = new ApplicantsAdapter(JobApplicantsActivity.this, jobApplicants);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(applicantsAdapter);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            long jobListingId = extras.getLong("jobListingId");
            getApplicants(jobListingId);
            //get current userId
            SharedPreferences pref = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
            String sharedPrefId = pref.getString("id", null);
            if(sharedPrefId!=null){
                userId=Integer.parseInt(sharedPrefId);
            }

            System.out.println("jobListingId:"+jobListingId);

        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if(scrollY== v.getChildAt(0).getMeasuredHeight()-v.getMeasuredHeight()){
                    long jobListingId = extras.getLong("jobListingId");
                    getApplicants(jobListingId);
                }
            }
        });

    }

    private void getApplicants(long jobListingId) {
        String url =jobAppUrl+"/listApplicantsByJobId?jobId="+jobListingId;

        System.out.println("getApplicants: "+url);
        JsonObjectRequest jsonAObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jobApplicantsDetails = response.getJSONArray("data");
                            System.out.println("response:"+response);
                            JSONObject status = response.getJSONObject("status");
                            if(Integer.toString((Integer)status.get("statusCode")).equalsIgnoreCase(Constants.API_SUCCESS_CODE)){
                                if(jobApplicantsDetails!=null&&jobApplicantsDetails.length()>0){
                                    for (int i = 0; i < jobApplicantsDetails.length(); i++) {
                                        // create a JSONObject for fetching single listing data
                                        JSONObject jobApplicantDetail = jobApplicantsDetails.getJSONObject(i);
                                        JobApplicantsModel jobApplicant = new JobApplicantsModel();
                                        // fetch variables and store it in JobListing object
                                        jobApplicant.setJobId(Long.parseLong(jobApplicantDetail.getString("jobId")));
                                        jobApplicant.setId(Long.parseLong(jobApplicantDetail.getString("applicantId")));
                                        jobApplicant.setDescription(jobApplicantDetail.getString("description"));
                                        getUserInfo(jobApplicant);
                                    }

                                    jobApplicantTitle.setText("Applicants" + "("+jobApplicantsDetails.length()+")");
                                    if(jobApplicantsDetails.length()==0){
                                        Toast.makeText(JobApplicantsActivity.this, "No Applicants Applied Yet.",Toast.LENGTH_LONG).show();
                                    }

                                }else{
                                    //TODO: set no records found
                                    log.info(this.getClass().getSimpleName()+ ": No Records Found.");
                                }
                            }else{
                                Toast.makeText(getApplicationContext(), "Failed to Get Applicants", Toast.LENGTH_LONG).show();
                                errorDialog(": Status Code is "+status.get("statusCode")+" | "+status);
                            }
                        } catch (JSONException e) {
                            errorDialog(": Response Error - "+e.toString());
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
        System.out.println("sizeee:"+jobApplicants.size());
    }

    private void getUserInfo(JobApplicantsModel job_Applicant) {
        String url =iamUrl+"/userProfile?userId="+job_Applicant.getId();

        JsonObjectRequest jsonAObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject userInfoObject = response.getJSONObject("data");
                            System.out.println("response:"+response);
                            JSONObject status = response.getJSONObject("status");
                            if(Integer.toString((Integer)status.get("statusCode")).equalsIgnoreCase(Constants.API_SUCCESS_CODE)){
                                List<String> errors = new ArrayList<String>();
                                if(userInfoObject!=null||userInfoObject.length()>0){
                                    JSONObject userDetail = userInfoObject;
                                    UserModel user = new UserModel();
                                    // fetch variables and store it in JobListing object
                                    user.setFirstName(CommonUtils.removeSpecialCharacters(userDetail.getString("firstName")));
                                    user.setLastName(CommonUtils.removeSpecialCharacters(userDetail.getString("lastName")));
                                    user.setAboutMe(CommonUtils.removeSpecialCharacters(userDetail.getString("aboutMe")));
                                    if(CommonUtils.isContactNo(userDetail.getString("contactNo"))){
                                        user.setContactNo(userDetail.getString("contactNo"));
                                    }else{
                                        errors.add("Invalid contact number");
                                    }

                                    Timestamp stamp = new Timestamp(Long.parseLong(String.valueOf(userDetail.getString("dob"))));
                                    Date date = new Date(stamp.getTime());
                                    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                                    String strDate = dateFormat.format(date);
                                    user.setDOB(strDate);
                                    if(CommonUtils.isGender(userDetail.getString("gender"))){
                                        user.setGender(userDetail.getString("gender"));
                                    }else{
                                        errors.add("Invalid gender");
                                    }

                                    user.setSkills(CommonUtils.removeSpecialCharacters(userDetail.getString("skills")));
                                    user.setProfessionalTitle(CommonUtils.removeSpecialCharacters(userDetail.getString("professionalTitle")));
                                    user.setLinkedInAcct(CommonUtils.removeSpecialCharacters(userDetail.getString("linkedInAcct")));
                                    if(errors.isEmpty()) {
                                        job_Applicant.setUser(user);
                                        jobApplicants.add(job_Applicant);
                                    }else{
                                        String listOfErrors = TextUtils.join(",", errors);
                                        errorDialog(": " +listOfErrors);
                                    }

                                }else{
                                    //TODO: set no records found
                                    errorDialog(": No Records Found.");
                                }
                            }else{
                                Toast.makeText(getApplicationContext(), "Failed to Get Applicant Information!", Toast.LENGTH_LONG).show();
                                errorDialog(": Status Code is "+status.get("statusCode")+" | "+status);
                            }


                        } catch (JSONException e) {
                            errorDialog(": Exception - "+e.toString());
                        }

                        applicantsAdapter=new ApplicantsAdapter(JobApplicantsActivity.this, jobApplicants);
                        recyclerView.setAdapter(applicantsAdapter);


                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("response:"+error.toString());
                        //Toast.makeText(JobListingDetailsActivity.this, error.toString(),Toast.LENGTH_LONG).show();
                        // TODO: Handle error
                        errorDialog(": Response Error - "+error.toString());
                    }
                });

        // Add a request (in this example, called stringRequest) to your RequestQueue.
        MySingleton.getInstance(this).addToRequestQueue(jsonAObjectRequest);
    }
    private void errorDialog(String error){
        log.error(this.getClass().getSimpleName()+ error);
        FragmentManager fm = getSupportFragmentManager();
        ErrorDialogFragment alertDialog = ErrorDialogFragment.newInstance(Constants.ERROR_RESPONSE_MSG);
        alertDialog.show(fm, "error_alert");
    }

}