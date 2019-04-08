package com.assalam.chatassalam;

import android.content.Context;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity implements StatusFragment.OnFragmentInteractionListener,
ChatFragment.OnFragmentInteractionListener, PanggilanFragment.OnFragmentInteractionListener, TaarufFragment.OnFragmentInteractionListener,
CameraFragment.OnFragmentInteractionListener {

    private Toolbar tToolbar;
    private TabLayout tlTabLayout;
    private ViewPager vpViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        tToolbar = findViewById(R.id.toolbar);
        tToolbar.setTitle("As-Salam");
        setSupportActionBar(tToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        vpViewPager = (ViewPager) findViewById(R.id.view_pager);
        SimpleFragmentPagerAdapter adapter = new SimpleFragmentPagerAdapter(this, getSupportFragmentManager());

        // Set the adapter onto the view pager
        vpViewPager.setAdapter(adapter);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(vpViewPager);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_call_black_24dp);
        tabLayout.getTabAt(4).setIcon(R.drawable.ic_camera_alt_black_24dp);
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

            return true;
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
            return new TaarufFragment();
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
