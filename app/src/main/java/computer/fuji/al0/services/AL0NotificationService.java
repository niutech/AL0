package computer.fuji.al0.services;

import android.content.Context;

import java.util.ArrayList;

import computer.fuji.al0.models.Call;
import computer.fuji.al0.models.Sms;
import computer.fuji.al0.utils.Preferences;

public class AL0NotificationService {
    private static NotificationListener notificationListener;
    private static boolean lastPhoneNotificationIsSeen = true;
    private static ArrayList<String> lastExternalAppsNotificationsNotSeen = new ArrayList<>();

    public interface NotificationListener {
        public void onSmsReceived(Sms sms);
        public void onMissedCallReceived(Call call);
        public void onExternalAppNotification(String appId);
    }

    public static boolean getLastPhoneNotificationIsSeen() {
        return AL0NotificationService.lastPhoneNotificationIsSeen;
    }

    public static ArrayList<String> getLastExternalAppsNotificationsNotSeen (Context context) {
        // return lastExternalAppsNotificationsNotSeen;
        Preferences preferences = new Preferences(context);
        return preferences.getExternalAppNotificationNotSeenIds();
    }

    // setters
    public static void setLastPhoneNotificationIsSeen(boolean lastPhoneNotificationIsSeen) {
        AL0NotificationService.lastPhoneNotificationIsSeen = lastPhoneNotificationIsSeen;
    }

    public static void setLastExternalAppsNotificationsIsSeen(String appId, Context context) {
        // remove notification app id from list
        for (int i = 0; i < lastExternalAppsNotificationsNotSeen.size(); i++) {
            if (lastExternalAppsNotificationsNotSeen.get(i).equals(appId)) {
                lastExternalAppsNotificationsNotSeen.remove(i);
            }
        }

        // store changes
        Preferences preferences = new Preferences(context);
        preferences.setExternalAppNotificationNotSeenIds(lastExternalAppsNotificationsNotSeen);
    }

    public static void setNotificationListener (NotificationListener notificationListener) {
        AL0NotificationService.notificationListener = notificationListener;
    }

    // events
    public static void notifySmsReceived (Sms sms) {
        lastPhoneNotificationIsSeen = false;
        if (notificationListener != null) {
            notificationListener.onSmsReceived(sms);
        }
    }

    public static void notifyMissedCallReceived (Call call) {
        lastPhoneNotificationIsSeen = false;
        if (notificationListener != null) {
            notificationListener.onMissedCallReceived(call);
        }
    }

    public static void notifyExternalAppNotificationReceived (String appId, Context context) {
        // make sure to remove app id from list to prevent having same id multiple time
        setLastExternalAppsNotificationsIsSeen(appId, context);
        lastExternalAppsNotificationsNotSeen.add(appId);

        // store updated notifications
        Preferences preferences = new Preferences(context);
        preferences.setExternalAppNotificationNotSeenIds(lastExternalAppsNotificationsNotSeen);

        // notify attached listeners

        if (notificationListener != null) {
            notificationListener.onExternalAppNotification(appId);
        }
    }
}
