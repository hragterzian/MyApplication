package com.example.hrag.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;

import id.zelory.compressor.Compressor;

public class UploadClass extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback{
    private final String host="ftp.unaux.com";
    private final String user="unaux_22000503";
    private final String pass="2ct4b6fasok";
    private static final int REQUEST_WRITE_PERMISSION = 786;

private File comp;
    private static final int PICK_IMAGE = 100;
    private ImageView imageView;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_WRITE_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.image_view);
        Button pickImageButton = (Button) findViewById(R.id.pick_image_button);










        pickImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermission();
            }
        });

    }
    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
        } else {
            openGallery();
        }
    }
    private void openGallery() {
        Intent gallery =
                new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            Uri imageUri;
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
            String realUri=getRealPathFromUri(getApplicationContext(),imageUri);
            Log.d("uri", "onActivityResult: "+imageUri.getPath());

            Log.v("real ",realUri);
            Log.v("real ",realUri);
            try {
                File comThis = new File(realUri);
                 comp = new Compressor(this).compressToFile(comThis);
            }catch(Exception E){
                Log.e("com erroor", "onActivityResult: "+ E.toString());
            }
            new Upload().execute();


        }


    }
    public static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
    public class Upload extends AsyncTask<Void, Void, String>
    {
        @Override
        protected String doInBackground(Void... unsued) {










            FTPClient con;
            try {

                Log.d("ftp", "entered");

                con = new FTPClient();
                con.connect(host);
                String rep=con.getReplyString().toString();
                Log.d("reply: ", rep);
                if (con.login(user, pass)) {

                    con.enterLocalPassiveMode(); // important!
                    con.setFileType(FTP.BINARY_FILE_TYPE);


                    FileInputStream in = new FileInputStream(comp);

                    boolean result = con.storeFile("slls 3uwet.gif", in);

                    in.close();
                    int code =con.getReplyCode();
                    Log.d("ftp", ""+result+" code "+code);
                    if (result) Log.v("upload result", "succeeded");
                    con.logout();
                    con.disconnect();
                }
            } catch (Exception e) {

                e.printStackTrace();
                Log.e(e.getClass().getName(), e.getMessage(), e);
            }
            return "Success";
        }
        @Override
        protected void onProgressUpdate(Void... unsued)
        {
        }

        @Override
        protected void onPostExecute(String sResponse)
        {
            try
            {


            }
            catch (Exception e)
            {
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                Log.e(e.getClass().getName(), e.getMessage(), e);
            }
        }




    }
}
