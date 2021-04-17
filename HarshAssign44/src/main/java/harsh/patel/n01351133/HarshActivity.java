package harsh.patel.n01351133;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;



import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.SEND_SMS;

public class HarshActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    final private int SEND_SMS_PERMISSION_CHECK = 1;
    final private int LOCATION_PERMISSION_CHECK = 2;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    public static final String SENT = "SMS_SENT";
    public static final String SHARED_PREF = "userPref";
    public static final String ASWTICH = "switch";
    public static final String FONT_SIZE = "fontSize";
    private boolean switchOnOff;
    private int selectedFontSize;
    ImageButton imgBtn;
    HarshSharedViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = ViewModelProviders.of(this).get(HarshSharedViewModel.class);
        sharedPref = getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);

        selectedFontSize = sharedPref.getInt(FONT_SIZE, 3);
        setFontSize(selectedFontSize);
        switchOnOff = sharedPref.getBoolean(ASWTICH, false);
        if (switchOnOff) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
        }

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.Harsh_toolbar); setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.Harsh_drawer_layout);
        NavigationView navigationView = findViewById(R.id.Harsh_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerview = navigationView.getHeaderView(0);
//        imgBtn = headerview.findViewById(R.id.HarshimageView);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.Harsh_fragment_container,
                    new HomeFrag()).commit();
            navigationView.setCheckedItem(R.id.Harsh_nav_home);
        }



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the tools bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.HarshHelp:
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://google.com"));
                startActivity(intent);
                break;
            case R.id.HarshLocation:
                checkLocationPermission();
                break;
            case R.id.HarshSMS:
                checkSmsPermission();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Harsh_nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.Harsh_fragment_container,
                        new HomeFrag()).commit();
                break;
            case R.id.Harsh_nav_download:
                getSupportFragmentManager().beginTransaction().replace(R.id.Harsh_fragment_container,
                        new DownloadFrag()).commit();
                break;
            case R.id.Harsh_nav_weather:
                getSupportFragmentManager().beginTransaction().replace(R.id.Harsh_fragment_container,
                        new WebServiceFrag()).commit();

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_CHECK:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    displayLocation();
                }
                break;
            case SEND_SMS_PERMISSION_CHECK:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendSMS();
                }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void checkLocationPermission() {
        FusedLocationProviderClient fusedLocationClient;
        fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(this);
        // Check if we have permission to access high accuracy fine location.

        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,
                    new String[]{ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_CHECK);
            return;
        } else {
//            fusedLocationClient.getLastLocation()
//                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
//                        @Override
//                        public void onSuccess(Location location) {
//                            // Got last known location. In some rare situations this can be null.
//                            // Logic to handle location object
//                            showSnackBar(location);
//                        }
//                    });
            displayLocation();
        }
    }

    public void displayLocation() {
        FusedLocationProviderClient fusedLocationClient;
        fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        // Logic to handle location object
                        showSnackBar(location);
                    }
                });
    }


    public void showSnackBar(Location location) {
        String latLongString = getString(R.string.no_location_found);
        if (location != null) {
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            latLongString = getString(R.string.latitude) + lat + "\n" + getString(R.string.longitude) + lng;
        }
        Snackbar snackBar = Snackbar.make(drawerLayout, latLongString, Snackbar.LENGTH_LONG);
        snackBar.show();
    }

    public void checkSmsPermission() {
        if (ContextCompat.checkSelfPermission(this, SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            // request permission (see result in onRequestPermissionsResult() method)
            ActivityCompat.requestPermissions(this,
                    new String[]{SEND_SMS},
                    SEND_SMS_PERMISSION_CHECK);
        } else {
            // permission already granted run sms send
            sendSMS();
        }
    }

    private void sendSMS() {
        // TODO Auto-generated method stub
        String phoneNo = getString(R.string.sms_number);
        String message = getString(R.string.sms_message);
        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
                new Intent(SENT), 0);

        //---when the SMS has been sent---
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        showSmsSnackBar(getString(R.string.sms_sent));
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        showSmsSnackBar(getString(R.string.generic_failure));
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        showSmsSnackBar(getString(R.string.no_service));
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        showSmsSnackBar(getString(R.string.null_pdu));
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        showSmsSnackBar(getString(R.string.radio_off));
                        break;
                }
            }
        }, new IntentFilter(SENT));

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNo, null, message, sentPI, null);
    }

    public void showSmsSnackBar(String msg) {
        Snackbar snackbar = Snackbar.make(drawerLayout, msg, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
            exitApp();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        viewModel.getFontSize().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                setFontSize(integer);
            }
        });

        viewModel.getOrientation().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean portrait) {
                if (portrait) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
                }
            }
        });
    }

    public void exitApp() {
        DialogInterface.OnClickListener dlgListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        finish();
                        // This above line close correctly
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String exitMsg = getString(R.string.exit_app);
        builder.setMessage(exitMsg)
                .setIcon(R.drawable.ic_exit)
                .setTitle(R.string.exit)
                .setPositiveButton(R.string.yes, dlgListener)
                .setNegativeButton(R.string.no, dlgListener).show();
    }

    private void setFontSize(int size) {
        switch (size) {
            case 0:
                setTheme(R.style.font12);
                break;
            case 1:
                setTheme(R.style.font13);
                break;
            case 2:
                setTheme(R.style.font14);
                break;
            case 3:
            default:
                setTheme(R.style.font15);
                break;
        }
    }
}