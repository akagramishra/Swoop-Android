package com.techzgamecoding.swoop;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.techzgamecoding.swoop.history_feature.History;
import com.techzgamecoding.swoop.utils.FbAds;
import com.techzgamecoding.swoop.whatsapp_feature.Whatsapp;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.techzgamecoding.swoop.browsing_feature.BrowserManager;
import com.techzgamecoding.swoop.download_feature.fragments.Downloads;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, TextView.OnEditorActionListener{

    private static final String TAG = "VD_Debug";
    private EditText searchTextBar;
    private ImageView btn_search_cancel;
    private ImageView btn_search;
    private BrowserManager browserManager;
    private Uri appLinkData;
    private FragmentManager manager;
    private BottomNavigationView navView;
    private LinearLayout toolbar;
    private String adMode;
    private FbAds fbAds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        // ATTENTION: This was auto-generated to handle app links.
        Intent appLinkIntent = getIntent();
        //String appLinkAction = appLinkIntent.getAction();
        appLinkData = appLinkIntent.getData();

        manager = this.getSupportFragmentManager();
        // This is for creating browser manager fragment
        if ((browserManager = (BrowserManager) this.getSupportFragmentManager().findFragmentByTag("BM")) == null)
        {
            manager.beginTransaction()
                    .add(browserManager = new BrowserManager(), "BM").commit();
        }

        // Bottom navigation
        navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navView.setItemIconTintList(null);

        toolbar = findViewById(R.id.toolbar);

        setUPBrowserToolbarView();
        setUpVideoSites();


    }

    private void setUPBrowserToolbarView(){

        // Toolbar search
        btn_search_cancel = findViewById(R.id.btn_search_cancel);
        btn_search_cancel.setOnClickListener(this);
        btn_search = findViewById(R.id.btn_search);
        searchTextBar = findViewById(R.id.et_search_bar);

        /*hide/show clear button in search view*/
        TextWatcher searchViewTextWatcher = new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().trim().length()==0){
                    btn_search_cancel.setVisibility(View.GONE);
                } else {
                    btn_search_cancel.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}
        };
        searchTextBar.addTextChangedListener(searchViewTextWatcher);
        searchTextBar.setOnEditorActionListener(this);
        btn_search.setOnClickListener(this);

        //Toolbar home button
        ImageView toolbar_home = findViewById(R.id.btn_home);
        toolbar_home.setOnClickListener(this);

        //Settings
        ImageView settings = findViewById(R.id.btn_settings);
        settings.setOnClickListener(this);
    }

    private void setUpVideoSites(){
        // Video sites link
        RecyclerView videoSites = findViewById(R.id.rvVideoSitesList);
        videoSites.setLayoutManager(new GridLayoutManager(MainActivity.this, 3));
        videoSites.setAdapter(new VideoSitesList(this));
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    homeClicked();
                    return true;
                case R.id.navigation_downloads:
                    downloadClicked();
                    return true;
                case R.id.navigation_history:
                    historyClicked();
                    return true;
            }
            return false;
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_search_cancel:
                searchTextBar.getText().clear();
                break;
            case R.id.btn_home:
                showToolbar();
                searchTextBar.getText().clear();
                getBrowserManager().closeAllWindow();
                break;
            case R.id.btn_settings:
                settingsClicked();
                break;
            case R.id.btn_search:
                new WebConnect(searchTextBar, this).connect();
                break;
        }
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        boolean handled = false;
        if (actionId == EditorInfo.IME_ACTION_GO) {
            new WebConnect(searchTextBar, this).connect();
        }
        return handled;
    }

    @Override
    public void onBackPressed() {
        if (manager.findFragmentByTag("Downloads") != null ||
                manager.findFragmentByTag("History") != null ||
                    manager.findFragmentByTag("Whatsapp") != null) {
            VDApp.getInstance().getOnBackPressedListener().onBackpressed();
            browserManager.resumeCurrentWindow();
            navView.setSelectedItemId(R.id.navigation_home);
        }
        else if (manager.findFragmentByTag("Settings") != null) {
            VDApp.getInstance().getOnBackPressedListener().onBackpressed();
            browserManager.resumeCurrentWindow();
            navView.setVisibility(View.VISIBLE);
        }
        else if (VDApp.getInstance().getOnBackPressedListener() != null) {
            VDApp.getInstance().getOnBackPressedListener().onBackpressed();
        } else {
            new AlertDialog.Builder(this)
                    .setMessage("Are you sure you want to exit?")
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.super.onBackPressed();
                        }
                    })
                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .create()
                    .show();
        }
    }

    public BrowserManager getBrowserManager() {
        return browserManager;
    }

    public interface OnBackPressedListener {
        void onBackpressed();
    }

    public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
        VDApp.getInstance().setOnBackPressedListener(onBackPressedListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (appLinkData != null) {
            browserManager.newWindow(appLinkData.toString());
        }
        browserManager.updateAdFilters();
    }

    public void browserClicked() {
        browserManager.unhideCurrentWindow();
    }

    private void downloadClicked(){
        closeHistory();
        closeWhatsapp();
        if (manager.findFragmentByTag("Downloads") == null) {
            browserManager.hideCurrentWindow();
            browserManager.pauseCurrentWindow();
            manager.beginTransaction().add(R.id.main_content, new Downloads(), "Downloads").commit();
        }
    }

    public void whatsappClicked(){
        closeDownloads();
        closeHistory();
        if (manager.findFragmentByTag("Whatsapp") == null) {
            browserManager.hideCurrentWindow();
            browserManager.pauseCurrentWindow();
            manager.beginTransaction().add(R.id.main_content, new Whatsapp(), "Whatsapp").commit();
        }
    }

    private void historyClicked(){
        closeDownloads();
        closeWhatsapp();
        if (manager.findFragmentByTag("History") == null) {
            browserManager.hideCurrentWindow();
            browserManager.pauseCurrentWindow();
            manager.beginTransaction().add(R.id.main_content, new History(), "History").commit();
        }
    }

    private void settingsClicked(){
        if (manager.findFragmentByTag("Settings") == null) {
            browserManager.hideCurrentWindow();
            browserManager.pauseCurrentWindow();
            navView.setVisibility(View.GONE);
            manager.beginTransaction().add(R.id.main_content, new Settings(), "Settings").commit();
        }
    }

    private void homeClicked(){
        browserManager.unhideCurrentWindow();
        browserManager.resumeCurrentWindow();
        closeDownloads();
        closeHistory();
        closeWhatsapp();
    }

    private void closeDownloads() {
        Fragment fragment = manager.findFragmentByTag("Downloads");
        if (fragment != null) {
            manager.beginTransaction().remove(fragment).commit();
        }
    }

    private void closeHistory() {
        Fragment fragment = manager.findFragmentByTag("History");
        if (fragment != null) {
            manager.beginTransaction().remove(fragment).commit();
        }
    }

    private void closeWhatsapp() {
        Fragment fragment = manager.findFragmentByTag("Whatsapp");
        if (fragment != null) {
            manager.beginTransaction().remove(fragment).commit();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        onRequestPermissionsResultCallback.onRequestPermissionsResult(requestCode, permissions,
                grantResults);
    }

    private ActivityCompat.OnRequestPermissionsResultCallback onRequestPermissionsResultCallback;

    public void setOnRequestPermissionsResultListener(ActivityCompat
                                                              .OnRequestPermissionsResultCallback
                                                              onRequestPermissionsResultCallback) {
        this.onRequestPermissionsResultCallback = onRequestPermissionsResultCallback;
    }

    public void hideToolbar(){
        toolbar.setVisibility(View.GONE);
    }

    public void showToolbar(){
        toolbar.setVisibility(View.VISIBLE);
    }

    public void loadInterstitialAd(){
        if(adMode.equals("0"))
            fbAds.loadInterstitialAd();

    }
}
