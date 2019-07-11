package com.assalam.chatassalam;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.assalam.chatassalam.api.ApiClient;
import com.assalam.chatassalam.api.ApiInterface;
import com.squareup.picasso.Picasso;

public class UserDetailActivity extends AppCompatActivity {

    private ApiInterface apiInterface;
    private SharedPreferences sharedPreferences;
    private Toolbar tToolbar;
    private ImageView ivGambar;
    private TextView tvNama;
    private TextView tvNoHp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);

        tToolbar = findViewById(R.id.toolbar);
        tToolbar.setTitle("Profil");
        setSupportActionBar(tToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ivGambar = findViewById(R.id.gambar);
        tvNama = findViewById(R.id.nama);
        tvNoHp = findViewById(R.id.no_hp);

        Picasso.get().load("https://www.assalam.id/assets/panel/images/" + getIntent().getStringExtra("gambar")).into(ivGambar);
        tvNama.setText(getIntent().getStringExtra("nama"));
        tvNoHp.setText(getIntent().getStringExtra("no_hp"));
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
                Intent intent = new Intent(UserDetailActivity.this, UserEditActivity.class);
                intent.putExtra("id_user", getIntent().getStringExtra("id_user"));
                intent.putExtra("nama", getIntent().getStringExtra("nama"));
                intent.putExtra("no_hp", getIntent().getStringExtra("no_hp"));
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
