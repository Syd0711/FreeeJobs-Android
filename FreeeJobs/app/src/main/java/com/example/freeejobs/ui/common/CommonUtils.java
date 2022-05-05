package com.example.freeejobs.ui.common;

import java.util.regex.Pattern;

public class CommonUtils {

    public static boolean isNumber(String value){
        return String.valueOf(value).matches("[0-9]+");
    }
    public static String removeSpecialCharacters(String before){
        Pattern special = Pattern.compile("");
        String after =before.replaceAll("[%&;*()+=|<>{}\\[\\]]","");
        return after;
    }
    public static boolean isRateType(String value){
        if(value.equalsIgnoreCase("Per Hour")||value.equalsIgnoreCase("Per Job")){
            return true;
        }else{
            return false;
        }
    }

    public static boolean isContactNo(String contactNo) {
        String regexPattern = "^(?:\\\\+65)?[689][0-9]{7}$";
        return String.valueOf(contactNo).matches(regexPattern);
    }
    public static boolean isEmailAdd(String email) {
        String regexPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        return String.valueOf(email).matches(regexPattern);
    }
    public static boolean isGender(String gender) {
        if(gender.equalsIgnoreCase("FEMALE")||gender.equalsIgnoreCase("MALE")) {
            return true;
        }else {
            return false;
        }
    }
}
