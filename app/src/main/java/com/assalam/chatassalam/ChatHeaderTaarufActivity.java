package com.assalam.chatassalam;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.assalam.chatassalam.api.ApiClient;
import com.assalam.chatassalam.api.ApiInterface;
import com.assalam.chatassalam.model.ChatHeader;
import com.assalam.chatassalam.model.Contact;
import com.assalam.chatassalam.model.Group;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatHeaderTaarufActivity extends AppCompatActivity {

    private ApiInterface apiInterface;
    private Toolbar tToolbar;
    private RecyclerView rvRecyclerView;
    private FloatingActionButton fab;
    private FirebaseRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_taaruf_header);

        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        tToolbar = findViewById(R.id.toolbar);
        tToolbar.setTitle("Chat Taaruf");
        setSupportActionBar(tToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatHeaderTaarufActivity.this, SelectTaarufContactActivity.class);
                startActivity(intent);
            }
        });

        rvRecyclerView = findViewById(R.id.recycler_view);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        // Now set the layout manager and the adapter to the RecyclerView
        rvRecyclerView.setLayoutManager(mLayoutManager);

        SharedPreferences sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        String idUser = sharedPreferences.getString("id_user", "");

        Query query=FirebaseDatabase.getInstance().getReference().child("chat_header_taaruf").child(idUser).orderByChild("waktu");
        FirebaseRecyclerOptions<ChatHeader> options =
                new FirebaseRecyclerOptions.Builder<ChatHeader>()
                        .setQuery(query, ChatHeader.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<ChatHeader, ChatHeaderTaarufActivity.ViewHolder>(options) {
            @Override
            public ChatHeaderTaarufActivity.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//                Toast.makeText(getActivity(), "create", Toast.LENGTH_SHORT).show();
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.card_chat_header, parent, false);

                return new ChatHeaderTaarufActivity.ViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(ChatHeaderTaarufActivity.ViewHolder holder, final int position, ChatHeader model) {
//                Toast.makeText(getActivity(), "bind", Toast.LENGTH_SHORT).show();
                LinearLayout llInti = holder.llInti;
                final TextView tvNama = holder.tvNama;
                final CircleImageView civGambar = holder.civGambar;
                final TextView tvPesanTerakhir = holder.tvPesanTerakhir;

                if (model.getHeader() != null) {
                    tvNama.setText(model.getNama());
                    tvPesanTerakhir.setText(model.getPesan_terakhir());
                    String status = "";
                    String id = "";
                    int i = 0;
                    for (; i < model.getHeader().length(); i++) {
                        if (model.getHeader().charAt(i) == '_') {
                            i++;
                            break;
                        }
                        status += model.getHeader().charAt(i);
                    }

                    for (; i < model.getHeader().length(); i++) {
                        if (model.getHeader().charAt(i) == '_') {
                            break;
                        }
                        id += model.getHeader().charAt(i);
                    }

                    final String finalId = id;
                    final String finalStatus = status;

                    if (status.equals("personal")) {
                        Map<String, String> param = new HashMap<>();
                        param.put("id_user", id);

                        Call<Contact> call = apiInterface.getUser(param);
                        call.enqueue(new Callback<Contact>() {
                            @Override
                            public void onResponse(Call<Contact> call, Response<Contact> response) {
                                Picasso.get().load("https://www.assalam.id/assets/panel/images/" + response.body().getGambar())
                                        .into(civGambar);
                            }

                            @Override
                            public void onFailure(Call<Contact> call, Throwable t) {

                            }
                        });
                    }
                    else {
                        Map<String, String> param = new HashMap<>();
                        param.put("id_group", id);

                        Call<Group> call = apiInterface.getGroup(param);
                        call.enqueue(new Callback<Group>() {
                            @Override
                            public void onResponse(Call<Group> call, Response<Group> response) {
                                Picasso.get().load("https://www.assalam.id/assets/panel/images/" + response.body().getGambar())
                                        .into(civGambar);
                            }

                            @Override
                            public void onFailure(Call<Group> call, Throwable t) {

                            }
                        });
                    }

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            final Intent intent = new Intent(v.getContext(), ChatTaarufActivity.class);
                            if (finalStatus.equals("personal")) {
                                intent.putExtra("id", finalId);
                                intent.putExtra("nama", tvNama.getText().toString());
                                intent.putExtra("status", finalStatus);
                                v.getContext().startActivity(intent);
                            }
                            else {
                                Map<String, String> param = new HashMap<>();
                                param.put("id_group", finalId);

                                Call<List<Contact>> call = apiInterface.groupMember(param);
                                call.enqueue(new Callback<List<Contact>>() {
                                    @Override
                                    public void onResponse(Call<List<Contact>> call, Response<List<Contact>> response) {
                                        ArrayList<String> listId = new ArrayList<>();
                                        for (Contact contact : response.body()) {
                                            listId.add(contact.getIdUser());
                                        }

                                        intent.putStringArrayListExtra("id", listId);
                                        intent.putExtra("id_group", finalId);
                                        intent.putExtra("nama", tvNama.getText().toString());
                                        intent.putExtra("status", finalStatus);
                                        v.getContext().startActivity(intent);
                                    }

                                    @Override
                                    public void onFailure(Call<List<Contact>> call, Throwable t) {

                                    }
                                });
                            }
                        }
                    });
                }
                else {
                    llInti.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(@NonNull DatabaseError error) {
                super.onError(error);
                Toast.makeText(ChatHeaderTaarufActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        adapter.startListening();
        rvRecyclerView.setAdapter(adapter);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout llInti;
        public CircleImageView civGambar;
        public TextView tvNama;
        public TextView tvPesanTerakhir;

        public ViewHolder(View itemView) {
            super(itemView);
            llInti = itemView.findViewById(R.id.inti);
            civGambar = itemView.findViewById(R.id.gambar);
            tvNama = itemView.findViewById(R.id.nama);
            tvPesanTerakhir = itemView.findViewById(R.id.pesan_terakhir);
        }
    }
}
