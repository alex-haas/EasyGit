package de.ahaas.easygit.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import de.ahaas.easygit.R;

/**
 * Created by Alex on 09.04.2016.
 */
public class PermissionHelper {
    private final static String TAG = PermissionHelper.class.getName();

    public static boolean isPermissionGranted(Activity activity, String permission){
        return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean arePermissionsGranted(Activity activity, String[] permissions){
        for(String permission : permissions){
            if(! isPermissionGranted(activity,permission)){
                return false;
            }
        }
        return true;
    }

    public static void executeOrAskForPermission(final DialogFragment requestingFragment, final Activity activity, final String[] permissions, final int requestCode, final Runnable method){
        for(String permission : permissions){
            Log.i(TAG, "Permission "+permission+" is granted: "+isPermissionGranted(activity, permission));
            if (! isPermissionGranted(activity, permission)) {
                Log.i(TAG, "Permission "+permission+" not ganted -> Check if the User needs Info about android 6 permissions");
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                    Log.i(TAG, "Permission was denied -> show Dialog with explanaition why the Permission is neccessary");
                    new AlertDialog.Builder(activity)
                            .setTitle(R.string.permission_dialog_title)
                            .setMessage(R.string.permission_dialog_text)
                            .setPositiveButton(R.string.permission_dialog_ok, (DialogInterface dialog, int which) -> {
                                    executeOrAskForPermission(requestingFragment, activity, permissions, requestCode, method);
                            })
                            .create()
                            .show();
                } else {
                    Log.i(TAG, "Asking directly for Permission to use "+permission+" with requestCode "+requestCode);
                    requestingFragment.requestPermissions(new String[]{permission}, requestCode);
                }
                return;
            }
        }

        method.run();
    }
}
