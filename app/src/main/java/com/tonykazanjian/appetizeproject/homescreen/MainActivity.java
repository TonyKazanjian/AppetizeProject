package com.tonykazanjian.appetizeproject.homescreen;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudrail.si.interfaces.CloudStorage;
import com.cloudrail.si.services.Box;
import com.cloudrail.si.services.Dropbox;
import com.squareup.picasso.Picasso;
import com.tonykazanjian.appetizeproject.BaseActivity;
import com.tonykazanjian.appetizeproject.network.Services;
import com.tonykazanjian.appetizeproject.network.UploadAsyncTask;
import com.tonykazanjian.appetizeproject.utils.PermissionsManager;
import com.tonykazanjian.appetizeproject.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends BaseActivity implements MainActivityView{

    private static final int REQUEST_ID_UPLOAD_STORAGE_PERMISSION = 1 ;
    private static final int SELECT_FILE = 2;

    /*** Layout vars ***/
    FrameLayout mImageFrameLayout;
    TextView mUploadTextView;
    ImageView mImageView;
    LinearLayout mDropboxUploadLl;
    LinearLayout mBoxUploadLl;

    /*** Data and Network vars ***/
    Services mUploadServices;
    UploadAsyncTask mUploadAsyncTask;
    Uri mPhotoUri;
    InputStream mInputStream;

    /*** Other vars ***/
    MainActivityPresenter mMainActivityPresenter;
    PermissionsManager mPermissionsManager;
    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMainActivityPresenter = new MainActivityPresenter(this,this);
        mPermissionsManager = new PermissionsManager(this);
        mUploadServices = Services.getInstance();
        mUploadServices.prepare(this);

        mImageFrameLayout = (FrameLayout) findViewById(R.id.imageFrameLayout);
        mImageView = (ImageView) findViewById(R.id.imageView);
        mUploadTextView = (TextView) findViewById(R.id.uploadTextView);
        mDropboxUploadLl = (LinearLayout) findViewById(R.id.dropbox_upload_btn);
        mBoxUploadLl = (LinearLayout) findViewById(R.id.box_upload_btn);

        mImageFrameLayout.setOnClickListener(new ImageClickListener());
        mDropboxUploadLl.setOnClickListener(new UploadClickListener());
        mBoxUploadLl.setOnClickListener(new UploadClickListener());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("photo uri", mPhotoUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mPhotoUri = savedInstanceState.getParcelable("photo uri");
        //Display photo again after orientation change or activity destruction
        if (mPhotoUri != null) {
            mMainActivityPresenter.displayPhoto(mPhotoUri.toString());
        }
    }

    @Override
    public void onPhotoSelected(String data) {
        Picasso.with(this).load(data).fit().centerCrop().into(mImageView);
        mUploadTextView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onUploadClicked(final CloudStorage storagePlatform, final String fileName) {
        mProgressDialog = getProgressDialog();
        try {
            mInputStream = getContentResolver().openInputStream(mPhotoUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mUploadAsyncTask = new UploadAsyncTask(mMainActivityPresenter, storagePlatform, fileName, mInputStream,
                new File(mPhotoUri.getPath()).length());
        mUploadAsyncTask.execute();
    }

    @Override
    public void onUploadResult(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create().show();
    }

    @Override
    public void onProgressDialogShown(boolean showProgress) {
        if (showProgress){
            mProgressDialog.show();
        } else {
            hideProgress(mProgressDialog);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == SELECT_FILE){
                mPhotoUri = data.getData();
                mMainActivityPresenter.displayPhoto(data.getData().toString());
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ID_UPLOAD_STORAGE_PERMISSION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Opens gallery right after user has granted permissions
                    startGalleryIntent();
                } else {
                    mPermissionsManager.permissionsDenied(getString(R.string.gallery_permissions_denied),
                            REQUEST_ID_UPLOAD_STORAGE_PERMISSION, Manifest.permission.READ_EXTERNAL_STORAGE);
                }
                break;
            }
        }
    }

    public void startGalleryIntent(){
        Intent intent = galleryIntent().setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private static Intent galleryIntent() {
        Intent intent = new Intent();
        // Selects only images from media storage
        intent.setType("image/*");
        return intent;
    }

    private ProgressDialog getProgressDialog(){
        ProgressDialog dialog = new ProgressDialog(this, R.style.AppAlertDialogTheme);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(getString(R.string.request_progress_message));
        dialog.setCancelable(false);
        return dialog;
    }

    public void hideProgress(ProgressDialog dialog) {
        if(dialog != null) {
            if(dialog.isShowing()) { // Makes sure Activity is active and showing dialog
                if (!this.isFinishing() && !this.isDestroyed()){
                    dialog.dismiss();
                }
            }
        }
    }

    /*** CLick Listeners ***/

    private class ImageClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            if (mPermissionsManager.checkStoragePermission(REQUEST_ID_UPLOAD_STORAGE_PERMISSION)){
                //opens gallery
                startGalleryIntent();
            }
        }
    }

    private class UploadClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            if (mPhotoUri != null) {
                switch (view.getId()) {
                    case R.id.dropbox_upload_btn:
                        Dropbox dropbox = null;
                        try {
                            dropbox = (Dropbox) mUploadServices.getService(Services.DROPBOX_UPLOAD);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        mMainActivityPresenter.uploadToStorage(dropbox, mPhotoUri);

                        break;
                    case R.id.box_upload_btn:
                        Box box = null;
                        try {
                            box = (Box) mUploadServices.getService(Services.BOX_UPLOAD);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        mMainActivityPresenter.uploadToStorage(box, mPhotoUri);
                        break;
                }
            } else {
                Toast.makeText(view.getContext(), R.string.missing_photo_toast_message, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
