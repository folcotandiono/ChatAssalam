package com.assalam.chatassalam;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.assalam.chatassalam.api.ApiClient;
import com.assalam.chatassalam.api.ApiInterface;
import com.assalam.chatassalam.model.Contact;
import com.assalam.chatassalam.model.GetTime;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectTaarufContactActivity extends AppCompatActivity {

    private ApiInterface apiInterface;
    private SharedPreferences sharedPreferences;
    private Toolbar tToolbar;
    private List<Contact> contactList;
    private RecyclerView rvRecyclerView;
    private TaarufContactListAdapter taarufContactListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_taaruf_contact);

        tToolbar = findViewById(R.id.toolbar);
        tToolbar.setTitle("Select taaruf contact");
        setSupportActionBar(tToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        rvRecyclerView = findViewById(R.id.recycler_view);

        contactList = new ArrayList<>();
        taarufContactListAdapter = new TaarufContactListAdapter(this, contactList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvRecyclerView.setLayoutManager(mLayoutManager);
        rvRecyclerView.setAdapter(taarufContactListAdapter);

        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);

        Map<String, String> param = new HashMap<>();
        param.put("id_user", sharedPreferences.getString("id_user", ""));

        Call<List<Contact>> call = apiInterface.listFriend(param);
        call.enqueue(new Callback<List<Contact>>() {
            @Override
            public void onResponse(Call<List<Contact>> call, Response<List<Contact>> response) {
                contactList.clear();

                for (Contact contact : response.body()) {
                    contactList.add(contact);
                }

                taarufContactListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<Contact>> call, Throwable throwable) {

            }
        });
    }
}
class TaarufContactListAdapter extends RecyclerView.Adapter<TaarufContactListAdapter.MyViewHolder> {

    private Context mContext;
    private List<Contact> contactList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView civGambar;
        private TextView tvIdUser, tvNama, tvStatus;

        public MyViewHolder(final View view) {
            super(view);
            civGambar = view.findViewById(R.id.gambar);
            tvIdUser = view.findViewById(R.id.id_user);
            tvNama = view.findViewById(R.id.nama);
            tvStatus = view.findViewById(R.id.status);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), ChatTaarufActivity.class);
                    intent.putExtra("id", tvIdUser.getText().toString());
                    intent.putExtra("nama", tvNama.getText().toString());
                    intent.putExtra("status", "personal");
                    v.getContext().startActivity(intent);

                    final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                    SharedPreferences sharedPreferences = v.getContext().getSharedPreferences("login", Context.MODE_PRIVATE);
                    final String idUser = sharedPreferences.getString("id_user", "");
                    final String nama = sharedPreferences.getString("nama", "");

                    ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

                    Call<GetTime> call = apiInterface.getTime();
                    call.enqueue(new Callback<GetTime>() {
                        @Override
                        public void onResponse(Call<GetTime> call, final Response<GetTime> response) {
                            Query queryRef = mDatabase.child("chat_header_taaruf").child(idUser).orderByChild("header").equalTo("personal_" + tvIdUser.getText().toString() + "_" + idUser);
                            queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (!dataSnapshot.exists()) {
                                        mDatabase.child("chat_header_taaruf").child(idUser).child("personal_" + tvIdUser.getText().toString() + "_" + idUser).child("header").setValue("personal_" + tvIdUser.getText().toString() + "_" + idUser);
                                        mDatabase.child("chat_header_taaruf").child(idUser).child("personal_" + tvIdUser.getText().toString() + "_" + idUser).child("nama").setValue(tvNama.getText().toString());
                                        mDatabase.child("chat_header_taaruf").child(idUser).child("personal_" + tvIdUser.getText().toString() + "_" + idUser).child("pesan_terakhir").setValue("");
                                        mDatabase.child("chat_header_taaruf").child(idUser).child("personal_" + tvIdUser.getText().toString() + "_" + idUser).child("waktu").setValue(response.body().getTanggal());
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            Query queryRef1 = mDatabase.child("chat_header_taaruf").child(tvIdUser.getText().toString()).orderByChild("header").equalTo("personal_" + idUser + "_" + tvIdUser.getText().toString());
                            queryRef1.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (!dataSnapshot.exists()) {
                                        mDatabase.child("chat_header_taaruf").child(tvIdUser.getText().toString()).child("personal_" + idUser + "_" + tvIdUser.getText().toString()).child("header").setValue("personal_" + idUser + "_" + tvIdUser.getText().toString());
                                        mDatabase.child("chat_header_taaruf").child(tvIdUser.getText().toString()).child("personal_" + idUser + "_" + tvIdUser.getText().toString()).child("nama").setValue(nama);
                                        mDatabase.child("chat_header_taaruf").child(tvIdUser.getText().toString()).child("personal_" + idUser + "_" + tvIdUser.getText().toString()).child("pesan_terakhir").setValue("");
                                        mDatabase.child("chat_header_taaruf").child(tvIdUser.getText().toString()).child("personal_" + idUser + "_" + tvIdUser.getText().toString()).child("waktu").setValue(response.body().getTanggal());
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }

                        @Override
                        public void onFailure(Call<GetTime> call, Throwable t) {

                        }
                    });


                }
            });
        }
    }


    public TaarufContactListAdapter(Context mContext, List<Contact> contactList) {
        this.mContext = mContext;
        this.contactList = contactList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_contact, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        if (!contactList.get(position).getGambar().toString().isEmpty()) {
            Picasso.get().load("https://www.assalam.id/assets/panel/images/" + contactList.get(position).getGambar().toString())
                    .into(holder.civGambar);
        }
        else {
            Picasso.get().load(R.drawable.guru).into(holder.civGambar);
        }
        holder.tvNama.setText(contactList.get(position).getNama().toString());
        holder.tvIdUser.setText(contactList.get(position).getIdUser().toString());
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }
}
