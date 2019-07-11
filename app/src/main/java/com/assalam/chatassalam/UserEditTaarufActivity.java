package com.assalam.chatassalam;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
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
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.assalam.chatassalam.api.ApiClient;
import com.assalam.chatassalam.api.ApiInterface;
import com.assalam.chatassalam.model.EditProfil;
import com.assalam.chatassalam.model.EditProfilTaaruf;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserEditTaarufActivity extends AppCompatActivity {

    private ApiInterface apiInterface;
    private SharedPreferences sharedPreferences;
    private Toolbar tToolbar;
    private ImageView ivGambar;
    private EditText etNama;
    private Spinner sJenisKelamin;
    private EditText etTanggalLahir;
    static final int permission_ = 12345;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit_taaruf);

        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);

        tToolbar = findViewById(R.id.toolbar);
        tToolbar.setTitle("Profil");
        setSupportActionBar(tToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ivGambar = findViewById(R.id.gambar);
        etNama = findViewById(R.id.nama);
        sJenisKelamin = findViewById(R.id.jenis_kelamin);
        etTanggalLahir = findViewById(R.id.tanggal_lahir);

        //create a list of items for the spinner.
        String[] items = new String[]{"L", "P"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.
        sJenisKelamin.setAdapter(adapter);

        Picasso.get().load("https://www.assalam.id/assets/panel/images/" + getIntent().getStringExtra("gambar")).into(ivGambar);
        etNama.setText(getIntent().getStringExtra("nama"));
        if (getIntent().getStringExtra("jenis_kelamin").equals("L")) {
            sJenisKelamin.setSelection(0);
        }
        else {
            sJenisKelamin.setSelection(1);
        }
        etTanggalLahir.setText(getIntent().getStringExtra("tanggal_lahir"));

        etTanggalLahir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(UserEditTaarufActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                etTanggalLahir.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        ivGambar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Here, thisActivity is the current activity
                if (checkPermission()) {
                    ActivityCompat.requestPermissions(UserEditTaarufActivity.this,
                            new String[] {android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    android.Manifest.permission.READ_EXTERNAL_STORAGE},
                            permission_);

                } else {
                    // Permission has already been granted
                    CropImage.activity()
                            .start(UserEditTaarufActivity.this);
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
                param.put("jenis_kelamin", sJenisKelamin.getSelectedItem().toString());
                param.put("tanggal_lahir", etTanggalLahir.getText().toString());
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

                Call<EditProfilTaaruf> call = apiInterface.editProfilTaaruf(param);
                call.enqueue(new Callback<EditProfilTaaruf>() {
                    @Override
                    public void onResponse(Call<EditProfilTaaruf> call, Response<EditProfilTaaruf> response) {
                        EditProfilTaaruf editProfilTaaruf = response.body();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("nama", editProfilTaaruf.getNama());
                        editor.putString("jenis_kelamin", editProfilTaaruf.getJenisKelamin());
                        editor.putString("tanggal_lahir", editProfilTaaruf.getTanggalLahir());
                        editor.putString("gambar", editProfilTaaruf.getGambar());
                        editor.apply();
                        Toast.makeText(UserEditTaarufActivity.this, "Berhasil", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<EditProfilTaaruf> call, Throwable throwable) {
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
                            .start(UserEditTaarufActivity.this);

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
        if (ContextCompat.checkSelfPermission(UserEditTaarufActivity.this,
                android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (ContextCompat.checkSelfPermission(UserEditTaarufActivity.this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (ContextCompat.checkSelfPermission(UserEditTaarufActivity.this,

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
