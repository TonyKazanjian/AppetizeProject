package com.tonykazanjian.appetizeproject.homescreen;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import com.cloudrail.si.interfaces.CloudStorage;
import com.tonykazanjian.appetizeproject.R;

/**
 * @author Tony Kazanjian
 *
 * This class is for extracting the business logic out of the Activity. This results in a cleaner, more modular,
 * and testable codebase.
 */

public class MainActivityPresenter {

    private MainActivityView mMainActivityView;
    private Context mContext;

    MainActivityPresenter(MainActivityView mainActivityView, Context context) {
        mMainActivityView = mainActivityView;
        mContext = context;
    }

    void displayPhoto(String uri){
        mMainActivityView.onPhotoSelected(uri);
    }

    void uploadToStorage(CloudStorage storagePlatform, Uri uri){
        if (uri != null) {
            mMainActivityView.onUploadClicked(storagePlatform, getFileName(uri));
        }
    }

    public void showProgressDialog(boolean isInProgress){
        mMainActivityView.onProgressDialogShown(isInProgress);
    }

    public void showUploadResult(boolean isSuccessful){
        String title;
        String message;
        if (isSuccessful) {
            title = mContext.getString(R.string.upload_success);
            message = mContext.getString(R.string.upload_success_message);
        } else {
            title = mContext.getString(R.string.upload_fail);
            message = mContext.getString(R.string.upload_fail_message);
        }
        mMainActivityView.onUploadResult(title, message);
    }

    // Extracts proper name of file for upload
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = mContext.getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
        }
        return "/" + result;
    }
}
