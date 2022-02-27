package computer.fuji.al0.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import computer.fuji.al0.R;
import computer.fuji.al0.components.Button;
import computer.fuji.al0.services.NotificationListenerService;
import computer.fuji.al0.utils.Cutout;
import computer.fuji.al0.utils.LockScreen;
import computer.fuji.al0.utils.Preferences;
import computer.fuji.al0.utils.UI;

public class AboutPhoneActivity extends AppCompatActivity {
    private Button buttonClose;
    private Button buttonExitAL0;
    private Button buttonExitAL0Symbol;
    private RelativeLayout logo;
    private TextView labelPhoneManufacturer;
    private TextView labelPhoneModel;
    private TextView labelAndroidVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_phone);
        UI.hideNavigationBar(this);

        // show activity on lock screen
        LockScreen.addShowOnLockScreenFlags(this);

        logo = (RelativeLayout) findViewById(R.id.about_phone_activity_logo);
        buttonClose = (Button) findViewById(R.id.about_phone_activity_button_close);
        buttonExitAL0 = (Button) findViewById(R.id.about_phone_activity_button_keep_lock);
        buttonExitAL0Symbol = (Button) findViewById(R.id.about_phone_activity_button_keep_lock_symbol);
        labelPhoneManufacturer = (TextView) findViewById(R.id.about_phone_label_phone_manufacturer);
        labelPhoneModel = (TextView) findViewById(R.id.about_phone_label_phone_model);
        labelAndroidVersion = (TextView) findViewById(R.id.about_phone_label_phone_version);

        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        buttonExitAL0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exit();
            }
        });

        buttonExitAL0Symbol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exit();
            }
        });

        populateStatistics();
    }

    @Override
    public void onAttachedToWindow () {
        super.onAttachedToWindow();
        // handle cutout
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Cutout cutout = new Cutout(getWindow());
            cutout.addPaddingToViewAtCutoutPosition(logo);
        }
    }

    // populate labels with phone statistics
    private void populateStatistics () {
        String phoneManufacturer = Build.MANUFACTURER;
        String phoneModel = Build.MODEL;
        int version = Build.VERSION.SDK_INT;
        String androidVersion = Build.VERSION.RELEASE;

        labelPhoneManufacturer.setText(phoneManufacturer);
        labelPhoneModel.setText(phoneModel);
        labelAndroidVersion.setText(androidVersion);
    }

    private void exit () {
        Preferences preferences = new Preferences(this);
        // stop notification silencer
        preferences.setNotificationListenerServiceNotificationsEnabled(true);
        // stop app services like screen state change listener
        preferences.setShouldIgnoreAppServices(true);
        // stop lock task, start default home app, finish current activity.
        Intent serviceIntent = new Intent(this, NotificationListenerService.class);
        stopService(serviceIntent);
        // stop pin screen mode
        stopLockTask();
        // start default launcher
        startActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME).setPackage(getPackageManager().queryIntentActivities(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME), PackageManager.MATCH_DEFAULT_ONLY).get(0).activityInfo.packageName));
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
