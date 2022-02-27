package computer.fuji.al0.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.display.DisplayManager;
import android.service.notification.StatusBarNotification;
import android.view.Display;

import java.util.ArrayList;

import computer.fuji.al0.utils.Preferences;

public class NotificationListenerService extends android.service.notification.NotificationListenerService {
    private NotificationListenerServiceReveicer notificationListenerServiceReveicer;
    private Preferences preferences;
    private DisplayManager displayManager;
    private static NotificationListenerService notificationListenerService;

    private String TAG = this.getClass().getSimpleName();
    private String INTENT_ACTION_SERVICE = "computer.fuji.al0.NOTIFICATION_LISTENER_SERVICE";
    private String INTENT_ACTION_LISTENER = "computer.fuji.al0.NOTIFICATION_LISTENER";

    private static final String telecomPackage = "com.android.server.telecom";

    private static ArrayList<NotificationReference> missedCallNotifications = new ArrayList<>();

    private static final String[] whiteListPackages = new String[] {
            telecomPackage
    };

    private boolean getIsDisplayOn () {
        if (displayManager != null) {
            for (Display display : displayManager.getDisplays()) {
                if (display.getState() != Display.STATE_OFF) {
                    return true;
                }
            }
        }

        return false;
    }

    public static void cancelMissedCallNotifications () {
        for (NotificationReference notification : missedCallNotifications) {
            notificationListenerService.cancelNotification(notification.getKey());
            notificationListenerService.cancelNotification(notification.getPackageName(), notification.getTag(), notification.getId());
        }

        missedCallNotifications.clear();
    }

    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        preferences = new Preferences(getApplicationContext());
        displayManager = (DisplayManager) getApplicationContext().getSystemService(Context.DISPLAY_SERVICE);
        notificationListenerServiceReveicer = new NotificationListenerServiceReveicer();
        IntentFilter filter = new IntentFilter();
        filter.addAction(INTENT_ACTION_SERVICE);
        registerReceiver(notificationListenerServiceReveicer, filter);
        notificationListenerService = this;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(notificationListenerServiceReveicer);
    }

    @Override
    public void onNotificationPosted(final StatusBarNotification sbn, RankingMap rankingMap) {
        boolean shouldEnableNotification = preferences.getNotificationListenerServiceNotificationsEnabled();
        boolean isInFocusMode = preferences.getIsFocusMode();
        boolean areStandardAppsSilent = preferences.getSoundSettingsStandardAppsSilent();
        String notificationPackage = sbn.getPackageName();
        boolean isAppNotificationAllowed = false;
        // notify when get notification from external home listed app

        // get external apps
        String externalApp1 = preferences.getExternalApp1();
        String externalApp2 = preferences.getExternalApp2();
        String externalApp3 = preferences.getExternalApp3();
        if (notificationPackage.equals(externalApp1) || notificationPackage.equals(externalApp2) || notificationPackage.equals(externalApp3)) {
            AL0NotificationService.notifyExternalAppNotificationReceived(notificationPackage, getApplicationContext());
            isAppNotificationAllowed = true;
        }

        // allow white listed package for notifications
        for (String allowedPackage : whiteListPackages) {
            if (sbn.getPackageName().equals(allowedPackage) || isAppNotificationAllowed || shouldEnableNotification) {
                // if is telecom package add to missed call notifications list
                if (sbn.getPackageName().equals(telecomPackage)) {
                    missedCallNotifications.add(new NotificationReference(sbn.getKey(), sbn.getPackageName(), sbn.getTag(), sbn.getId()));
                }
                return;
            }
        }

        /*
        TODO check is a call notification, prevent turn screen on
        if (sbn.getNotification().category.equals(Notification.CATEGORY_CALL)) {
            Log.d("NotificationListener", "is display on: " + getIsDisplayOn());
        }
         */
        // cancel notifications
        // cancelAllNotifications();
        cancelNotification(sbn.getKey());
        cancelNotification(sbn.getPackageName(), sbn.getTag(), sbn.getId());
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        boolean shouldEnableNotification = preferences.getNotificationListenerServiceNotificationsEnabled();
        boolean isInFocusMode = preferences.getIsFocusMode();
        boolean areStandardAppsSilent = preferences.getSoundSettingsStandardAppsSilent();
        String notificationPackage = sbn.getPackageName();
        boolean isAppNotificationAllowed = false;
        // notify when get notification from external home listed app

        // get external apps
        String externalApp1 = preferences.getExternalApp1();
        String externalApp2 = preferences.getExternalApp2();
        String externalApp3 = preferences.getExternalApp3();
        if (notificationPackage.equals(externalApp1) || notificationPackage.equals(externalApp2) || notificationPackage.equals(externalApp3)) {
            AL0NotificationService.notifyExternalAppNotificationReceived(notificationPackage, getApplicationContext());
            isAppNotificationAllowed = true;
        }

        // allow white listed package for notifications
        for (String allowedPackage : whiteListPackages) {
            if (sbn.getPackageName().equals(allowedPackage) || isAppNotificationAllowed || shouldEnableNotification) {
                return;
            }
        }

        Intent i = new Intent(INTENT_ACTION_LISTENER);
        i.putExtra("notification_event","onNotificationPosted :" + sbn.getPackageName() + "\n");
        sendBroadcast(i);
        // cancel notification
        // cancelAllNotifications();
        cancelNotification(sbn.getKey());
        cancelNotification(sbn.getPackageName(), sbn.getTag(), sbn.getId());
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        // retrieve allowed apps
        String externalApp1 = preferences.getExternalApp1();
        String externalApp2 = preferences.getExternalApp2();
        String externalApp3 = preferences.getExternalApp3();
        String removedNotificationAppId = sbn.getPackageName();
        if (removedNotificationAppId.equals(externalApp1)  || removedNotificationAppId.equals(externalApp2) || removedNotificationAppId.equals(externalApp3) ) {
            AL0NotificationService.setLastExternalAppsNotificationsIsSeen(sbn.getPackageName(), getApplicationContext());
        }

        Intent i = new  Intent(INTENT_ACTION_LISTENER);
        i.putExtra("notification_event","onNotificationRemoved :" + sbn.getPackageName() + "\n");

        sendBroadcast(i);
    }

    class NotificationListenerServiceReveicer extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean shouldEnableNotification = preferences.getNotificationListenerServiceNotificationsEnabled();

            if (intent.getStringExtra("command").equals("clearall") && !shouldEnableNotification){
                NotificationListenerService.this.cancelAllNotifications();
            }
            else if (intent.getStringExtra("command").equals("list") && !shouldEnableNotification){
                Intent i1 = new  Intent(INTENT_ACTION_LISTENER);
                i1.putExtra("notification_event","=====================");
                sendBroadcast(i1);
                int i=1;
                for (StatusBarNotification sbn : NotificationListenerService.this.getActiveNotifications()) {
                    Intent i2 = new  Intent(INTENT_ACTION_LISTENER);
                    i2.putExtra("notification_event",i +" " + sbn.getPackageName() + "\n");
                    sendBroadcast(i2);
                    i++;
                }
                Intent i3 = new  Intent(INTENT_ACTION_LISTENER);
                i3.putExtra("notification_event","===== Notification List ====");
                sendBroadcast(i3);
            }

        }
    }

    private class NotificationReference {
        private String key;
        private String packageName;
        private String tag;
        private int id;

        public NotificationReference (String key, String packageName, String tag, int id) {
            this.key = key;
            this.packageName = packageName;
            this.tag = tag;
            this.id = id;
        }

        public String getKey () {
            return this.key;
        }

        public String getPackageName () {
            return this.packageName;
        }

        public String getTag () {
            return this.tag;
        }

        public int getId () {
            return this.id;
        }
    }

}
