package in.indekode.hrushi;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WelcomeActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layouts;
    private Button btnSkip, btnNext;
    private PreferenceManager prefManager;
    private static final int PERMISSION_CALL = 1334;
    private static final int PERMISSIONS_REQUEST_CODE = 1240;
    String[] appPermissions = {Manifest.permission.ACCESS_FINE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Checking for first time launch - before calling setContentView()
        prefManager = new PreferenceManager(this);
        if (!prefManager.isFirstTimeLaunch()) {
            launchHomeScreen();
            finish();
        }

        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_welcome);

        viewPager =  findViewById(R.id.view_pager);
        dotsLayout =  findViewById(R.id.layoutDots);
        btnSkip =  findViewById(R.id.btn_skip);
        btnNext =  findViewById(R.id.btn_next);


        layouts = new int[]{
                R.layout.ss1,
                R.layout.ss2,
                R.layout.ss3};

        addBottomDots(0);

        changeStatusBarColor();

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        btnSkip.setOnClickListener(v -> launchHomeScreen());

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int current = getItem(+1);
                if (current < layouts.length) {

                    viewPager.setCurrentItem(current);
                } else {
                    checkAndRequestPermissions();
                    launchHomeScreen();
                }
            }
        });
    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    private void launchHomeScreen() {
        prefManager.setFirstTimeLaunch(false);
        startActivity(new Intent(WelcomeActivity.this, D_P_Activity.class));
        finish();
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);


            if (position == layouts.length - 1) {

                btnNext.setText(getString(R.string.start));
                btnSkip.setVisibility(View.GONE);
            } else {

                btnNext.setText(getString(R.string.next));
                btnSkip.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    };


    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }

    public boolean checkAndRequestPermissions(){

        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String perm : appPermissions){
            if (ContextCompat.checkSelfPermission(this, perm) !=
                    PackageManager.PERMISSION_GRANTED){
                listPermissionsNeeded.add(perm);
            }
        }

        if (!listPermissionsNeeded.isEmpty()){
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    PERMISSIONS_REQUEST_CODE);
            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CODE){
            HashMap<String, Integer> permissionResult = new HashMap<>();
            int deniedCount = 0;

            for (int i=0; i<grantResults.length; i ++) {

                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    permissionResult.put(permissions[i], grantResults[i]);
                    deniedCount ++;
                }
            }

            if (deniedCount == 0) {
                // proceed
            } else {
                for (Map.Entry<String, Integer> entry : permissionResult.entrySet()) {
                    String permName = entry.getKey();
                    int permResult = entry.getValue();

                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, permName)) {
                        // Show dialog of explanation
                        showDialog("", "This app needs location permissions to work",
                                "Yes, Grant Permission",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        checkAndRequestPermissions();
                                    }
                                },
                                "No, Exit App",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        Toast.makeText(WelcomeActivity.this, "Denied", Toast.LENGTH_SHORT).show();
                                    }
                                }, false
                        );
                    } else {

                        showDialog("", "You have denied some permissions. Allow all permission at [Setting] > [Permissions]",
                                "Go to Settings",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        // Go to App Settings
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                                Uri.fromParts("package", getPackageName(), null));
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                },
                                "No, Exit App",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        Toast.makeText(WelcomeActivity.this, "Denied", Toast.LENGTH_SHORT).show();
                                    }
                                }, false);
                        break;
                    }
                }
            }
        }
    }

    public AlertDialog showDialog(String title, String msg, String positiveLabel,
                                  DialogInterface.OnClickListener positiveOnClick,
                                  String negativeLabel, DialogInterface.OnClickListener negativeOnClick,
                                  boolean isCancelable){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setCancelable(isCancelable);
        builder.setMessage(msg);
        builder.setPositiveButton(positiveLabel, positiveOnClick);
        builder.setNegativeButton(negativeLabel, negativeOnClick);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        return alertDialog;
    }


}