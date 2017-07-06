package com.tonykazanjian.appetizeproject.network;

import android.os.AsyncTask;
import android.util.Log;

import com.cloudrail.si.interfaces.CloudStorage;
import com.tonykazanjian.appetizeproject.homescreen.MainActivityPresenter;

import java.io.InputStream;

/**
 * @author Tony Kazanjian
 */

public class UploadAsyncTask extends AsyncTask<Void, Void, Boolean> {

    private MainActivityPresenter mMainActivityPresenter;
    private CloudStorage mCloudStorage;
    private String mFileName;
    private InputStream mInputStream;
    private long mFileSize;

    private boolean mIsSuccessful;

    public UploadAsyncTask(MainActivityPresenter mainActivityPresenter, CloudStorage cloudStorage, String fileName,
                           InputStream inputStream, long fileSize){
        mMainActivityPresenter = mainActivityPresenter;
        mCloudStorage = cloudStorage;
        mFileName = fileName;
        mInputStream = inputStream;
        mFileSize = fileSize;
    }


    @Override
    protected Boolean doInBackground(Void... voids) {
        mIsSuccessful = true;

        try {
            mCloudStorage.upload(mFileName, mInputStream, mFileSize, true);
        } catch (Exception e) {
            Log.i(this.getClass().getSimpleName(), e.getMessage());
            mIsSuccessful = false;
        }
        return true;
    }

    @Override
    protected void onPreExecute() {
        mMainActivityPresenter.showProgressDialog(true);
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        mMainActivityPresenter.showProgressDialog(false);
        if (mIsSuccessful){
            mMainActivityPresenter.showUploadResult(true);
        } else {
            mMainActivityPresenter.showUploadResult(false);
        }
    }

    @Override
    protected void onCancelled(Boolean aBoolean) {
        mMainActivityPresenter.showProgressDialog(false);
    }
}
