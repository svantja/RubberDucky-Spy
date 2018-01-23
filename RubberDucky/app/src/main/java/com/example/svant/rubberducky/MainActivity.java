package com.example.svant.rubberducky;

import android.content.ClipData;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.MemoryFile;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.Manifest;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.ByteArrayOutputStream;
import java.util.List;

import  android.widget.ImageView;
import com.google.android.gms.common.api.GoogleApiClient;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class MainActivity extends AppCompatActivity {
    private GoogleApiClient mGoogleApiClient;
    private String FOLDER_NAME = "xTests6";
    private static final String TAG = "<< DRIVE >>";
    ImageView targetImage;
    int PICK_IMAGE_MULTIPLE = 1;
    String imageEncoded;
    List<String> imagesEncodedList;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button button = findViewById(R.id.button);
        targetImage = (ImageView)findViewById(R.id.targetimage);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check if permissions were already granted
                if (Build.VERSION.SDK_INT >= 23) {
                    int result = ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE);
                    System.out.println(result + "Result");
                    // permissions were already granted
                    if(result == PackageManager.PERMISSION_GRANTED){
                        System.out.println("Permission Granted");

                    }else{ // permissions have to be granted!
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                PICK_IMAGE_MULTIPLE);

                    }
                }else { // Permissions must have been set while installing
                    int result = ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE);
                    if(result == PackageManager.PERMISSION_GRANTED){
                        System.out.println("Permission Granted");

                    }else{ // permissions have to be granted!
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                PICK_IMAGE_MULTIPLE);

                    }
                }

                getAllImagesPath();

            }
        });

    }

    public void getAllImagesPath(){
        Boolean isSDPresent = android.os.Environment.getExternalStorageState().
                equals(android.os.Environment.MEDIA_MOUNTED);
        // is SD card is present, list all images from sd card
        if(isSDPresent){
            final String[] columns = { MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media._ID };
            final String orderBy = MediaStore.Images.Media._ID;
            //Stores all the images from the gallery in Cursor
            Cursor cursor = getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
                    null, orderBy);
            //Total number of images
            int count = cursor.getCount();

            //Create an array to store path to all the images
            String[] arrPath = new String[count];

            for (int i = 0; i < count; i++) {
                cursor.moveToPosition(i);
                int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                //Store the path of the image
                arrPath[i]= cursor.getString(dataColumnIndex);
                Log.i("PATH", arrPath[i]);
            }
            createJSON(cursor);
            // The cursor should be freed up after use with close()
            cursor.close();
        }

        // list all images from internal storage
        final String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID };
        final String orderBy = MediaStore.Images.Media._ID;
        //Stores all the images from the gallery in Cursor
        Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.INTERNAL_CONTENT_URI, columns, null,
                null, orderBy);
        //Total number of images
        int count = cursor.getCount();

        //Create an array to store path to all the images
        String[] arrPath = new String[count];

        for (int i = 0; i < count; i++) {
            cursor.moveToPosition(i);
            int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            //Store the path of the image
            arrPath[i]= cursor.getString(dataColumnIndex);
            Log.i("PATH", arrPath[i]);
        }
        // The cursor should be freed up after use with close()
        cursor.close();
    }

    public void createJSON(Cursor cursor){
        JSONArray list = new JSONArray();
        int count = cursor.getCount();
        for (int i = 0; i < count; i++){
            cursor.moveToPosition(i);
            int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            byte[] imageArr = cursor.getBlob(dataColumnIndex);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageArr, 0, imageArr.length);
            String encodedImage = getStringFromBitMap(bitmap);
            try {
                JSONObject jsonObj = new JSONObject("{\"image\":\" + encodedImage + \"}");
                list.put(jsonObj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    System.out.println("---------------" + list);
    }

    public String getStringFromBitMap(Bitmap bitmapPicture){
/*
 * This functions converts Bitmap picture to a string which can be
 * JSONified.
 * */
        final int COMPRESSION_QUALITY = 100;
        String encodedImage;
        ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
        bitmapPicture.compress(Bitmap.CompressFormat.JPEG, COMPRESSION_QUALITY,
                byteArrayBitmapStream);
        byte[] b = byteArrayBitmapStream.toByteArray();
        encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encodedImage;
    }

}

