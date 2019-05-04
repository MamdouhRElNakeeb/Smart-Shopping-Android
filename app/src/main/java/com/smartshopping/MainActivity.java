package com.smartshopping;

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


import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.OkHttpResponseAndJSONObjectRequestListener;
import com.androidnetworking.interfaces.OkHttpResponseAndStringRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Response;


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
                    Thread.sleep(1000);

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
               Toast.makeText(this, "File is found!", Toast.LENGTH_SHORT).show();
               Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
               ImageView myImage = (ImageView) findViewById(R.id.barcodeImage);
               myImage.setImageBitmap(myBitmap);

               uploadImage(file);

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


    public void uploadImage(File file)
    {
        // TODO

        Log.d("fileee", file.getAbsolutePath());

        ANRequest.MultiPartBuilder multiPartBuilder = AndroidNetworking.upload("http://34.73.23.49:5000/upload");

        multiPartBuilder
                .addMultipartFile("image", file)
                .setTag("image")
                .setPriority(Priority.HIGH)
                .build()
                .setUploadProgressListener(new UploadProgressListener() {
                    @Override
                    public void onProgress(long bytesUploaded, long totalBytes) {
                        Log.d("progress", String.valueOf(bytesUploaded) + " / " + String.valueOf(totalBytes));
                    }
                })
                .getAsOkHttpResponseAndJSONObject(new OkHttpResponseAndJSONObjectRequestListener() {
                    @Override
                    public void onResponse(Response okHttpResponse, JSONObject response) {
                        Log.d("JSON", response.toString());
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("eerrrrr", "errroor ya 3amaaam");
                        Log.d("err", anError.getErrorBody());
                    }
                });

    }
}


//
//.getAsJSONArray(new JSONArrayRequestListener() {
//@Override
//public void onResponse(JSONArray response) {
//        Log.v("JSON","GOT RESPONSE");
//        }
//
//@Override
//public void onError(ANError anError) {
//
//        }
//        });