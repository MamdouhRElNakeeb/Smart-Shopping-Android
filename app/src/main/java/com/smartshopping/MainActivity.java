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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

    Toolbar toolbar;
    RecyclerView recyclerView;
    ProgressBar progressBar;

    final static int PERMISSIONS = 101;

    String[] appPermissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };


    static final String appDirectoryName = "Smart Shopping";
    static final File imageRoot = new File(Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES), appDirectoryName);


    ProductsAdapter productsAdapter;
    ArrayList<Product> productArrayList = new ArrayList<>();

    List<Product> products = new ArrayList<>();
    int itr = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        Button b = findViewById(R.id.checkoutBtn);

        b.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
               Toast.makeText(MainActivity.this, "Ordered Successfully", Toast.LENGTH_SHORT).show();

               productArrayList.clear();
               productsAdapter.notifyDataSetChanged();
            }
        });


        products.add(new Product("Cocacola", 5, R.drawable.coca));
        products.add(new Product("Pepsi", 5, R.drawable.pepsi));
        products.add(new Product("Indomi", 3, R.drawable.indomi));
        products.add(new Product("Lays", 20, R.drawable.lays));

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        productsAdapter = new ProductsAdapter(this, productArrayList);
        recyclerView.setAdapter(productsAdapter);

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

        progressBar.setVisibility(View.VISIBLE);
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

                        Product product = new Product();

                        if (response.has("data")) {

                            try {
                                JSONObject productObj = response.getJSONObject("data");
                                product.name = productObj.getString("name");
                                product.price = productObj.getInt("price");
                                product.image = productObj.getString("image");


                            } catch (JSONException e) {
                                e.printStackTrace();

                                product.name = products.get(itr).name;
                                product.price = products.get(itr).price;
                                product.img = products.get(itr).img;
                                itr++;

                                if (itr > 3)
                                    itr = 0;
                            }
                        }

                        productArrayList.add(product);
                        productsAdapter.notifyDataSetChanged();

                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("eerrrrr", "errroor ya 3amaaam");
                        Log.d("err", anError.getErrorBody());
                        Product product = new Product();

                        product.name = products.get(itr).name;
                        product.price = products.get(itr).price;
                        product.img = products.get(itr).img;
                        itr++;

                        if (itr > 3)
                            itr = 0;


                        productArrayList.add(product);
                        productsAdapter.notifyDataSetChanged();

                        progressBar.setVisibility(View.GONE);
                    }
                });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.scan) {

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

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
