package com.mobile.picture;

import static androidx.core.content.FileProvider.getUriForFile;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.content.Intent;
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
    private ActivityResultLauncher<Intent> actResLauncherTakePhoto;
    private ActivityResultLauncher<Intent> actResLauncherSelectPhoto;

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
        String root = getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString();
        String imageFolderPath = root;
        File imagesFolder = new File(imageFolderPath);

        if(imagesFolder.mkdirs()){
            Log.d("Take Photo", imagesFolder + " created.");
        } else {
            Log.d("Take Photo", imagesFolder + " NOT created.");
        }

        File image = new File(Context., "my_images");
        File newFile = new File(image, "default_image.jpg");
        Uri fileUri = getUriForFile(Context , "com.mobile.picture", newFile);

        //File image = new File(imageFolderPath, "TempPhoto.jpg");
        image.delete();
        //Uri fileUri = getUriForFile(this, "com.mobile.picture", image);
        actResLauncherTakePhoto = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            switch (result.getResultCode()) {
                case RESULT_OK:
                    //for (int k = 1; k <= 6; k++) {
                    try {
                        final Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), fileUri);
                        if (bitmap != null) {
                            runOnUiThread(() -> imageViewPhoto.setImageURI(fileUri));
                            break;
                        } else {
                            Thread.sleep(500);
                        }
                    } catch (Exception e) {
                        //e.printStackTrace();
                    }
                    //}
                    break;
                case RESULT_CANCELED:
                    Log.e("Take Photo", "Result Cancel");
                    break;
                default:
            }
        });

        btnTakePhoto.setOnClickListener(v -> {
            if(hasCameraHardware(this)) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                if (intent.resolveActivity(getPackageManager()) != null){
                    try {
                        actResLauncherTakePhoto.launch(intent);
                    }catch (Exception e) {
                        Toast.makeText(this, "Permission denied",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnSelectPhoto.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_PICK);
            if(intent.resolveActivity(getPackageManager()) != null) {
                try {
                    actResLauncherSelectPhoto.launch(intent);
                } catch (Exception e) {
                    Toast.makeText(this, "Permission denied",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "There is no app that support this action",
                        Toast.LENGTH_SHORT).show();
            }
        });

        actResLauncherSelectPhoto = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            Log.e("Take Photo", "YYYYYY");
            Log.e("Take Photo", "Result = " + result);
            switch (result.getResultCode()) {
                case RESULT_OK:
                    Log.d("Take photo", "Select photo");
                    Uri selectedImageUri = result.getData().getData();
                    if(null != selectedImageUri) {
                        //update the preview image in the layout
                        runOnUiThread(() -> imageViewPhoto.setImageURI(selectedImageUri));
                    }
                    break;
            }
        });
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