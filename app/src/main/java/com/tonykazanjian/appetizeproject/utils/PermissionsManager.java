package com.tonykazanjian.appetizeproject.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.tonykazanjian.appetizeproject.BaseActivity;
import com.tonykazanjian.appetizeproject.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tony Kazanjian
 */

public class PermissionsManager {

    private Context mContext;

    public PermissionsManager(Context context) {
        mContext = context;
    }

    public void permissionsDenied(String dialogText, final int requestId, final String permission) {
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext, permission)) {
            ((BaseActivity)mContext).showDialogForPermissionDenial(dialogText,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    dialog.dismiss();
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    if (permission.equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                                        checkStoragePermission(requestId);
                                    }
                                    break;
                            }
                        }
                    });
        }
        //permission is denied (and never ask again is  checked)
        //shouldShowRequestPermissionRationale will return false
        else {
            Toast.makeText(mContext, R.string.enable_permissions, Toast.LENGTH_LONG)
                    .show();
        }
    }

    public boolean checkStoragePermission(int requestId) {
        int galleryPermission = ContextCompat.checkSelfPermission(mContext,Manifest.permission.READ_EXTERNAL_STORAGE);
        List<String> listPermissionNeeded = new ArrayList<>();
        if (galleryPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (!listPermissionNeeded.isEmpty()){
            ActivityCompat.requestPermissions((Activity)mContext, listPermissionNeeded
                    .toArray(new String[listPermissionNeeded.size()]), requestId);
            return false;
        }
        return true;
    }
}
