package com.assalam.chatassalam;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.assalam.chatassalam.api.ApiClient;
import com.assalam.chatassalam.api.ApiInterface;
import com.assalam.chatassalam.model.Contact;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {

    private ApiInterface apiInterface;
    private Toolbar tToolbar;
    private EditText etUsername;
    private ImageView ivSearch;
    private RecyclerView rvRecyclerView;
    private SearchUserAdapter adapter;
    private List<Contact> contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        tToolbar = findViewById(R.id.toolbar);
        tToolbar.setTitle("Pencarian");
        setSupportActionBar(tToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        etUsername = findViewById(R.id.username);

        ivSearch = findViewById(R.id.search);
        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> param = new HashMap<>();
                param.put("username", etUsername.getText().toString());

                Call<List<Contact>> call = apiInterface.searchUser(param);
                call.enqueue(new Callback<List<Contact>>() {
                    @Override
                    public void onResponse(Call<List<Contact>> call, Response<List<Contact>> response) {
                        contactList.clear();

                        for (Contact contact : response.body()) {
                            contactList.add(contact);
                        }

                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Call<List<Contact>> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
            }
        });

        rvRecyclerView = findViewById(R.id.recycler_view);

        contactList = new ArrayList<>();

        adapter = new SearchUserAdapter(contactList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvRecyclerView.setLayoutManager(mLayoutManager);
        rvRecyclerView.setAdapter(adapter);
    }
}
class SearchUserAdapter extends RecyclerView.Adapter<SearchUserAdapter.MyViewHolder> {

    private List<Contact> contactList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvIdUser, tvNama, tvUsername;
        public CircleImageView civGambar;

        public MyViewHolder(View view) {
            super(view);
            tvIdUser = view.findViewById(R.id.id_user);
            tvNama = view.findViewById(R.id.nama);
            tvUsername = view.findViewById(R.id.username);
            civGambar = view.findViewById(R.id.gambar);
        }
    }


    public SearchUserAdapter(List<Contact> contactList) {
        this.contactList = contactList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_search_contact, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Contact contact = contactList.get(position);
        holder.tvIdUser.setText(contact.getIdUser());
        holder.tvNama.setText(contact.getNama());
        holder.tvUsername.setText(contact.getUsername());
        Picasso.get().load("https://www.assalam.id/assets/panel/images/" + contact.getGambar()).into(holder.civGambar);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.itemView.getContext(), UserDetailTaarufActivity.class);
                intent.putExtra("id_user", contact.getIdUser());
                intent.putExtra("nama", contact.getNama());
                intent.putExtra("username", contact.getUsername());
                intent.putExtra("gambar", contact.getGambar());
                intent.putExtra("jenis_kelamin", contact.getJenisKelamin());
                intent.putExtra("no_hp", contact.getNoHp());
                intent.putExtra("tanggal_lahir", contact.getTanggalLahir());
                intent.putExtra("status_taaruf", contact.getStatusTaaruf());
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }
}
