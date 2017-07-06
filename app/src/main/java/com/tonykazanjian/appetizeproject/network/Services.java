package com.tonykazanjian.appetizeproject.network;

import android.content.Context;

import com.cloudrail.si.CloudRail;
import com.cloudrail.si.services.Box;
import com.cloudrail.si.interfaces.CloudStorage;
import com.cloudrail.si.services.Dropbox;
import com.tonykazanjian.appetizeproject.utils.Constants;

/**
 *
 * This class encapsulates the different services being used by the application. It also initializes
 * them and persists the authentication data.
 *
 * @author Tony Kazanjian
 */

public class Services {

    public static final int DROPBOX_UPLOAD = 0;
    public static final int BOX_UPLOAD = 1;
    private static final Services sServiceInstance = new Services();

    private Dropbox mDropbox;
    private Box mBox;

    public static Services getInstance() {
        return sServiceInstance;
    }

    private Services() {
    }

    public void prepare(Context context){
        CloudRail.setAppKey(Constants.CLOUDRAILS_LICENSE_KEY);
        mDropbox = new Dropbox(context,
                Constants.DROPBOX_KEY, Constants.DROPBOX_SECRET);
        mBox = new Box(context, Constants.BOX_KEY, Constants.BOX_SECRET);
    }

    public CloudStorage getService(int serviceType) throws Exception {
        CloudStorage service;
        switch (serviceType) {
            case DROPBOX_UPLOAD:
                service = mDropbox;
                break;
            case BOX_UPLOAD:
                service = mBox;
                break;
            default:
                throw new Exception("No service available");
        }
        return service;
    }
}
