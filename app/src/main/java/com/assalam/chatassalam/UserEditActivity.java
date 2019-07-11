package com.assalam.chatassalam;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.assalam.chatassalam.api.ApiClient;
import com.assalam.chatassalam.api.ApiInterface;
import com.assalam.chatassalam.model.EditProfil;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserEditActivity extends AppCompatActivity {

    private ApiInterface apiInterface;
    private SharedPreferences sharedPreferences;
    private Toolbar tToolbar;
    private ImageView ivGambar;
    private EditText etNama;
    static final int permission_ = 12345;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit);

        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);

        tToolbar = findViewById(R.id.toolbar);
        tToolbar.setTitle("Profil");
        setSupportActionBar(tToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ivGambar = findViewById(R.id.gambar);
        etNama = findViewById(R.id.nama);

        Picasso.get().load("https://www.assalam.id/assets/panel/images/" + getIntent().getStringExtra("gambar")).into(ivGambar);
        etNama.setText(getIntent().getStringExtra("nama"));

        ivGambar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Here, thisActivity is the current activity
                if (checkPermission()) {
                    ActivityCompat.requestPermissions(UserEditActivity.this,
                            new String[] {android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    android.Manifest.permission.READ_EXTERNAL_STORAGE},
                            permission_);

                } else {
                    // Permission has already been granted
                    CropImage.activity()
                            .start(UserEditActivity.this);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ok, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_ok:
                Map<String, String> param = new HashMap<>();
                param.put("id_user", getIntent().getStringExtra("id_user"));
                param.put("nama", etNama.getText().toString());

                String image = "";

                if (ivGambar.getDrawable() != null) {
                    BitmapDrawable drawable = (BitmapDrawable) ivGambar.getDrawable();
                    Bitmap bitmap = drawable.getBitmap();
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 20, bos);
                    byte[] bb = bos.toByteArray();
                    image = Base64.encodeToString(bb, Base64.DEFAULT);
                }

                param.put("gambar", image);

                Call<EditProfil> call = apiInterface.editProfil(param);
                call.enqueue(new Callback<EditProfil>() {
                    @Override
                    public void onResponse(Call<EditProfil> call, Response<EditProfil> response) {
                        EditProfil editProfil = response.body();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("nama", editProfil.getNama());
                        editor.putString("gambar", editProfil.getGambar());
                        editor.apply();
                        Toast.makeText(UserEditActivity.this, "Berhasil", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<EditProfil> call, Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case permission_ : {
                // If request is cancelled, the result arrays are empty.
                Boolean bisa = true;
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                        bisa = false;
                    }
                }
                if (grantResults.length == 0) bisa = false;
                if (bisa) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    CropImage.activity()
                            .start(UserEditActivity.this);

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
    @SuppressLint("NewApi")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // handle result of pick image chooser
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);

            ivGambar.setImageURI(imageUri);
        }
    }

    private Boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(UserEditActivity.this,
                android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (ContextCompat.checkSelfPermission(UserEditActivity.this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (ContextCompat.checkSelfPermission(UserEditActivity.this,

                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
