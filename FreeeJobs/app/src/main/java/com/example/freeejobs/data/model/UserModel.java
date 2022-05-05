package com.example.freeejobs.data.model;

import java.io.Serializable;
import java.util.Date;

public class UserModel implements Serializable {

    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private String contactNo;
    private String gender;
    private String professionalTitle;
    private String aboutMe;
    private String aboutMeClient;
    private String skills;
    private String linkedInAcct;
    private String dob;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getContactNo() {
        return contactNo;
    }

    public String getGender() {
        return gender;
    }

    public String getProfessionalTitle() {
        return professionalTitle;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public String getAboutMeClient() {
        return aboutMeClient;
    }

    public String getSkills() {
        return skills;
    }

    public String getLinkedInAcct() {
        return linkedInAcct;
    }

    public String getDOB() {
        return dob;
    }

    public void setDOB(String dob) {
        this.dob = dob;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setProfessionalTitle(String professionalTitle) {
        this.professionalTitle = professionalTitle;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    public void setAboutMeClient(String aboutMeClient) {
        this.aboutMeClient = aboutMeClient;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public void setLinkedInAcct(String linkedInAcct) {
        this.linkedInAcct = linkedInAcct;
    }
}

