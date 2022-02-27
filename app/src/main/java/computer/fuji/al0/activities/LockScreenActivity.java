package computer.fuji.al0.activities;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import computer.fuji.al0.R;

import computer.fuji.al0.components.Button;
import computer.fuji.al0.utils.LockScreen;
import computer.fuji.al0.utils.Preferences;
import computer.fuji.al0.utils.UI;

public class LockScreenActivity extends AppCompatActivity {

    private Button unlockButton;
    private Button unlockButtonInvisible;
    private Preferences preferences;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // show activity on lock screen
        LockScreen.addShowOnLockScreenFlags(this);

        setContentView(R.layout.activity_lock_screen);
        UI.hideNavigationBar(this);

        preferences = new Preferences(this);

        unlockButton = findViewById(R.id.lock_screen_activity_button_unlock);
        unlockButtonInvisible = findViewById(R.id.lock_screen_activity_button_unlock_invisible);

        unlockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                unlockScreen();
            }
        });

        unlockButtonInvisible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                unlockScreen();
            }
        });
    }

    private void unlockScreen () {
        this.finish();
    }

    @Override
    public void onResume () {
        super.onResume();
        if (preferences.getShouldShowLockScreen()) {
            // do nothing
        } else {
            unlockScreen();
        }
    }
}
