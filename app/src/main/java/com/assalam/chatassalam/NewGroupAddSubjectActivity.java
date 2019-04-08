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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.assalam.chatassalam.api.ApiClient;
import com.assalam.chatassalam.api.ApiInterface;
import com.assalam.chatassalam.model.Contact;
import com.assalam.chatassalam.model.GetTime;
import com.assalam.chatassalam.model.Group;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewGroupAddSubjectActivity extends AppCompatActivity {

    private Toolbar tToolbar;
    private CircleImageView civGambar;
    private EditText etNamaGroup;
    private Button bNext;
    private TextView tvBanyak;
    private ApiInterface apiInterface;
    private RecyclerView rvRecyclerView;
    private NewGroupParticipantsAdapter adapter;
    static final int permission_ = 12345;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group_add_subject);
// ...
        mDatabase = FirebaseDatabase.getInstance().getReference();

        tToolbar = findViewById(R.id.toolbar);
        tToolbar.setTitle("New group");
        tToolbar.setSubtitle("Add subject");
        setSupportActionBar(tToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        civGambar = findViewById(R.id.gambar);

        Picasso.get().load("https://www.assalam.id/assets/panel/images/" + "default.png").into(civGambar);

        civGambar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Here, thisActivity is the current activity
                if (checkPermission()) {
                        ActivityCompat.requestPermissions(NewGroupAddSubjectActivity.this,
                                new String[] {android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                        android.Manifest.permission.READ_EXTERNAL_STORAGE},
                                permission_);

                } else {
                    // Permission has already been granted
                    CropImage.activity()
                            .start(NewGroupAddSubjectActivity.this);
                }
            }
        });

        etNamaGroup = findViewById(R.id.nama_group);

        bNext = findViewById(R.id.next);

        bNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etNamaGroup.getText().toString().isEmpty()) {
                    Toast.makeText(NewGroupAddSubjectActivity.this, "Nama group kosong", Toast.LENGTH_SHORT).show();
                }
                final ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

                Map<String, String> param = new HashMap<>();
                param.put("nama", etNamaGroup.getText().toString());

                BitmapDrawable drawable = (BitmapDrawable) civGambar.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG,100,bos);
                byte[] bb = bos.toByteArray();
                String image = Base64.encodeToString(bb , Base64.DEFAULT);

                param.put("gambar", image);

                final SharedPreferences sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
                param.put("id_user", sharedPreferences.getString("id_user", ""));

                final List<String> listIdUser = getIntent().getStringArrayListExtra("list_id_user");
                JSONArray json = new JSONArray();

                for (String id : listIdUser) {
                    json.put(id);
                }
                param.put("list_id_user", json.toString());

                Call<Group> call = apiInterface.makeGroup(param);
                call.enqueue(new Callback<Group>() {
                    @Override
                    public void onResponse(Call<Group> call, Response<Group> response) {
                        final Group group = response.body();
                        Call<GetTime> call1 = apiInterface.getTime();
                        call1.enqueue(new Callback<GetTime>() {
                            @Override
                            public void onResponse(Call<GetTime> call, Response<GetTime> response) {
                                String id = mDatabase.push().getKey();

//                                mDatabase.child("chat").child(sharedPreferences.getString("id_user", "")).child("group_" + group.getIdGroup()).child(id).child("pesan").setValue("");
//                                mDatabase.child("chat").child(sharedPreferences.getString("id_user", "")).child("group_" + group.getIdGroup()).child(id).child("id_user").setValue(sharedPreferences.getString("id_user", ""));
//                                mDatabase.child("chat").child(sharedPreferences.getString("id_user", "")).child("group_" + group.getIdGroup()).child(id).child("nama").setValue(sharedPreferences.getString("nama", ""));
//                                mDatabase.child("chat").child(sharedPreferences.getString("id_user", "")).child("group_" + group.getIdGroup()).child(id).child("waktu").setValue(group.getTanggal());
//                                mDatabase.child("chat").child(sharedPreferences.getString("id_user", "")).child("group_" + group.getIdGroup()).child(id).child("read").setValue("0");


                                SharedPreferences sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
                                String idUser = sharedPreferences.getString("id_user", "");
                                mDatabase.child("chat_header").child(idUser).child("group_" + group.getIdGroup()).child("header").setValue("group_" + group.getIdGroup());
                                mDatabase.child("chat_header").child(idUser).child("group_" + group.getIdGroup()).child("nama").setValue(group.getNama());
                                mDatabase.child("chat_header").child(idUser).child("group_" + group.getIdGroup()).child("pesan_terakhir").setValue("");
                                mDatabase.child("chat_header").child(idUser).child("group_" + group.getIdGroup()).child("waktu").setValue(response.body().getTanggal());

                                for (String id1 : listIdUser) {
//                                    mDatabase.child("chat").child(id1).child("group_" + group.getIdGroup()).child(id).child("pesan").setValue("");
//                                    mDatabase.child("chat").child(id1).child("group_" + group.getIdGroup()).child(id).child("id_user").setValue(sharedPreferences.getString("id_user", ""));
//                                    mDatabase.child("chat").child(id1).child("group_" + group.getIdGroup()).child(id).child("nama").setValue(sharedPreferences.getString("nama", ""));
//                                    mDatabase.child("chat").child(id1).child("group_" + group.getIdGroup()).child(id).child("waktu").setValue(group.getTanggal());
//                                    mDatabase.child("chat").child(id1).child("group_" + group.getIdGroup()).child(id).child("read").setValue("0");

                                    mDatabase.child("chat_header").child(id1).child("group_" + group.getIdGroup()).child("header").setValue("group_" + group.getIdGroup());
                                    mDatabase.child("chat_header").child(id1).child("group_" + group.getIdGroup()).child("nama").setValue(group.getNama());
                                    mDatabase.child("chat_header").child(id1).child("group_" + group.getIdGroup()).child("pesan_terakhir").setValue("");
                                    mDatabase.child("chat_header").child(id1).child("group_" + group.getIdGroup()).child("waktu").setValue(response.body().getTanggal());
                                }
                            }

                            @Override
                            public void onFailure(Call<GetTime> call, Throwable t) {

                            }
                        });

                    }

                    @Override
                    public void onFailure(Call<Group> call, Throwable t) {
                        t.printStackTrace();
                    }
                });

                Intent intent = new Intent(NewGroupAddSubjectActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        tvBanyak = findViewById(R.id.banyak);

        rvRecyclerView = findViewById(R.id.recycler_view);

        List<String> listIdUser = getIntent().getStringArrayListExtra("list_id_user");

        adapter = new NewGroupParticipantsAdapter(listIdUser);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 4);
        rvRecyclerView.setLayoutManager(mLayoutManager);
        rvRecyclerView.setAdapter(adapter);

        tvBanyak.setText("Banyak : " + listIdUser.size() + " orang");

        apiInterface = ApiClient.getClient().create(ApiInterface.class);
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
                            .start(NewGroupAddSubjectActivity.this);

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

            civGambar.setImageURI(imageUri);
        }
    }

    private Boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(NewGroupAddSubjectActivity.this,
                android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (ContextCompat.checkSelfPermission(NewGroupAddSubjectActivity.this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (ContextCompat.checkSelfPermission(NewGroupAddSubjectActivity.this,

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
class NewGroupParticipantsAdapter extends RecyclerView.Adapter<NewGroupParticipantsAdapter.MyViewHolder> {

    private List<String> listIdUser;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView civGambar;
        public TextView tvNama;

        public MyViewHolder(View view) {
            super(view);
            civGambar = view.findViewById(R.id.gambar);
            tvNama = view.findViewById(R.id.nama);
        }
    }


    public NewGroupParticipantsAdapter(List<String> listIdUser) {
        this.listIdUser = listIdUser;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_participant_new_group, parent, false);


        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        String idUser = listIdUser.get(position);

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        Map<String, String> param = new HashMap<>();
        param.put("id_user", idUser);

        Call<Contact> call = apiInterface.getUser(param);
        call.enqueue(new Callback<Contact>() {
            @Override
            public void onResponse(Call<Contact> call, Response<Contact> response) {
                Contact contact = response.body();
                holder.tvNama.setText(contact.getNama());
                Picasso.get().load("https://www.assalam.id/assets/panel/images/" + contact.getGambar()).into(holder.civGambar);
            }

            @Override
            public void onFailure(Call<Contact> call, Throwable t) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return listIdUser.size();
    }
}
