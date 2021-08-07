package com.anoxsoftech.scannerbuddy;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Toast;

import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDPage;

import org.w3c.dom.Document;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


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
        File file = getOutputFile();
        if (file != null) {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                PdfDocument pdfDocument = new PdfDocument();

                for (int i = 0; i < imageUris.size(); i++) {
                    InputStream inStream = getContentResolver().openInputStream(imageUris.get(i));
                    Bitmap bitmap = BitmapFactory.decodeStream(inStream);
                    PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), (i + 1)).create();
                    PdfDocument.Page page = pdfDocument.startPage(pageInfo);
                    Canvas canvas = page.getCanvas();
                    Paint paint = new Paint();
                    paint.setColor(Color.BLUE);

                    bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(),bitmap.getHeight(), true);
                    canvas.drawPaint(paint);
                    canvas.drawBitmap(bitmap, 0f, 0f, null);
                    pdfDocument.finishPage(page);
                    bitmap.recycle();
                }
                pdfDocument.writeTo(fileOutputStream);
                pdfDocument.close();
                Toast.makeText(this, "PDf Created", Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private File getOutputFile() {
        File root = new File(Environment.getExternalStorageDirectory(),"MY PDF Folder");

        boolean isFolderCreated = true;

        if (!root.exists()) {
            isFolderCreated = root.mkdir();
        }

        if (isFolderCreated) {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
            String imageFileName = "PDF_" + timeStamp;

            return new File(root, imageFileName + ".pdf");
        } else {
            Toast.makeText(this, "Folder is not created", Toast.LENGTH_SHORT).show();
            return null;
        }
    }
}