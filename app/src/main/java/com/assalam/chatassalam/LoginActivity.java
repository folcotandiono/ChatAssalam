package com.assalam.chatassalam;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.assalam.chatassalam.api.ApiClient;
import com.assalam.chatassalam.api.ApiInterface;
import com.assalam.chatassalam.model.Login;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etNoHp;
    private EditText etPassword;
    private Button bLogin;
    private TextView tvRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etNoHp = findViewById(R.id.no_hp);
        etPassword = findViewById(R.id.password);
        bLogin = findViewById(R.id.login);
        tvRegister = findViewById(R.id.register);

        SharedPreferences sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);

        if (!sharedPreferences.getString("id_user", "").isEmpty()) {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String noHp = etNoHp.getText().toString();
                String password = etPassword.getText().toString();

                if (noHp.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "No. hp kosong", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Password kosong", Toast.LENGTH_SHORT).show();
                    return;
                }

                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

                Map<String, String> param = new HashMap<>();
                param.put("no_hp", noHp);
                param.put("password", password);

                Call<Login> call = apiInterface.login(param);
                call.enqueue(new Callback<Login>() {
                    @Override
                    public void onResponse(Call<Login> call, Response<Login> response) {
                        Login login = response.body();

                        if (login.getResponse().equals("ok")) {
                            SharedPreferences sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("id_user", login.getIdUser());
                            editor.putString("nama", login.getNama());
                            editor.putString("no_hp", login.getNoHp());
                            editor.putString("jenis_kelamin", login.getJenisKelamin());
                            editor.putString("tanggal_lahir", login.getTanggalLahir());
                            editor.apply();

                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            Toast.makeText(LoginActivity.this, login.getResponse(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Login> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
            }
        });


    }
}
