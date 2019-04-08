package com.assalam.chatassalam;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
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
import com.assalam.chatassalam.model.Contact;
import com.assalam.chatassalam.model.GetTime;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectContactActivity extends AppCompatActivity {

    private Toolbar tToolbar;
    private LinearLayout llNewGroup;
    private LinearLayout llNewContact;
    private List<Contact> contactList;
    private RecyclerView rvRecyclerView;
    private ContactListAdapter contactListAdapter;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1234;
    private static final String[] PROJECTION = new String[] {
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contact);

        tToolbar = findViewById(R.id.toolbar);
        tToolbar.setTitle("Select contact");
        setSupportActionBar(tToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        llNewGroup = findViewById(R.id.new_group);

        llNewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectContactActivity.this, NewGroupAddParticipantsActivity.class);
                startActivity(intent);
            }
        });

        llNewContact = findViewById(R.id.new_contact);

        llNewContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(SelectContactActivity.this, NewContactActivity.class);
//                startActivity(intent);
                Intent intent = new Intent(Intent.ACTION_INSERT,
                        ContactsContract.Contacts.CONTENT_URI);
                startActivity(intent);
            }
        });

        rvRecyclerView = findViewById(R.id.recycler_view);

        contactList = new ArrayList<>();
        contactListAdapter = new ContactListAdapter(this, contactList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvRecyclerView.setLayoutManager(mLayoutManager);
        rvRecyclerView.setAdapter(contactListAdapter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(SelectContactActivity.this,
                    Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
                    ActivityCompat.requestPermissions(SelectContactActivity.this,
                            new String[]{Manifest.permission.READ_CONTACTS},
                            MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                } else {
                    ActivityCompat.requestPermissions(SelectContactActivity.this,
                            new String[]{Manifest.permission.READ_CONTACTS},
                            MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                }
            } else {
//                getContact();
                new GetContactTask().execute();
            }
        }
        else {
//            getContact();
            new GetContactTask().execute();
        }
//        getContact();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
//                    getContact();
                    new GetContactTask().execute();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void getContact(String contactListJson) {
        String jsonListPhoneNumber = contactListJson;

        SharedPreferences sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        Map<String, String> param = new HashMap<>();
        param.put("no_hp", sharedPreferences.getString("no_hp", ""));
        param.put("list_no_hp", jsonListPhoneNumber);

        Call<List<Contact>> call = apiInterface.contactList(param);
        call.enqueue(new Callback<List<Contact>>() {
            @Override
            public void onResponse(Call<List<Contact>> call, Response<List<Contact>> response) {
                List<Contact> listContact = response.body();

                contactList.clear();

                for (Contact contact : listContact) {
                    contactList.add(contact);
                }

                contactListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<Contact>> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(SelectContactActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class GetContactTask extends AsyncTask<Void, Void, String> {
        protected String doInBackground(Void... voids) {
            JSONArray listPhoneNumber = new JSONArray();
//            ContentResolver cr = getContentResolver();
//            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
//                    null, null, null, null);
//
//            if ((cur != null ? cur.getCount() : 0) > 0) {
//                while (cur != null && cur.moveToNext()) {
//                    String id = cur.getString(
//                            cur.getColumnIndex(ContactsContract.Contacts._ID));
//                    String name = cur.getString(cur.getColumnIndex(
//                            ContactsContract.Contacts.DISPLAY_NAME));
//
//                    if (cur.getInt(cur.getColumnIndex(
//                            ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
//                        Cursor pCur = cr.query(
//                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//                                null,
//                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
//                                new String[]{id}, null);
//                        String phoneNumber = "";
//                        while (pCur.moveToNext()) {
//                            String phoneNo = pCur.getString(pCur.getColumnIndex(
//                                    ContactsContract.CommonDataKinds.Phone.NUMBER));
//                            phoneNumber = phoneNo;
//                        }
//                        phoneNumber = phoneNumber.replaceAll("[^0-9\\+]", "");
//                        listPhoneNumber.put(phoneNumber);
//                        pCur.close();
//                    }
//                }
//            }
//            if(cur!=null){
//                cur.close();
//            }

            Set<String> set = new HashSet<>();

            ContentResolver cr = getContentResolver();
            Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION, null, null, null);

            if (cursor != null) {
                try {
                    final int nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                    final int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                    String name, number;
                    while (cursor.moveToNext()) {
                        name = cursor.getString(nameIndex);
                        number = cursor.getString(numberIndex);
                        String phoneNumber = number.replaceAll("[^0-9\\+]", "");
                        set.add(phoneNumber);
                    }
                } finally {
                    cursor.close();
                }
            }

            for (String s : set) {
                listPhoneNumber.put(s);
            }
            return listPhoneNumber.toString();
        }

        protected void onProgressUpdate() {

        }

        protected void onPostExecute(String result) {
            getContact(result);
        }
    }

    private String getContactList() {
        JSONArray listPhoneNumber = new JSONArray();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    String phoneNumber = "";
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        phoneNumber = phoneNo;
                    }
                    phoneNumber = phoneNumber.replaceAll("[^0-9\\+]", "");
                    listPhoneNumber.put(phoneNumber);
                    pCur.close();
                }
            }
        }
        if(cur!=null){
            cur.close();
        }
        return listPhoneNumber.toString();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.MyViewHolder> {

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
                    Intent intent = new Intent(v.getContext(), ChatActivity.class);
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
                            Query queryRef = mDatabase.child("chat_header").child(idUser).orderByChild("header").startAt("personal_" + tvIdUser.getText().toString() + "_" + idUser);
                            queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (!dataSnapshot.exists()) {
                                        mDatabase.child("chat_header").child(idUser).child("personal_" + tvIdUser.getText().toString() + "_" + idUser).child("header").setValue("personal_" + tvIdUser.getText().toString() + "_" + idUser);
                                        mDatabase.child("chat_header").child(idUser).child("personal_" + tvIdUser.getText().toString() + "_" + idUser).child("nama").setValue(tvNama.getText().toString());
                                        mDatabase.child("chat_header").child(idUser).child("personal_" + tvIdUser.getText().toString() + "_" + idUser).child("pesan_terakhir").setValue("");
                                        mDatabase.child("chat_header").child(idUser).child("personal_" + tvIdUser.getText().toString() + "_" + idUser).child("waktu").setValue(response.body().getTanggal());
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            Query queryRef1 = mDatabase.child("chat_header").child(tvIdUser.getText().toString()).orderByChild("header").startAt("personal_" + idUser + "_" + tvIdUser.getText().toString());
                            queryRef1.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (!dataSnapshot.exists()) {
                                        mDatabase.child("chat_header").child(tvIdUser.getText().toString()).child("personal_" + idUser + "_" + tvIdUser.getText().toString()).child("header").setValue("personal_" + idUser + "_" + tvIdUser.getText().toString());
                                        mDatabase.child("chat_header").child(tvIdUser.getText().toString()).child("personal_" + idUser + "_" + tvIdUser.getText().toString()).child("nama").setValue(nama);
                                        mDatabase.child("chat_header").child(tvIdUser.getText().toString()).child("personal_" + idUser + "_" + tvIdUser.getText().toString()).child("pesan_terakhir").setValue("");
                                        mDatabase.child("chat_header").child(tvIdUser.getText().toString()).child("personal_" + idUser + "_" + tvIdUser.getText().toString()).child("waktu").setValue(response.body().getTanggal());
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


    public ContactListAdapter(Context mContext, List<Contact> contactList) {
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
