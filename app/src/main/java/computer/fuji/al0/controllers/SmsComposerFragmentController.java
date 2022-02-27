package computer.fuji.al0.controllers;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

import computer.fuji.al0.fragments.SmsComposerFragment;
import computer.fuji.al0.models.Contact;
import computer.fuji.al0.models.Sms;
import computer.fuji.al0.services.SmsService;

import java.util.ArrayList;
import java.util.Date;

public class SmsComposerFragmentController {
    private SmsComposerFragment fragment;
    private SmsManager smsManager;
    private Contact contact;
    private String smsBody = "";
    private BroadcastReceiver broadcastReceiver;

    private static String SMS_SENT = "SMS_SENT";
    private int messagesPartsCount;
    private boolean isMessageInError = false;

    public SmsComposerFragmentController (SmsComposerFragment smsComposerFragment) {
        this.fragment = smsComposerFragment;
        this.contact = contact;
        smsManager = SmsManager.getDefault();
    }

    public void setContact (Contact contact) {
        this.contact = contact;
    }

    // events
    public void onSmsBodyChange (String newSmsBody) {
        smsBody = newSmsBody;
    }

    public void onButtonSendPress () {
        if (contact == null) {
            fragment.setInSendErrorMode(SmsManager.RESULT_ERROR_GENERIC_FAILURE);
        } else if (smsBody.length() > 0) {
            sendSms();
        }
    }

    public void onButtonClosePress () {
        fragment.onClose();
    }

    public void onFragmentPause () {
        unregisterBroadcastReceiver();
    }

    private void unregisterBroadcastReceiver () {
        if (broadcastReceiver != null) {
            fragment.getContext().unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }
    }

    private void sendSms () {
        unregisterBroadcastReceiver();
        String number = contact.getPhoneNumber();

        PendingIntent sentPendingIntent = PendingIntent.getBroadcast(fragment.getContext(), 0,
                new Intent(SMS_SENT), 0);

        // set fragment in pending mode
        fragment.setInSendingMode();

        // check sms info
        int[] smsMessageStats = SmsMessage.calculateLength(smsBody, false);
        int numberOfSms = smsMessageStats[0];
        messagesPartsCount = numberOfSms;
        isMessageInError = false;

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        if (messagesPartsCount == 1 && !isMessageInError) {
                            Sms sentSms = new Sms("new_sms", contact, Sms.Type.OUTBOUND, smsBody, new Date(), true, true);
                            SmsService.addSms(context, sentSms);
                            // SMS sent
                            // reset fragment ready for a new message
                            smsBody = "";
                            fragment.clearSmsBody();
                            fragment.setInComposeMode();
                            fragment.onSmsSent(sentSms);
                        }
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        // Sms sending error
                        // Generic failure
                        fragment.setInSendErrorMode(SmsManager.RESULT_ERROR_GENERIC_FAILURE);
                        isMessageInError = true;
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        // Sms sending error
                        // No service
                        fragment.setInSendErrorMode(SmsManager.RESULT_ERROR_NO_SERVICE);
                        isMessageInError = true;
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        // Sms sending error
                        // Null PDU
                        fragment.setInSendErrorMode(SmsManager.RESULT_ERROR_NULL_PDU);
                        isMessageInError = true;
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        // Sms sending error
                        // Radio off
                        fragment.setInSendErrorMode(SmsManager.RESULT_ERROR_RADIO_OFF);
                        isMessageInError = true;
                        break;
                }

                messagesPartsCount = messagesPartsCount - 1;
            }
        };

        // register receiver on fragment's context
        fragment.getContext().registerReceiver(broadcastReceiver, new IntentFilter(SMS_SENT));

        if (numberOfSms > 1) {
            // send multipart sms
            ArrayList<String> smsBodyParts = smsManager.divideMessage(smsBody);
            ArrayList<PendingIntent> sentPendingIntents = new ArrayList<>();
            for (int i = 0; i < smsBodyParts.size(); i++) {
                sentPendingIntents.add(PendingIntent.getBroadcast(fragment.getContext(), 0, new Intent(SMS_SENT), 0));
            }
            smsManager.sendMultipartTextMessage(number, null, smsBodyParts, sentPendingIntents, null);
        } else {
            // send the sms
            smsManager.sendTextMessage(number, null, smsBody, sentPendingIntent, null);
        }


        // if (SmsMessage.calculateLength(smsBody))
    }
}
