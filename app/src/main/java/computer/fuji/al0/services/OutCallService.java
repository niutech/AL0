package computer.fuji.al0.services;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import androidx.core.app.ActivityCompat;

import computer.fuji.al0.utils.PhoneNumber;

public class OutCallService {
    public static void addCall(Activity activity, String number) {
        String cleanNumber = PhoneNumber.cleanPhoneNumber(number);
        Uri uri = Uri.parse("tel:".concat(cleanNumber));
        Intent startPhoneCallActivityIntent = new Intent(Intent.ACTION_CALL, uri);
        // Intent startPhoneCallActivityIntent = new Intent(Intent.ACTION_DIAL, uri);
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            activity.startActivity(startPhoneCallActivityIntent);
        } else {
            // do nothing
        }
    }
}
