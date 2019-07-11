package com.assalam.chatassalam;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.assalam.chatassalam.api.ApiClient;
import com.assalam.chatassalam.api.ApiInterface;
import com.assalam.chatassalam.model.AddFriend;
import com.assalam.chatassalam.model.CheckFriend;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserDetailTaarufActivity extends AppCompatActivity {

    private ApiInterface apiInterface;
    private SharedPreferences sharedPreferences;
    private Toolbar tToolbar;
    private ImageView ivGambar;
    private Button bTambahTeman;
    private TextView tvNama;
    private TextView tvUsername;
    private TextView tvJenisKelamin;
    private TextView tvNoHp;
    private TextView tvTanggalLahir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail_taaruf);

        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        tToolbar = findViewById(R.id.toolbar);
        tToolbar.setTitle("Detail user");
        setSupportActionBar(tToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ivGambar = findViewById(R.id.gambar);
        bTambahTeman = findViewById(R.id.tambah_teman);
        tvNama = findViewById(R.id.nama);
        tvUsername = findViewById(R.id.username);
        tvJenisKelamin = findViewById(R.id.jenis_kelamin);
        tvNoHp = findViewById(R.id.no_hp);
        tvTanggalLahir = findViewById(R.id.tanggal_lahir);

        Picasso.get().load("https://www.assalam.id/assets/panel/images/" + getIntent().getStringExtra("gambar")).into(ivGambar);
        tvNama.setText(getIntent().getStringExtra("nama"));
        tvUsername.setText(getIntent().getStringExtra("username"));
        tvJenisKelamin.setText(getIntent().getStringExtra("jenis_kelamin"));
        tvNoHp.setText(getIntent().getStringExtra("no_hp"));
        tvTanggalLahir.setText(getIntent().getStringExtra("tanggal_lahir"));

        bTambahTeman.setVisibility(View.GONE);

        Map<String, String> param = new HashMap<>();
        sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        param.put("id_user", sharedPreferences.getString("id_user", ""));
        param.put("id_teman", getIntent().getStringExtra("id_user"));

        Call<CheckFriend> call = apiInterface.checkFriend(param);
        call.enqueue(new Callback<CheckFriend>() {
            @Override
            public void onResponse(Call<CheckFriend> call, Response<CheckFriend> response) {
                CheckFriend checkFriend = response.body();
                if (checkFriend.getFriend().equals("0") && !getIntent().getStringExtra("id_user").equals(sharedPreferences.getString("id_user", ""))) {
                    bTambahTeman.setVisibility(View.VISIBLE);
                }
                else {
                    bTambahTeman.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<CheckFriend> call, Throwable throwable) {

            }
        });

        bTambahTeman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, String> param = new HashMap<>();
                param.put("id_user", sharedPreferences.getString("id_user", ""));
                param.put("id_teman", getIntent().getStringExtra("id_user"));

                Call<AddFriend> call = apiInterface.addFriend(param);
                call.enqueue(new Callback<AddFriend>() {
                    @Override
                    public void onResponse(Call<AddFriend> call, Response<AddFriend> response) {
                        bTambahTeman.setVisibility(View.GONE);
                        Toast.makeText(UserDetailTaarufActivity.this, "Teman telah ditambah", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<AddFriend> call, Throwable throwable) {

                    }
                });
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (getIntent().getStringExtra("id_user").equals(sharedPreferences.getString("id_user", ""))) {
            getMenuInflater().inflate(R.menu.menu_edit, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_edit:
                Intent intent = new Intent(UserDetailTaarufActivity.this, UserEditTaarufActivity.class);
                intent.putExtra("id_user", getIntent().getStringExtra("id_user"));
                intent.putExtra("nama", getIntent().getStringExtra("nama"));
                intent.putExtra("no_hp", getIntent().getStringExtra("no_hp"));
                intent.putExtra("jenis_kelamin", getIntent().getStringExtra("jenis_kelamin"));
                intent.putExtra("tanggal_lahir", getIntent().getStringExtra("tanggal_lahir"));
                intent.putExtra("gambar", getIntent().getStringExtra("gambar"));
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
