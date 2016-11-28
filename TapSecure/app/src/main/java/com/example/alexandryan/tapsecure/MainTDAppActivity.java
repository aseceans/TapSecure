package com.example.alexandryan.tapsecure;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


import static com.example.alexandryan.tapsecure.R.id.fragment_container;

public class MainTDAppActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        TDHomePageFragment.OnFragmentInteractionListener{

    NfcAdapter nfc;

    protected NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tdapp);
        pubnubService.currentActivity = this;
        loadHomeFragment(savedInstanceState);
        createNavDrawerAndToolbar(2);
        createCards();
        NFCService.initNFC();
        pubnubService.PubnubConnect();
    }

    public void createCards(){
        //create the cards
        BankService.getBankService().setDebitInfo(new Card(false));
        BankService.getBankService().setVisaInfo(new Card(true));
        BankService.createSharedPref(getSharedPreferences("settings", Context.MODE_PRIVATE));
        BankService.loadSharedPrefsIntoCards();
        BankService.saveCardsToSharedPrefs();
    }

    public void createNavDrawerAndToolbar(int startidx){
        //Change nav bar Items to Grey starting at index 1
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        for (int i = startidx; i < navigationView.getMenu().size(); ++i){
            MenuItem menuItem = navigationView.getMenu().getItem(i);
            SpannableString s = new SpannableString(menuItem.getTitle());
            s.setSpan(new ForegroundColorSpan(Color.GRAY), 0, s.length(), 0);
            menuItem.setTitle(s);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void loadHomeFragment(Bundle savedInstanceState){
        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            TDHomePageFragment homeFrag = new TDHomePageFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            homeFrag.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, homeFrag).commit();
        }
    }

    public void launchNewFragment(Fragment newFragment){
        // setTitle(titleText);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_tdapp, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            launchNewFragment(new TDHomePageFragment());
        } else if (id == R.id.nav_interacFlash) {
            startActivity(new Intent(this, TapSecureMainActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    // Define a handle to access the context on the main thread. This Handler will handle room created messages.
    private Handler roomCreatedHandle = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            String outputString = "Connected to: " + pubnubService.ROOM_NAME;
            Toast toast = Toast.makeText(pubnubService.currentActivity, outputString, Toast.LENGTH_SHORT);
            toast.show();
        }
    };

    public Handler getRoomCreatedHandle() {return roomCreatedHandle;}

    protected void onNewIntent(Intent intent)
    {
        NFCService.NFConNewIntent(intent, this);
        super.onNewIntent(intent);
    }

    @Override
    protected void onResume() {
        NFCService.NFConResume(MainTDAppActivity.class, this);
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        NFCService.NFConPause(this);
        super.onPause();
    }
}
