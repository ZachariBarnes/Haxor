package com.h4x0r.h4x0r;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.AppodealMediaView;
import com.appodeal.ads.BannerCallbacks;
import com.appodeal.ads.NativeAd;
import com.appodeal.ads.NativeCallbacks;
import com.google.ads.conversiontracking.AdWordsConversionReporter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.NativeExpressAdView;

import java.util.List;

import static com.h4x0r.h4x0r.R.string.menu_native_ad_unit_id;

public class MainMenu extends AppCompatActivity {
    Button startbtn;
    Button optionsbtn;
    Button exitbtn;
    Button creditsbtn;
    Button tutbtn;
    private String APPODEAL_KEY = "e8fcc09bb638dc4b7b62797a301d0b5f6f02f3895ec9217e";
    private String ADMOB_KEY = "e8fcc09bb638dc4b7b62797a301d0b5f6f02f3895ec9217e";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        MobileAds.initialize(getApplicationContext(), ADMOB_KEY);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //Locks portrait mode

        // First Open Haxor
// Google Android first open conversion tracking snippet
// Add this code to the onCreate() method of your application activity

        AdWordsConversionReporter.reportWithConversionId(this.getApplicationContext(),
                "866247408", "DKkACKmGymwQ8MWHnQM", "0.00", false);

        // Locate the buttons in activity_main.xml
        startbtn = (Button) findViewById(R.id.btnStart);
        tutbtn = (Button) findViewById(R.id.btnTutorial);
        optionsbtn = (Button) findViewById(R.id.btnOptions);
        exitbtn = (Button) findViewById(R.id.btnExit);
        creditsbtn = (Button) findViewById(R.id.btnCredits);

       NativeExpressAdView adView = (NativeExpressAdView)findViewById(R.id.nativeAdView);
        AdRequest request = new AdRequest.Builder()
                .build();
        adView.loadAd(request);



        // Capture button clicks
        startbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                // Start NewActivity.class
                Intent gameIntent = new Intent(MainMenu.this,
                        Game.class);
                  startActivityForResult(gameIntent,0);
            }
        });

        // Capture button clicks
        tutbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                // Start NewActivity.class
                Intent gameIntent = new Intent(MainMenu.this,
                        Tutorial.class);
                startActivityForResult(gameIntent,0);
            }
        });

        // Capture button clicks
        optionsbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                final LinearLayout menuLayout = (LinearLayout) findViewById(R.id.menuLayout);
              CreateOptionsLayout(menuLayout);
            }
        });

        creditsbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                // Start NewActivity.class
                Intent creditsIntent = new Intent(MainMenu.this,
                        Credits.class);
                startActivity(creditsIntent);
            }
        });

        // Capture button clicks
        exitbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                // Start NewActivity.class
            finish();
            }
        });


        //Appodeal stuff
        Appodeal.disableNetwork(this, "cheetah");
        Appodeal.initialize(this, APPODEAL_KEY, Appodeal.BANNER);
        Appodeal.show(this, Appodeal.BANNER_BOTTOM);

    }
    @Override
    public void onStop (){
        super.onStop();
    }


    public void CreateOptionsLayout(final LinearLayout menuLayout){
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        MyPreferenceFragment mPrefsFragment = new MyPreferenceFragment();
        ft.replace(android.R.id.content, mPrefsFragment).addToBackStack("Options()");
        ft.commit();
    }
    @Override
    public void onResume() {
        super.onResume();
        Appodeal.onResume(this, Appodeal.BANNER);
    }




}

