package computer.fuji.al0.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.telephony.SmsMessage;

import computer.fuji.al0.models.Contact;
import computer.fuji.al0.models.Sms;
import computer.fuji.al0.utils.Preferences;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SmsReceiverService extends BroadcastReceiver {
    public static final String SMS_BUNDLE = "pdus";
    private static Ringtone ringtone;
    private static Vibrator vibrator;

    private static OnSmsReceivedListener onSmsReceivedCallback;

    public interface OnSmsReceivedListener {
        public void onSmsReceived(Sms sms);
    }

    public static void setOnSmsReceivedCallback(SmsReceiverService.OnSmsReceivedListener onSmsReceivedCallback) {
        SmsReceiverService.onSmsReceivedCallback = onSmsReceivedCallback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle intentExtras = intent.getExtras();
        if (intentExtras != null && intentExtras.containsKey(SMS_BUNDLE)) {
            Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);
            Map<String, String> smsMap = smsIntentToSmsMap(context, sms);
            for (String senderNumber : smsMap.keySet()) {
                String smsBody = smsMap.get(senderNumber);
                String address = senderNumber;

                // play ringtone
                playRingtone(context);

                Contact contact = ContactsService.getContactFromPhoneNumber(context, address);
                if (contact == null) {
                    contact = new Contact(address, address, address);
                }
                Sms newSms = new Sms("", contact, Sms.Type.INBOUND, smsBody, new Date(), false, false);

                // store sms
                Sms addedSms = SmsService.addSms(context, newSms);

                // call new sms callback
                if (SmsReceiverService.onSmsReceivedCallback != null) {
                    SmsReceiverService.onSmsReceivedCallback.onSmsReceived(addedSms);
                }

                AL0NotificationService.notifySmsReceived(addedSms);
            }
        }
    }

    // credits:
    // https://github.com/svyd/gtalksms/blob/master/src/com/googlecode/gtalksms/receivers/SmsReceiver.java
    private Map<String, String> smsIntentToSmsMap (Context context, Object[] pdus) {
        Map<String, String> receivedMessagesMap = null;
        SmsMessage[] smsMessages = null;


        int numberOfPdus = pdus.length;
        receivedMessagesMap = new HashMap<String, String>(numberOfPdus);
        smsMessages = new SmsMessage[numberOfPdus];

        // There can be multiple SMS from multiple senders, there can be a maximum of numberOfPdus different senders
        // However, send long SMS of same sender in one message
        for (int i = 0; i < numberOfPdus; i++) {
            smsMessages[i] = SmsMessage.createFromPdu((byte[])pdus[i]);

            String originatingAddress = smsMessages[i].getOriginatingAddress();

            // Check if index with number exists
            if (!receivedMessagesMap.containsKey(originatingAddress)) {
                // Index with number doesn't exist
                // Save string into associative array with sender number as index
                receivedMessagesMap.put(smsMessages[i].getOriginatingAddress(), smsMessages[i].getMessageBody());

            } else {
                // Number has been there, add content but consider that
                // receivedMessagesMap.get(originatingAddress) already contains sms:sndrNbr:previousparts of SMS,
                // so just add the part of the current PDU
                String previousparts = receivedMessagesMap.get(originatingAddress);
                String msgString = previousparts + smsMessages[i].getMessageBody();
                receivedMessagesMap.put(originatingAddress, msgString);
            }
        }

        return receivedMessagesMap;
    }

    // play ringtone
    // use default system ringtone
    private static void playRingtone (Context context) {
        Uri alert = SoundManager.getDefaultNotification(context).getUri();
        ringtone = RingtoneManager.getRingtone(context,  alert);
        ringtone.setAudioAttributes(new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE).build());
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int systemVolume = audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION);

        ringtone.play();

        Preferences preferences = new Preferences(context);
        if (preferences.getSoundSettingsVibrationEnabled()) {
            vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            long[] pattern = {0, 100, 500};
            vibrator.vibrate(pattern, -1);
        }
    }
}
