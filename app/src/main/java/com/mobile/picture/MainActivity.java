package com.mobile.picture;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private ImageView imageViewPhoto;
    private Button btnTakePhoto;
    private Button btnSelectPhoto;
    private Uri fileUri;

    ActivityResultLauncher<Intent> actResLauncherTakePhoto;
    ActivityResultLauncher<Intent> actResLauncherSelectPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String root = getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString();

        String imageFolderPath = root;
        File imagesFolder = new File(imageFolderPath);

        if (imagesFolder.mkdirs()) {
            Log.d("Take Photo", imagesFolder + " created.");
        } else {
            Log.d("Take Photo", imagesFolder + " NOT created.");
        }

        File image = new File(imageFolderPath, "TempPhoto.jpg");
        fileUri = FileProvider.getUriForFile(this, "com.example.demotakephoto", image);

        initViews();
        initEvents();

        showPhoto(fileUri);
    }

    public void showPhoto(Uri fileUri) {
        try {
            final Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), fileUri);
            runOnUiThread(() -> imageViewPhoto.setImageURI(fileUri));
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    public void initViews(){
        imageViewPhoto = findViewById(R.id.imgPhoto);
        btnTakePhoto = findViewById(R.id.butTakePhoto);
        btnSelectPhoto = findViewById(R.id.butSelectPhoto);
    }

    public void initEvents() {
        ActivityResultContracts.StartActivityForResult actForRes = null;

        // Take photo from camera
        actForRes = new ActivityResultContracts.StartActivityForResult();
        actResLauncherTakePhoto = registerForActivityResult(actForRes, result -> {
            switch (result.getResultCode()) {
                case RESULT_OK:
                    showPhoto(fileUri);
                    break;
                case RESULT_CANCELED:
                    //Log.e("Take Photo","Result Cancel");
                    break;
                default:
                    //Log.e("Take Photo", "getResultCode = " + result.getResultCode());
                    break;
            }
        });

        btnTakePhoto.setOnClickListener(v -> {
            if (hasCameraHardware(this)) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if (hasCameraPermissions(this)){
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        try {
                            actResLauncherTakePhoto.launch(intent);
                        } catch (Exception e) {
                            AlertDialog.Builder alert = new AlertDialog.Builder(this)
                                    .setTitle(getString(R.string.Error))
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setMessage(getString(R.string.You_must_enable_permission_camera))
                                    .setPositiveButton(getString(R.string.OK), null);
                            alert.show();
                        }
                    } else {
                        AlertDialog.Builder alert = new AlertDialog.Builder(this)
                                .setTitle(getString(R.string.Error))
                                .setMessage(getString(R.string.There_is_no_app_that_support_this_action))
                                .setPositiveButton(getString(R.string.OK), null);
                        alert.show();
                    }}
                else{
                    AlertDialog.Builder alert = new AlertDialog.Builder(this)
                            .setTitle(getString(R.string.Error))
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setMessage(getString(R.string.You_must_enable_permission_camera))
                            .setPositiveButton(getString(R.string.OK), null);
                    alert.show();
                }
            } else {
                AlertDialog.Builder alert = new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.Info))
                        .setMessage(getString(R.string.Camera_not_detected))
                        .setPositiveButton(getString(R.string.OK), null);
                alert.show();
            }
        });

        // Select photo from gallery
        actForRes = new ActivityResultContracts.StartActivityForResult();
        actResLauncherSelectPhoto = registerForActivityResult(actForRes, result -> {
            switch (result.getResultCode()) {
                case RESULT_OK:
                    Uri selectedImageUri = result.getData().getData();
                    if (null != selectedImageUri) {
                        runOnUiThread(() -> imageViewPhoto.setImageURI(selectedImageUri));
                    }
                    break;
            }
        });

        btnSelectPhoto.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_PICK);
            if (intent.resolveActivity(getPackageManager()) != null) {
                try {
                    actResLauncherSelectPhoto.launch(intent);
                } catch (Exception e) {
                    Toast.makeText(this, "Permission denied",
                            Toast.LENGTH_SHORT).show();
                }
            }else {
                AlertDialog.Builder alert = new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.Error))
                        .setMessage(getString(R.string.There_is_no_app_that_support_this_action))
                        .setPositiveButton(getString(R.string.OK), null);
                alert.show();
            }
        });
    }

    private boolean hasCameraPermissions(Context context) {
        if (context.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Log.e("Take Photo", "PERMISSION DENIED");
            return false;
        }

        return true;
    }

    private boolean hasCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
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