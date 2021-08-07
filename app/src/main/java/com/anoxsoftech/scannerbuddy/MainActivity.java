package com.anoxsoftech.scannerbuddy;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Uri Imageuri;
    private static final int IMAGE_CAPTURE_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }
    public void check(View v){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permission, 401);
            }
            else
            {
                OpenCamera();
            }
        }
        else
        {
            OpenCamera();
        }
    }

    private void OpenCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"New Image");
        values.put(MediaStore.Images.Media.DESCRIPTION,"From Camera");
        Imageuri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);


        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Imageuri);
        startActivityForResult(cameraIntent,IMAGE_CAPTURE_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode ==RESULT_OK){
           Intent i = new Intent(this, ScanActivity.class);
            i.setData(Imageuri);
            startActivity(i);
        }

    }

    public void GalleryImage(View view) {
        startActivity(new Intent(this, GalleryImage.class));
    }
}