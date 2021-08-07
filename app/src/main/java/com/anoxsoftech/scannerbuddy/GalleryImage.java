package com.anoxsoftech.scannerbuddy;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import java.util.ArrayList;

public class GalleryImage extends AppCompatActivity {

    ImageSwitcher imageSwitcher;
    private ArrayList<Uri> imageUris;
    private static final int PIC_IMAGE_CODE = 0;
    int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_image);

        imageSwitcher = findViewById(R.id.imageswitch);

        imageUris = new ArrayList<>();

        imageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {

                ImageView imageView = new ImageView(getApplicationContext());
                return imageView;
            }
        });
    }


    public void prevBtn(View view) {
        if(position > 0){
            position--;
            imageSwitcher.setImageURI(imageUris.get(position));
        }else{
            Toast.makeText(this, "No previous Image", Toast.LENGTH_SHORT).show();
        }

    }

    public void nextBtn(View view) {
        if(position < imageUris.size() -1){
            position++;
            imageSwitcher.setImageURI(imageUris.get(position));
        }else{
            Toast.makeText(this, "No More Image", Toast.LENGTH_SHORT).show();
        }

    }

    public void openGalleryBtn(View view) {
        Intent i = new Intent();
        i.setType("image/*");
        i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Select Image(s)"),PIC_IMAGE_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PIC_IMAGE_CODE){
            if(resultCode== Activity.RESULT_OK){
                if (data.getClipData() != null){
                    int count = data.getClipData().getItemCount();
                    for(int i=0; i <count ; i++){
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        imageUris.add(imageUri);
                    }
                    imageSwitcher.setImageURI(imageUris.get(0));
                    position = 0;

                }else{
                    Uri imageuri = data.getData();
                    imageUris.add(imageuri);
                    imageSwitcher.setImageURI(imageUris.get(0));
                    position = 0;
                }
            }
        }
    }

    public void CreatePDF(View view) {

    }
}