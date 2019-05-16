package com.assalam.chatassalam;

import android.app.DatePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.assalam.chatassalam.api.ApiClient;
import com.assalam.chatassalam.api.ApiInterface;
import com.assalam.chatassalam.model.Register;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText etNoHp;
    private EditText etUsername;
    private EditText etPassword;
    private EditText etRePassword;
    private EditText etNama;
    private Spinner sJenisKelamin;
    private EditText etTanggalLahir;
    private Button bRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etNoHp = findViewById(R.id.no_hp);
        etUsername = findViewById(R.id.username);
        etPassword = findViewById(R.id.password);
        etRePassword = findViewById(R.id.re_password);
        etNama = findViewById(R.id.nama);
        sJenisKelamin = findViewById(R.id.jenis_kelamin);
        etTanggalLahir = findViewById(R.id.tanggal_lahir);
        bRegister = findViewById(R.id.register);

        //create a list of items for the spinner.
        String[] items = new String[]{"L", "P"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.
        sJenisKelamin.setAdapter(adapter);

        etTanggalLahir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(RegisterActivity.this,
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

        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String noHp = etNoHp.getText().toString();
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                String rePassword = etRePassword.getText().toString();
                String nama = etNama.getText().toString();
                String jenisKelamin = sJenisKelamin.getSelectedItem().toString();
                String tanggalLahir = etTanggalLahir.getText().toString();

                if (noHp.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "No. hp kosong", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (username.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Username kosong", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Password kosong", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!password.equals(rePassword)) {
                    Toast.makeText(RegisterActivity.this, "Password harus sama", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (nama.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Nama kosong", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (jenisKelamin.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Jenis kelamin kosong", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (tanggalLahir.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Tanggal lahir kosong", Toast.LENGTH_SHORT).show();
                    return;
                }

                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

                Map<String, String> param = new HashMap<>();
                param.put("no_hp", noHp);
                param.put("username", username);
                param.put("password", password);
                param.put("nama", nama);
                param.put("jenis_kelamin", jenisKelamin);
                param.put("tanggal_lahir", tanggalLahir);

                Call<Register> call = apiInterface.register(param);
                call.enqueue(new Callback<Register>() {
                    @Override
                    public void onResponse(Call<Register> call, Response<Register> response) {
                        Register register = response.body();
                        Toast.makeText(RegisterActivity.this, register.getResponse(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<Register> call, Throwable t) {

                    }
                });
            }
        });
    }
}
