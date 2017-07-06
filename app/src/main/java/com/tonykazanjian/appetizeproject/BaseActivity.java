package com.tonykazanjian.appetizeproject;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

/**
 * @author Tony Kazanjian
 */

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // Shows dialog when user denies app permissions twice to explain consequences. Cannot be set in BaseActivity
    // descendants because the dialog must be where the AppCompat theme is applied.
    public void showDialogForPermissionDenial(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton(getString(R.string.permission_denial_positive_action), okListener)
                .setNegativeButton(getString(R.string.denial_negative_action), okListener)
                .create()
                .show();
    }
}
