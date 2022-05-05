package com.example.freeejobs.ui.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.freeejobs.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ErrorDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ErrorDialogFragment extends DialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_MSG = "msg";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ErrorDialogFragment() {
        // Required empty public constructor
    }

    public static ErrorDialogFragment newInstance(String msg) {
        ErrorDialogFragment fragment = new ErrorDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MSG, msg);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String errorMsg = getArguments().getString(ARG_MSG);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("ERROR");
        alertDialogBuilder.setMessage(errorMsg);
        alertDialogBuilder.setPositiveButton("GO BACK",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // this gets the current activity.
                Activity currentActivity = getActivity();
                // this finish() method ends the current activity.
                currentActivity.finish();
            }
        });

        return alertDialogBuilder.create();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_MSG);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_error_dialog, container, false);
    }
}