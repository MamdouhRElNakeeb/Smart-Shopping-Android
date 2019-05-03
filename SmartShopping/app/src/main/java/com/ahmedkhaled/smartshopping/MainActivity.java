package com.ahmedkhaled.smartshopping;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class MainActivity extends AppCompatActivity
{

    final static int PERMISSIONS = 101;

    String[] appPermissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };


    static final String appDirectoryName = "Smart Shopping";
    static final File imageRoot = new File(Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES), appDirectoryName);



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button b = findViewById(R.id.scanButton);
        b.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                // Code here executes on main thread after user presses button
                checkAndRequestPermission();
                try
                {
                    //set time in mili
                    Thread.sleep(5000);

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                capturePhoto();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED)
        {
            return;
        }

        if (resultCode == RESULT_OK && requestCode == PERMISSIONS && data != null)
        {

            Bitmap bitmap = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
// Test if it return the data correctly
//            ImageView imageView = findViewById(R.id.barcodeImage);
//            imageView.setImageBitmap(bitmap);
            File file = createFile(bitmap);
            if(file.exists())
            {
// Test if it created the file correctly
//               Toast.makeText(this, "File is found!", Toast.LENGTH_SHORT).show();
//               Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
//               ImageView myImage = (ImageView) findViewById(R.id.barcodeImage);
//               myImage.setImageBitmap(myBitmap);


                // TODO
                // uploadImage(file);
            }

        }

    }

//---------------------------Functions----------------------------//

    public void capturePhoto()
    {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, PERMISSIONS);
    }

    public void checkAndRequestPermission()
    {
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String perm : appPermissions)
        {
            if (ContextCompat.checkSelfPermission(this,perm) != PackageManager.PERMISSION_GRANTED)
            {
                listPermissionsNeeded.add(perm);
            }
        }

        // Ask for non-granted permissions
        if (!listPermissionsNeeded.isEmpty())
        {
            ActivityCompat.requestPermissions(this,listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),PERMISSIONS);
        }

    }


    public File createFile(Bitmap bitmap)
    {
        imageRoot.mkdirs();
        final File image = new File(imageRoot, "image1.jpg");

        OutputStream os;
        try
        {
            os = new FileOutputStream(image);
            bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*Doesn't even matter*/, os);
            os.flush();
            os.close();
            return image;
        }
        catch (Exception e)
        {
            Log.i("Hamada Sokar Zyada", "Error writing bitmap", e);
        }
        return image;
    }


    public void uploadImage()
    {
        // TODO
    }
}
