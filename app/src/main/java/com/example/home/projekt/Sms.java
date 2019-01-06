package com.example.home.projekt;

import android.telephony.SmsManager;

public class Sms {
    public static boolean SendMessage(String messageToSend, String number){
        try {
            SmsManager.getDefault().sendTextMessage(number, null, messageToSend, null,null);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
