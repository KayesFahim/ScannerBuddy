package com.anoxsoftech.scannerbuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;


public class ViewPDF extends AppCompatActivity {

    ArrayList<Uri> list;
    ArrayAdapter<Uri> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pdf);

        Intent intent = new Intent();

        ArrayList<Uri> arrayList = intent.getParcelableArrayListExtra("multipleImage");

        ListView listView = findViewById(R.id.imageList);

        list = new ArrayList<>();
        adapter = new ArrayAdapter<Uri>(this, R.layout.listview_layout, R.id.imageView, list);

        for(int i=0; i < arrayList.size(); i++){
            list.add(arrayList.get(i));
        }


        listView.setAdapter(adapter);
    }
}