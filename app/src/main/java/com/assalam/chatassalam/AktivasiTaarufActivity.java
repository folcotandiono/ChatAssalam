package com.assalam.chatassalam;

import android.*;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.assalam.chatassalam.api.ApiClient;
import com.assalam.chatassalam.api.ApiInterface;
import com.assalam.chatassalam.model.PembayaranTaaruf;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AktivasiTaarufActivity extends AppCompatActivity {

    private Toolbar tToolbar;
    private Button bShow;
    private TextView tvRekening;
    private ImageView ivGambar;
    private Button bAmbilGambar;
    private Button bAktivasi;
    private ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aktivasi_taaruf);

        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        tToolbar = findViewById(R.id.toolbar);
        tToolbar.setTitle("Aktivasi Taaruf");
        setSupportActionBar(tToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        bShow = findViewById(R.id.show);
        tvRekening = findViewById(R.id.rekening);
        ivGambar = findViewById(R.id.gambar);
        bAmbilGambar = findViewById(R.id.ambil_gambar);

        bAktivasi = findViewById(R.id.aktivasi);

        bShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvRekening.setVisibility(View.VISIBLE);
            }
        });

        bAmbilGambar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Here, thisActivity is the current activity
                if (ContextCompat.checkSelfPermission(AktivasiTaarufActivity.this,
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(AktivasiTaarufActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(AktivasiTaarufActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                        // No explanation needed; request the permission
                        ActivityCompat.requestPermissions(AktivasiTaarufActivity.this,
                                new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                                1234);

                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.

                } else {
                    // Permission has already been granted
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(AktivasiTaarufActivity.this);
                }
            }
        });

        bAktivasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ProgressDialog pd = new ProgressDialog(AktivasiTaarufActivity.this);
                pd.setMessage("loading");
                pd.show();

                SharedPreferences sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
                Map<String, String> param = new HashMap<>();
                param.put("id_user", sharedPreferences.getString("id_user", ""));

                BitmapDrawable drawable = (BitmapDrawable) ivGambar.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,50,bos);
                byte[] bb = bos.toByteArray();
                String image = Base64.encodeToString(bb , Base64.DEFAULT);

                param.put("gambar", image);

                Call<PembayaranTaaruf> call = apiInterface.pembayaranTaaruf(param);
                call.enqueue(new Callback<PembayaranTaaruf>() {
                    @Override
                    public void onResponse(Call<PembayaranTaaruf> call, Response<PembayaranTaaruf> response) {
                        pd.dismiss();
                        Toast.makeText(AktivasiTaarufActivity.this, "Berhasil", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<PembayaranTaaruf> call, Throwable t) {
                        t.printStackTrace();
                        pd.dismiss();
                        Toast.makeText(AktivasiTaarufActivity.this, "Gagal", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1234: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(AktivasiTaarufActivity.this);

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                ivGambar.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
