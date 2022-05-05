package com.example.freeejobs.ui.jobListing;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.freeejobs.MySingleton;
import com.example.freeejobs.R;
import com.example.freeejobs.constant.Constants;
import com.example.freeejobs.data.model.JobApplicantsModel;
import com.example.freeejobs.data.model.JobListingModel;
import com.example.freeejobs.ui.common.CommonUtils;
import com.example.freeejobs.ui.common.ErrorDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

public class ApplicantDetailsActivity extends AppCompatActivity {

    FloatingActionButton backButton;
    Button acceptbutton;
    Button rejectbutton;
    TextView firstName;
    TextView lastName;
    TextView contactNo;
    TextView gender;
    TextView professionalTitle;
    TextView aboutMe;
    TextView skills;
    TextView linkedInAcct;
    TextView dob;
    TextView description;
    JobListingModel listing;
    private long userId;

    final String jobListingUrl = Constants.freeeJobsURL+Constants.jobListingAPIPath;
    final String jobAppUrl = Constants.freeeJobsURL+Constants.jobApplicationAPIPath;
    Logger log = LoggerFactory.getLogger(ApplicantDetailsActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applicant_details);
        backButton = findViewById(R.id.ApplicantDetailsBackButton);
        acceptbutton = findViewById(R.id.ApplicantDetailsAcceptButton);
        rejectbutton = findViewById(R.id.ApplicantDetailsRejectButton);
        firstName = findViewById(R.id.FirstNameValueTextView);
        lastName = findViewById(R.id.LastNameValueTextView);
        contactNo = findViewById(R.id.ContactNoValueTextView);
        gender = findViewById(R.id.GenderValueTextView);
        professionalTitle = findViewById(R.id.ProfTitleValueTextView);
        aboutMe = findViewById(R.id.AboutMeValueTextView);
        skills = findViewById(R.id.SkillsValueTextView);
        dob = findViewById(R.id.DOBValueTextView);
        linkedInAcct = findViewById(R.id.LinkedInAcctValueTextView);
        description = findViewById(R.id.DescriptionValueTextView);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
//            long jobListingId = extras.getLong("jobListingId");
//            userId = extras.getLong("userId");
//            userId = extras.getLong("userId");
            JobApplicantsModel applicant = (JobApplicantsModel) extras.getSerializable("applicantInfo");
            userId = applicant.getId();
            getApplicantDetails(applicant);

        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        rejectbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JobApplicantsModel applicant = (JobApplicantsModel) extras.getSerializable("applicantInfo");
                setApplicationStatus(applicant, "R");
            }
        });

        acceptbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JobApplicantsModel applicant = (JobApplicantsModel) extras.getSerializable("applicantInfo");
                setApplicationStatus(applicant, "A");
            }
        });

    }

    private void getApplicantDetails(JobApplicantsModel applicant) {

        List<String> errors = new ArrayList<String>();

        firstName.setText(CommonUtils.removeSpecialCharacters(applicant.getUser().getFirstName()));
        lastName.setText(CommonUtils.removeSpecialCharacters(applicant.getUser().getLastName()));
        if(CommonUtils.isContactNo(applicant.getUser().getContactNo())){
            contactNo.setText(applicant.getUser().getContactNo());
        }else{
            errors.add("Invalid contact number");
        }
        if(CommonUtils.isGender(applicant.getUser().getGender())){
            gender.setText(applicant.getUser().getGender());
        }else{
            errors.add("Invalid gender");
        }

        professionalTitle.setText(CommonUtils.removeSpecialCharacters(applicant.getUser().getProfessionalTitle()));
        aboutMe.setText(CommonUtils.removeSpecialCharacters(applicant.getUser().getAboutMe()));
        skills.setText(CommonUtils.removeSpecialCharacters(applicant.getUser().getSkills()));
        linkedInAcct.setText(CommonUtils.removeSpecialCharacters(applicant.getUser().getLinkedInAcct()));
//        Timestamp stamp = new Timestamp(Long.parseLong(String.valueOf(applicant.getUser().getDOB())));
//        Date date = new Date(stamp.getTime());
//        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
//        String strDate = dateFormat.format(date);
        dob.setText(applicant.getUser().getDOB());
        description.setText(CommonUtils.removeSpecialCharacters(applicant.getDescription()));
        if(!errors.isEmpty()) {
            String listOfErrors = TextUtils.join(",", errors);
            errorDialog(": " +listOfErrors);
        }

    }

    private void setApplicationStatus(JobApplicantsModel applicant, String status) {
        String url =jobAppUrl+"/setAppStatus";
        JSONObject jsonObj = new JSONObject();

        try {
            jsonObj.put("jobId", applicant.getJobId());
            jsonObj.put("applicantId", applicant.getId());
            jsonObj.put("status",status);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONObject setApplicantStatusObject = response.getJSONObject("data");
                        System.out.println("responseApplyJob:"+response);
                        JSONObject status = response.getJSONObject("status");
                        if(Integer.toString((Integer)status.get("statusCode")).equalsIgnoreCase(Constants.API_SUCCESS_CODE)){
                            finish();
                        }else{
                            Toast.makeText(getApplicationContext(), "Failed to Reject or Accept Applicant", Toast.LENGTH_LONG).show();
                            errorDialog(": Status Code is "+status.get("statusCode")+" | "+status);
                        }


                    } catch (JSONException e) {
                        errorDialog(": Exception - "+e.toString());
                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("response:"+error.toString());
                    // TODO: Handle error
                    errorDialog(": Exception - "+error.toString());

                }
            });

            MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
        } catch (JSONException e) {
            errorDialog(": Exception - "+e.toString());
        }


    }

    private void errorDialog(String error){
        log.error(this.getClass().getSimpleName()+ error);
        FragmentManager fm = getSupportFragmentManager();
        ErrorDialogFragment alertDialog = ErrorDialogFragment.newInstance(Constants.ERROR_RESPONSE_MSG);
        alertDialog.show(fm, "error_alert");
    }
}