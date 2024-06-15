package com.codedev.qrcodescanergenerator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.zxing.WriterException;

import java.io.OutputStream;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class QRGeneratorActivity extends AppCompatActivity {

    private static final int STORAGE_PERMISSION_CODE = 101;
    private TextView qrCodeTV;
    private ImageView QR;
    private TextInputEditText edit;
    private Button GenerateQR,saveBtn,shareBtn;

    private QRGEncoder qrgEncoder;
    private Bitmap bitmap;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrgenerator);
        qrCodeTV=findViewById(R.id.tvGen);
        QR=findViewById(R.id.QR);
        edit=findViewById(R.id.edit_text);
        GenerateQR=findViewById(R.id.BtnGen);
        saveBtn=findViewById(R.id.saveBtn);
        shareBtn=findViewById(R.id.shareBtn);
        GenerateQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String data=edit.getText().toString();
                if (data.isEmpty()){
                    Toast.makeText(QRGeneratorActivity.this, "Please Enter some Text ", Toast.LENGTH_SHORT).show();
                }
                else {
                    WindowManager manager=(WindowManager) getSystemService(WINDOW_SERVICE);
                    Display display=manager.getDefaultDisplay();
                    Point point=new Point();
                    display.getSize(point);
                    int width=point.x;
                    int height=point.y;
                    int dimen= width < height ? width:height;
                    dimen= dimen * 3/4;
                    qrgEncoder=new QRGEncoder(edit.getText().toString(),null, QRGContents.Type.TEXT,dimen);
                    try {
                        bitmap =qrgEncoder.encodeAsBitmap();
                        qrCodeTV.setVisibility(View.GONE);
                        QR.setImageBitmap(bitmap);

                    }catch (WriterException e){
                        e.printStackTrace();
                    }

                }
            }
        });

// Request storage permission if not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageUri = saveImageToStorage(bitmap);
            }
        });

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageUri != null) {
                    shareImage(imageUri);
                } else {
                    Toast.makeText(QRGeneratorActivity.this, "Please save the image first", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private Uri saveImageToStorage(Bitmap bitmap) {
        if (bitmap == null) {
            Toast.makeText(this, "Bitmap is null", Toast.LENGTH_SHORT).show();
            return null;
        }

        String filename = System.currentTimeMillis() + ".png";
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, filename);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");

        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        try (OutputStream outputStream = getContentResolver().openOutputStream(uri)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            Toast.makeText(this, "Image saved", Toast.LENGTH_SHORT).show();
            return uri;
        } catch (Exception e) {
            Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return null;
        }
    }

    private void shareImage(Uri imageUri) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.setType("image/png");
        startActivity(Intent.createChooser(shareIntent, "Share Image"));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}