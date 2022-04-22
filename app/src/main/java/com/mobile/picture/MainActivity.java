package com.mobile.picture;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
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

    private boolean hasCameraHardware(Context context){
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)){
            return true;
        }else{
            return false;
        }
    }

    private boolean hasCameraPermissions(Context context){
        if (context.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            Log.e("Take Photo", "PERMISSION DENIED");
            return false;
        }
        return true;
    }
}