package com.tonykazanjian.appetizeproject.homescreen;

import com.cloudrail.si.interfaces.CloudStorage;

/**
 * @author Tony Kazanjian
 */

interface MainActivityView {
    void onPhotoSelected(String uri);
    void onUploadClicked(CloudStorage storagePlatform, String fileName);
    void onUploadResult(String title, String message);
    void onProgressDialogShown(boolean showProgress);
}
