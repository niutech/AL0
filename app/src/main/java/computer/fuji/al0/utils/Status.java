package computer.fuji.al0.utils;

import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import computer.fuji.al0.R;
import computer.fuji.al0.services.SoundManager;

import static android.telephony.CellSignalStrength.SIGNAL_STRENGTH_GOOD;
import static android.telephony.CellSignalStrength.SIGNAL_STRENGTH_GREAT;
import static android.telephony.CellSignalStrength.SIGNAL_STRENGTH_MODERATE;
import static android.telephony.CellSignalStrength.SIGNAL_STRENGTH_NONE_OR_UNKNOWN;
import static android.telephony.CellSignalStrength.SIGNAL_STRENGTH_POOR;

public class Status {
    private static final String signalEmpy = "◦";
    private static final String signalFull = "•";
    // ▲△

    private static boolean getIsSimAvailable (Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        int simState = telephonyManager.getSimState();
        boolean isSimAvailable = simState != TelephonyManager.SIM_STATE_ABSENT && simState != TelephonyManager.SIM_STATE_UNKNOWN;
        return isSimAvailable;
    }

    public static String batteryLevelToString (Context context, int batteryLevel, boolean isCharging) {
        String batteryFullPrefix = context.getString(R.string.main_activity_status_battery);
        String batteryLowPrefix = context.getString(R.string.main_activity_status_battery_almost_empty);
        String batteryEmptyPrefix = context.getString(R.string.main_activity_status_battery_empty);
        String batteryPrefix = batteryLevel < 50 ? batteryLevel < 10 && !isCharging ? batteryEmptyPrefix : batteryLowPrefix : batteryFullPrefix;
        String batteryChargingPrefix = context.getString(R.string.main_activity_status_battery_charging);
        String battery = isCharging ? batteryChargingPrefix.concat(batteryPrefix) : batteryPrefix;
        return battery.concat(" ").concat(String.valueOf(batteryLevel)).concat("%");
    }

    public static boolean isAirplaneModeOn(Context context) {
        // update airplane mode string
        return Settings.System.getInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
    }

    public static String signalToString(Context context, int signalLevel, String networkClass, boolean ignoreSilentMode) {
        String strengthSymbol = "";

        switch (signalLevel) {
            case SIGNAL_STRENGTH_NONE_OR_UNKNOWN:
                strengthSymbol = signalEmpy + signalEmpy + signalEmpy + signalEmpy + " ";
                break;
            case SIGNAL_STRENGTH_POOR:
            default:
                strengthSymbol = signalFull + signalEmpy + signalEmpy + signalEmpy + " ";
                break;
            case SIGNAL_STRENGTH_MODERATE:
                strengthSymbol = signalFull + signalFull + signalEmpy + signalEmpy + " ";
                break;
            case SIGNAL_STRENGTH_GOOD:
                strengthSymbol = signalFull + signalFull + signalFull + signalEmpy + " ";
                break;
            case SIGNAL_STRENGTH_GREAT:
                strengthSymbol = signalFull + signalFull + signalFull + signalFull + " ";
                break;
        }

        if (isAirplaneModeOn(context)) {
            // show airplane mode when airplane mode
            return context.getString(R.string.main_activity_status_signal_airplane_mode);
        } else {
            if (getIsSimAvailable(context)) {
                String signalString = strengthSymbol.concat(networkClass);
                // show silent mode string when in silent mode
                if (!ignoreSilentMode && SoundManager.getIsSilentMode(context)) {
                    return signalString.concat(context.getString(R.string.long_line_spaced)).concat(context.getString(R.string.main_activity_status_silent_mode));
                }
                else {
                    return signalString;
                }
            } else {
                // show sim not available
                String noSimString = context.getString(R.string.main_activity_status_signal_no_sim);
                if (!ignoreSilentMode && SoundManager.getIsSilentMode(context)) {
                    return noSimString.concat(context.getString(R.string.long_line_spaced)).concat(context.getString(R.string.main_activity_status_silent_mode));
                } else {
                    return noSimString;
                }
            }
        }
    }
}
