package com.anoxsoftech.scannerbuddy;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.microedition.khronos.opengles.GL;


public class GalleryImage extends AppCompatActivity {

    Button createpdfBtn;
    ImageSwitcher imageSwitcher;
    private ArrayList<Uri> imageUris;
    private static final int PIC_IMAGE_CODE = 0;
    int position = 0;
    ProgressDialog progressDialog;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_image);

        imageSwitcher = findViewById(R.id.imageswitch);
        createpdfBtn = findViewById(R.id.createPdf);


        imageUris = new ArrayList<>();

        imageSwitcher.setFactory(() -> new ImageView(getApplicationContext()));
    }


    public void prevBtn(View view) {
        if (position > 0) {
            position--;
            imageSwitcher.setImageURI(imageUris.get(position));
        } else {
            Toast.makeText(this, "No previous Image", Toast.LENGTH_SHORT).show();
        }

    }

    public void nextBtn(View view) {
        if (position < imageUris.size() - 1) {
            position++;
            imageSwitcher.setImageURI(imageUris.get(position));
        } else {
            Toast.makeText(this, "No More Image", Toast.LENGTH_SHORT).show();
        }

    }

    public void openGalleryBtn(View view) {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ||
                checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
            requestPermissions(permission, 401);
        } else {
            OpenGallery();
        }

    }

    private void OpenGallery() {
        Intent i = new Intent();
        i.setType("image/*");
        i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Select Image(s)"), PIC_IMAGE_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PIC_IMAGE_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        imageUris.add(imageUri);
                    }

                } else {
                    Uri imageuri = data.getData();
                    imageUris.add(imageuri);
                }
                imageSwitcher.setImageURI(imageUris.get(0));
                position = 0;
            }
        }
    }

    public void CreatePDF(View view) {


            try {

                PdfDocument pdfDocument = new PdfDocument();

                for (int i = 0; i < imageUris.size(); i++) {
                    InputStream inStream = getContentResolver().openInputStream(imageUris.get(i));
                    Bitmap original = BitmapFactory.decodeStream(inStream);
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    original.compress(Bitmap.CompressFormat.JPEG, 10, out);
                    Bitmap bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));


                    PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), (i + 1)).create();
                    PdfDocument.Page page = pdfDocument.startPage(pageInfo);
                    Canvas canvas = page.getCanvas();
                    Paint paint = new Paint();
                    paint.setColor(Color.WHITE);
                    bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);
                    canvas.drawPaint(paint);
                    canvas.drawBitmap(bitmap, 0, 0, null);
                    pdfDocument.finishPage(page);
                    bitmap.recycle();
                }
                if (ContextCompat.checkSelfPermission(GalleryImage.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(GalleryImage.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    } else {
                        // No explanation needed; request the permission
                        ActivityCompat.requestPermissions(GalleryImage.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                1000);
                    }
                } else {
                    //Save PDF file

                    //Create time stamp
                    Date date = new Date() ;
                    @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("_yyyy_MM_dd_HH_mm_ss").format(date);



                    String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +"/"+  "ScannerBuddy_" +timeStamp+ ".pdf";

                    File myFile = new File(filePath);

                    try {
                        OutputStream output = new FileOutputStream(myFile);
                        pdfDocument.writeTo(output);
                        output.flush();
                        output.close();
                        Toast.makeText(GalleryImage.this, "PDF Created Succesfully", Toast.LENGTH_LONG).show();
                        Toast.makeText(GalleryImage.this, "PDF Saved On Memory", Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                pdfDocument.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
}

