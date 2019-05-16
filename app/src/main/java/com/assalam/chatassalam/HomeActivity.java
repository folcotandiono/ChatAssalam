package com.assalam.chatassalam;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;
import android.widget.Toast;

import com.assalam.chatassalam.api.ApiClient;
import com.assalam.chatassalam.api.ApiInterface;
import com.assalam.chatassalam.model.Contact;
import com.assalam.chatassalam.model.StatusTaaruf;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity implements StatusFragment.OnFragmentInteractionListener,
ChatFragment.OnFragmentInteractionListener, PanggilanFragment.OnFragmentInteractionListener, TaarufFragment.OnFragmentInteractionListener,
CameraFragment.OnFragmentInteractionListener {

    private Toolbar tToolbar;
    private TabLayout tlTabLayout;
    private ViewPager vpViewPager;
    private ApiInterface apiInterface;
    private SimpleFragmentPagerAdapter simpleFragmentPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        tToolbar = findViewById(R.id.toolbar);
        tToolbar.setTitle("As-Salam");
        setSupportActionBar(tToolbar);

        vpViewPager = (ViewPager) findViewById(R.id.view_pager);
        simpleFragmentPagerAdapter = new SimpleFragmentPagerAdapter(this, getSupportFragmentManager());

        // Set the adapter onto the view pager
        vpViewPager.setAdapter(simpleFragmentPagerAdapter);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(vpViewPager);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_call_black_24dp);
        tabLayout.getTabAt(4).setIcon(R.drawable.ic_camera_alt_black_24dp);

        vpViewPager.setCurrentItem(1);

        SharedPreferences sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        String statusTaaruf = sharedPreferences.getString("status_taaruf", "");

        if (statusTaaruf.equals("1")) {
        }
        else {
            tabLayout.removeTabAt(3);
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_aktivasi_taaruf) {
            Intent intent = new Intent(HomeActivity.this, AktivasiTaarufActivity.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.menu_refresh) {
            SharedPreferences sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
            Map<String, String> param = new HashMap<>();
            param.put("id_user", sharedPreferences.getString("id_user", ""));
            Call<Contact> call = apiInterface.getUser(param);
            call.enqueue(new Callback<Contact>() {
                @Override
                public void onResponse(Call<Contact> call, Response<Contact> response) {
                    Contact contact = response.body();
                    SharedPreferences sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("id_user", contact.getIdUser());
                    editor.putString("username", contact.getUsername());
                    editor.putString("nama", contact.getNama());
                    editor.putString("no_hp", contact.getNoHp());
                    editor.putString("jenis_kelamin", contact.getJenisKelamin());
                    editor.putString("tanggal_lahir", contact.getTanggalLahir());
                    editor.putString("status_taaruf", contact.getStatusTaaruf());
                    editor.putString("gambar", contact.getGambar());
                    editor.apply();

                    Intent intent = getIntent();
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }

                @Override
                public void onFailure(Call<Contact> call, Throwable t) {

                }
            });
        }

        return super.onOptionsItemSelected(item);
    }
}

class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;

    public SimpleFragmentPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    // This determines the fragment for each tab
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new StatusFragment();
        } else if (position == 1){
            return new ChatFragment();
        } else if (position == 2){
            return new PanggilanFragment();
        } else if (position == 3) {
            Fragment fragment = new TaarufFragment();
            return fragment;
        } else if (position == 4) {
            return new CameraFragment();
        } else {
            return null;
        }
    }

    // This determines the number of tabs
    @Override
    public int getCount() {
        return 5;
    }

    // This determines the title for each tab
    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        switch (position) {
            case 0:
                return "STATUS";
            case 1:
                return "CHAT";
            case 2:
                return "";
            case 3:
                return "TA'ARUF";
            case 4:
                return "";
            default:
                return null;
        }
    }

}
