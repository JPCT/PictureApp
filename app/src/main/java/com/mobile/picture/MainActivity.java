package com.mobile.picture;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private ImageView imageViewPhoto;
    private Button btnTakePhoto;
    private Button btnSelectPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initEvents();
    }

    public void initViews(){
        imageViewPhoto = findViewById(R.id.imgPhoto);
        btnTakePhoto = findViewById(R.id.butTakePhoto);
        btnSelectPhoto = findViewById(R.id.butSelectPhoto);
    }

    public void initEvents(){

    }
}