package com.assalam.chatassalam;

import android.*;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.assalam.chatassalam.api.ApiClient;
import com.assalam.chatassalam.api.ApiInterface;
import com.assalam.chatassalam.model.Contact;
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

public class NewGroupAddParticipantsActivity extends AppCompatActivity {

    private Toolbar tToolbar;
    private List<Contact> contactList;
    private RecyclerView rvRecyclerView;
    private Button bNext;
    private ContactListNewGroupAdapter contactListNewGroupAdapter;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1234;
    private static final String[] PROJECTION = new String[] {
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group_add_participants);

        tToolbar = findViewById(R.id.toolbar);
        tToolbar.setTitle("New group");
        tToolbar.setSubtitle("Add participants");
        setSupportActionBar(tToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        rvRecyclerView = findViewById(R.id.recycler_view);

        contactList = new ArrayList<>();
        contactListNewGroupAdapter = new ContactListNewGroupAdapter(this, contactList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvRecyclerView.setLayoutManager(mLayoutManager);
        rvRecyclerView.setAdapter(contactListNewGroupAdapter);

        bNext = findViewById(R.id.next);

        bNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> listIdUser = new ArrayList<>();
                for (int i = 0; i < rvRecyclerView.getChildCount(); i++) {
                    ContactListNewGroupAdapter.MyViewHolder holder = (ContactListNewGroupAdapter.MyViewHolder) rvRecyclerView.findViewHolderForAdapterPosition(i);
                    ImageView ivCheck = holder.ivCheck;
                    TextView tvIdUser = holder.tvIdUser;
                    if (ivCheck.getVisibility() == View.VISIBLE) {
                        listIdUser.add(tvIdUser.getText().toString());
                    }
                }

                if (listIdUser.isEmpty()) {
                    Toast.makeText(NewGroupAddParticipantsActivity.this, "Paling sedikit 1 kontak harus dipilih", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent(NewGroupAddParticipantsActivity.this, NewGroupAddSubjectActivity.class);

                    intent.putStringArrayListExtra("list_id_user", (ArrayList<String>) listIdUser);

                    startActivity(intent);
                }

            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(NewGroupAddParticipantsActivity.this,
                    android.Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_CONTACTS)) {
                    ActivityCompat.requestPermissions(NewGroupAddParticipantsActivity.this,
                            new String[]{android.Manifest.permission.READ_CONTACTS},
                            MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                } else {
                    ActivityCompat.requestPermissions(NewGroupAddParticipantsActivity.this,
                            new String[]{android.Manifest.permission.READ_CONTACTS},
                            MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                }
            } else {
//                getContact();
                new NewGroupAddParticipantsActivity.GetContactTask().execute();
            }
        }
        else {
//            getContact();
            new NewGroupAddParticipantsActivity.GetContactTask().execute();
        }
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
                    new NewGroupAddParticipantsActivity.GetContactTask().execute();

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

                contactListNewGroupAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<Contact>> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(NewGroupAddParticipantsActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
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
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
class ContactListNewGroupAdapter extends RecyclerView.Adapter<ContactListNewGroupAdapter.MyViewHolder> {

    private Context mContext;
    private List<Contact> contactList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView civGambar;
        public TextView tvIdUser, tvNama, tvStatus;
        public ImageView ivCheck;

        public MyViewHolder(final View view) {
            super(view);
            civGambar = view.findViewById(R.id.gambar);
            tvIdUser = view.findViewById(R.id.id_user);
            tvNama = view.findViewById(R.id.nama);
            tvStatus = view.findViewById(R.id.status);
            ivCheck = view.findViewById(R.id.check);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ivCheck.getVisibility() == View.GONE) {
                        ivCheck.setVisibility(View.VISIBLE);
                    }
                    else {
                        ivCheck.setVisibility(View.GONE);
                    }
                }
            });
        }
    }


    public ContactListNewGroupAdapter(Context mContext, List<Contact> contactList) {
        this.mContext = mContext;
        this.contactList = contactList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_contact_new_group, parent, false);

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
