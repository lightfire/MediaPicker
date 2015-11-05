package net.alhazmy13.mediapicker;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Alhazmy13 on 10/26/15.
 */
public class CameraActivity extends AppCompatActivity {
    private static final int CAMERA_REQUEST = 1888;
    String path;

    File destination;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;


    //// TODO: 10/28/15  fix the bug on pick multi image
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pickImageWrapper();
    }

    private String getRandonString(){
        return UUID.randomUUID().toString();
    }


    private void pickImage(){
        destination = new   File(Environment.getExternalStorageDirectory(),getRandonString()+".jpg");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(destination));
        startActivityForResult(intent, CAMERA_REQUEST);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            try {
                FileInputStream in = new FileInputStream(destination);
                BitmapFactory.Options options = new BitmapFactory.Options();
                //  options.inSampleSize = 10; //Downsample 10x
                Log.d("PP", " bitmap factory==========" + destination.getAbsolutePath());
                CameraPicker.onImagePicked.OnImagePicked(destination.getAbsolutePath().toString());
                Bitmap user_picture_bmp = BitmapFactory.decodeStream(in, null, options);

                //  userImage.setImageBitmap(user_picture_bmp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        finish();
    }



    private void pickImageWrapper() {
        if (Build.VERSION.SDK_INT >= 23) {
            List<String> permissionsNeeded = new ArrayList<String>();

            final List<String> permissionsList = new ArrayList<String>();
            if (!addPermission(permissionsList, Manifest.permission.CAMERA))
                permissionsNeeded.add("Camera");
            if (!addPermission(permissionsList, Manifest.permission.READ_EXTERNAL_STORAGE))
                permissionsNeeded.add("Read External Storage");
            if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                permissionsNeeded.add("Write External Storage");

            if (permissionsList.size() > 0) {
                if (permissionsNeeded.size() > 0) {
                    // Need Rationale
                    String message = "You need to grant access to " + permissionsNeeded.get(0);
                    for (int i = 1; i < permissionsNeeded.size(); i++)
                        message = message + ", " + permissionsNeeded.get(i);
                    showMessageOKCancel(message,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(CameraActivity.this,permissionsList.toArray(new String[permissionsList.size()]),
                                            REQUEST_CODE_ASK_PERMISSIONS);
                                }
                            });
                    return;
                }
                ActivityCompat.requestPermissions(CameraActivity.this,permissionsList.toArray(new String[permissionsList.size()]),
                        REQUEST_CODE_ASK_PERMISSIONS);
                return;
            }

            pickImage();
        }else{
            pickImage();
        }
    }
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(CameraActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
    private boolean addPermission(List<String> permissionsList, String permission) {
        if (ActivityCompat.checkSelfPermission(CameraActivity.this,permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            if (!ActivityCompat.shouldShowRequestPermissionRationale(CameraActivity.this,permission))
                return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
            {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for ACCESS_FINE_LOCATION
                if (perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    // All Permissions Granted
                    pickImage();
                } else {
                    // Permission Denied
                    Toast.makeText(CameraActivity.this, "Some Permission is Denied", Toast.LENGTH_SHORT)
                            .show();
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}