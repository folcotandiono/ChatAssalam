package com.assalam.chatassalam;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.assalam.chatassalam.api.ApiClient;
import com.assalam.chatassalam.api.ApiInterface;
import com.assalam.chatassalam.model.Chat;
import com.assalam.chatassalam.model.ChatHeader;
import com.assalam.chatassalam.model.Contact;
import com.assalam.chatassalam.model.GetTime;
import com.assalam.chatassalam.model.GetTimeDifference;
import com.assalam.chatassalam.model.Group;
import com.daasuu.bl.ArrowDirection;
import com.daasuu.bl.BubbleLayout;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.library.bubbleview.BubbleDrawable;
import com.github.library.bubbleview.BubbleTextView;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    private LinearLayout llLayoutPertama;
    private Toolbar tToolbar;
    private CircleImageView civGambar;
    private RecyclerView lvListView;
    private EditText etMessage;
    private FloatingActionButton fab;
    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter adapter;
    private Calendar calendar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        llLayoutPertama = findViewById(R.id.layout_pertama);
        llLayoutPertama.setBackground(getResources().getDrawable(R.drawable.chat));

        tToolbar = findViewById(R.id.toolbar);
        tToolbar.setTitle(getIntent().getStringExtra("nama"));
        setSupportActionBar(tToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        civGambar = findViewById(R.id.gambar);
        Picasso.get().load(R.drawable.guru).into(civGambar);

        lvListView = findViewById(R.id.list_view);
//        lvListView.setDivider(null);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(ChatActivity.this);

        // Now set the layout manager and the adapter to the RecyclerView
        lvListView.setLayoutManager(mLayoutManager);

        etMessage = findViewById(R.id.message);
        fab = findViewById(R.id.fab);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        final String status = getIntent().getStringExtra("status");

        final SharedPreferences sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        final String idUser = sharedPreferences.getString("id_user", "");
        final String idUserLain = getIntent().getStringExtra("id");
        final List<String> listIdUserLain = getIntent().getStringArrayListExtra("id");
        final String idGroup = getIntent().getStringExtra("id_group");

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etMessage.getText().toString().isEmpty()) {
                    return;
                }
                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

                Call<GetTime> call = apiInterface.getTime();

                call.enqueue(new Callback<GetTime>() {
                    @Override
                    public void onResponse(Call<GetTime> call, Response<GetTime> response) {
                        String timestamp = response.body().getTanggal();
                        String id = mDatabase.push().getKey();
                        if (status.equals("personal")) {
                            mDatabase.child("chat").child(idUser).child(status + "_" + idUserLain).child(id).child("pesan").setValue(etMessage.getText().toString());
                            mDatabase.child("chat").child(idUser).child(status + "_" + idUserLain).child(id).child("id_user").setValue(sharedPreferences.getString("id_user", ""));
                            mDatabase.child("chat").child(idUser).child(status + "_" + idUserLain).child(id).child("nama").setValue(sharedPreferences.getString("nama", ""));
                            mDatabase.child("chat").child(idUser).child(status + "_" + idUserLain).child(id).child("waktu").setValue(timestamp);
                            mDatabase.child("chat").child(idUser).child(status + "_" + idUserLain).child(id).child("read").setValue("0");
                            mDatabase.child("chat_header").child(idUser).child(status + "_" + idUserLain + "_" + idUser).child("pesan_terakhir").setValue(etMessage.getText().toString());
                            mDatabase.child("chat_header").child(idUser).child(status + "_" + idUserLain + "_" + idUser).child("waktu").setValue(timestamp);

                            mDatabase.child("chat").child(idUserLain).child(status + "_" + idUser).child(id).child("pesan").setValue(etMessage.getText().toString());
                            mDatabase.child("chat").child(idUserLain).child(status + "_" + idUser).child(id).child("id_user").setValue(sharedPreferences.getString("id_user", ""));
                            mDatabase.child("chat").child(idUserLain).child(status + "_" + idUser).child(id).child("nama").setValue(sharedPreferences.getString("nama", ""));
                            mDatabase.child("chat").child(idUserLain).child(status + "_" + idUser).child(id).child("waktu").setValue(timestamp);
                            mDatabase.child("chat").child(idUserLain).child(status + "_" + idUser).child(id).child("read").setValue("0");
                            mDatabase.child("chat_header").child(idUserLain).child(status + "_" + idUser + "_" + idUserLain).child("pesan_terakhir").setValue(etMessage.getText().toString());
                            mDatabase.child("chat_header").child(idUserLain).child(status + "_" + idUser + "_" + idUserLain).child("waktu").setValue(timestamp);

                        }
                        else {
                            for (String idUserLain : listIdUserLain) {
                                mDatabase.child("chat").child(idUserLain).child(status + "_" + idGroup).child(id).child("pesan").setValue(etMessage.getText().toString());
                                mDatabase.child("chat").child(idUserLain).child(status + "_" + idGroup).child(id).child("id_user").setValue(sharedPreferences.getString("id_user", ""));
                                mDatabase.child("chat").child(idUserLain).child(status + "_" + idGroup).child(id).child("nama").setValue(sharedPreferences.getString("nama", ""));
                                mDatabase.child("chat").child(idUserLain).child(status + "_" + idGroup).child(id).child("waktu").setValue(timestamp);
                                mDatabase.child("chat").child(idUserLain).child(status + "_" + idGroup).child(id).child("read").setValue("0");
                                mDatabase.child("chat_header").child(idUserLain).child(status + "_" + idGroup).child("pesan_terakhir").setValue(etMessage.getText().toString());
                                mDatabase.child("chat_header").child(idUserLain).child(status + "_" + idGroup).child("waktu").setValue(timestamp);
                            }
                        }
                        etMessage.setText("");

                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //Do something after 100ms
                                lvListView.scrollToPosition(lvListView.getAdapter().getItemCount() - 1);
                            }
                        }, 300);
                    }

                    @Override
                    public void onFailure(Call<GetTime> call, Throwable t) {

                    }
                });
            }
        });

        Query query;

        if (status.equals("personal")) query = FirebaseDatabase.getInstance().getReference().child("chat").child(idUser).child(status + "_" + idUserLain);
        else query = FirebaseDatabase.getInstance().getReference().child("chat").child(idUser).child(status + "_" + idGroup);

        FirebaseRecyclerOptions<Chat> options =
                new FirebaseRecyclerOptions.Builder<Chat>()
                        .setQuery(query, Chat.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Chat, ChatActivity.ViewHolder>(options) {

            @Override
            public void onError(@NonNull DatabaseError error) {
                super.onError(error);
                Toast.makeText(ChatActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
            @Override
            public ChatActivity.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//                Toast.makeText(ChatActivity.this, "create", Toast.LENGTH_SHORT).show();
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.card_chat, parent, false);

                return new ChatActivity.ViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(ChatActivity.ViewHolder holder, final int position, Chat model) {
//                Toast.makeText(ChatActivity.this, "bind", Toast.LENGTH_SHORT).show();
                DatabaseReference itemRef = getRef(position);
                String itemKey = itemRef.getKey();

                LinearLayout llInti;
                LinearLayout llChat;
                BubbleLayout blBubbleLayout;
                LinearLayout llNamaWaktuKiri;
                LinearLayout llNamaWaktuKanan;
                TextView tvNamaKiri;
                TextView tvPesan;
                final TextView tvWaktuKiri;
                TextView tvReadKiri;
                TextView tvNamaKanan;
                TextView tvWaktuKanan;
                TextView tvReadKanan;

                llInti = holder.llInti;
                llNamaWaktuKiri = holder.llNamaWaktuKiri;
                llNamaWaktuKanan = holder.llNamaWaktuKanan;
                llChat = holder.llChat;
                blBubbleLayout = holder.blBubbleLayout;
                tvNamaKiri = holder.tvNamaKiri;
                tvPesan = holder.tvPesan;
                tvWaktuKiri = holder.tvWaktuKiri;
                tvReadKiri = holder.tvReadKiri;
                tvNamaKanan = holder.tvNamaKanan;
                tvWaktuKanan = holder.tvWaktuKanan;
                tvReadKanan = holder.tvReadKanan;

                tvWaktuKiri.setText("");
                tvWaktuKanan.setText("");

//                Timestamp timestamp = Timestamp.valueOf(model.getWaktu());

                if (model.getId_user() != null) {
                    llInti.setVisibility(View.VISIBLE);
                    if (model.getPesan().isEmpty()) {
                        llInti.setVisibility(View.GONE);
                    }
                    else {
                        llInti.setVisibility(View.VISIBLE);
                    }
                    if (model.getId_user().equals(idUser)) {
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params.gravity = Gravity.RIGHT;

                        llChat.setLayoutParams(params);
                        blBubbleLayout.setArrowDirection(ArrowDirection.RIGHT);
                        blBubbleLayout.setBubbleColor(getResources().getColor(R.color.colorChat));

                        llNamaWaktuKanan.setVisibility(View.GONE);
                        llNamaWaktuKiri.setVisibility(View.VISIBLE);
                    } else {
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params.gravity = Gravity.LEFT;

                        llChat.setLayoutParams(params);
                        blBubbleLayout.setArrowDirection(ArrowDirection.LEFT);
                        blBubbleLayout.setBubbleColor(getResources().getColor(R.color.colorWhite));

                        llNamaWaktuKiri.setVisibility(View.GONE);
                        llNamaWaktuKanan.setVisibility(View.VISIBLE);

                        if (status.equals("personal")) {
                            mDatabase.child("chat").child(idUser).child(status + "_" + idUserLain).child(itemKey).child("read").setValue("1");
                            mDatabase.child("chat").child(idUserLain).child(status + "_" + idUser).child(itemKey).child("read").setValue("1");
                        }
                        else {
                            for (String idUserLain : listIdUserLain) {
                                if (idUserLain.equals(idUser)) continue;
                                mDatabase.child("chat").child(idUserLain).child(status + "_" + idGroup).child(itemKey).child("read").setValue("1");
                            }
                        }
                    }

                    if (status.equals("personal")) {
                        tvNamaKiri.setVisibility(View.INVISIBLE);
                        tvNamaKanan.setVisibility(View.INVISIBLE);
                    } else {
                        tvNamaKiri.setVisibility(View.VISIBLE);
                        tvNamaKanan.setVisibility(View.VISIBLE);
                    }

                    if (model.getWaktu() != null && calendar != null) {
                        Calendar calendarSekarang = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
                        try {
                            calendarSekarang.setTime(sdf.parse(model.getWaktu()));// all done
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        long bedaWaktuMenit = calendar.getTimeInMillis() - calendarSekarang.getTimeInMillis();
                        bedaWaktuMenit /= 1000;
                        bedaWaktuMenit /= 60;

                        Calendar calendarUntukDitampilkan = Calendar.getInstance();

                        calendarUntukDitampilkan.add(Calendar.MINUTE, (int) -bedaWaktuMenit);

                        String s = String.format("%02d", calendarUntukDitampilkan.get(Calendar.HOUR_OF_DAY)) + ":" + String.format("%02d", calendarUntukDitampilkan.get(Calendar.MINUTE));
                        tvWaktuKiri.setText(s);
                        tvWaktuKanan.setText(s);
                    }
                    tvNamaKiri.setText(model.getNama());
                    tvNamaKanan.setText(model.getNama());
                    tvPesan.setText(model.getPesan());

                    if (model.getRead() != null) {
                        if (model.getRead().equals("1") && model.getId_user().equals(idUser)) {
                            tvReadKiri.setVisibility(View.VISIBLE);
                            tvReadKanan.setVisibility(View.VISIBLE);
                        }
                        else {
                            tvReadKiri.setVisibility(View.INVISIBLE);
                            tvReadKanan.setVisibility(View.INVISIBLE);
                        }
                    }
                    else {
                        tvReadKiri.setVisibility(View.INVISIBLE);
                        tvReadKanan.setVisibility(View.INVISIBLE);
                    }

                }
                else {
                    llInti.setVisibility(View.GONE);
                }
            }

        };
        adapter.startListening();
        lvListView.setAdapter(adapter);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                lvListView.scrollToPosition(lvListView.getAdapter().getItemCount() - 1);
            }
        }, 3000);

        callAsynchronousTask();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout llInti;
        public LinearLayout llChat;
        public BubbleLayout blBubbleLayout;
        public LinearLayout llNamaWaktuKiri;
        public LinearLayout llNamaWaktuKanan;
        public TextView tvNamaKiri;
        public TextView tvPesan;
        public TextView tvWaktuKiri;
        public TextView tvReadKiri;
        public TextView tvNamaKanan;
        public TextView tvWaktuKanan;
        public TextView tvReadKanan;

        public ViewHolder(View v) {
            super(v);
            llInti = v.findViewById(R.id.inti);
            llNamaWaktuKiri = v.findViewById(R.id.nama_waktu_kiri);
            llNamaWaktuKanan = v.findViewById(R.id.nama_waktu_kanan);
            llChat = v.findViewById(R.id.chat);
            blBubbleLayout = v.findViewById(R.id.bubble_layout);
            tvNamaKiri = v.findViewById(R.id.nama_kiri);
            tvPesan = (TextView) v.findViewById(R.id.pesan);
            tvWaktuKiri = v.findViewById(R.id.waktu_kiri);
            tvReadKiri = v.findViewById(R.id.read_kiri);
            tvNamaKanan = v.findViewById(R.id.nama_kanan);
            tvWaktuKanan = v.findViewById(R.id.waktu_kanan);
            tvReadKanan = v.findViewById(R.id.read_kanan);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void callAsynchronousTask() {
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            new RequestServerTime().execute();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 1000); //execute in every 50000 ms
    }

    private class RequestServerTime extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... voids) {
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

            Call<GetTime> call = apiInterface.getTime();
            call.enqueue(new Callback<GetTime>() {
                @Override
                public void onResponse(Call<GetTime> call, Response<GetTime> response) {
                    calendar = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                    try {
                        calendar.setTime(sdf.parse(response.body().getTanggal()));// all done
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(Call<GetTime> call, Throwable t) {
                    t.printStackTrace();
                }
            });
            return null;
        }

        protected void onProgressUpdate() {
        }

        protected void onPostExecute() {
        }
    }

}
