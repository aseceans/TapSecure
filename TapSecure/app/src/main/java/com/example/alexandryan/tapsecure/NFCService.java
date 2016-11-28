package com.example.alexandryan.tapsecure;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Message;
import android.os.Parcelable;
import android.widget.Toast;

/**
 * Created by Ryan-A-Laptop on 2016-11-27.
 */

public class NFCService {
    public static NfcAdapter nfc;

    public static void initNFC()
    {
        nfc = NfcAdapter.getDefaultAdapter(pubnubService.currentActivity);
    }

    public static void NFConNewIntent(Intent in, Activity av)
    {
        Parcelable[] parcelables = in.getParcelableArrayExtra(nfc.EXTRA_NDEF_MESSAGES);
        String cardType = readTextFromMessage((NdefMessage) parcelables[0]);
        if(parcelables != null && parcelables.length > 0) {
            if(cardType.equals("Visa"))
            {
                if(!BankService.VisaCard.InteracFlashEnabled)
                    Toast.makeText(pubnubService.currentActivity, "Visa cards tap is disabled!", Toast.LENGTH_SHORT).show();
                else if (BankService.VisaCard.InteracFlashEnabled && !BankService.VisaCard.TapSecureEnabled)
                    Toast.makeText(pubnubService.currentActivity, "Visa cards tapsecure is disabled!", Toast.LENGTH_SHORT).show();
                else if(BankService.VisaCard.InteracFlashEnabled && BankService.VisaCard.TapSecureEnabled)
                {
                    if(pubnubService.currentActivity.getClass() != SettingsActivity.class) {
                        Intent myIntent = new Intent(av, SettingsActivity.class);
                        myIntent.putExtra("name", cardType);
                        av.startActivity(myIntent);
                    }
                    else
                    {
                        Message m = ((SettingsActivity)pubnubService.currentActivity).getTapHandle().obtainMessage();
                        Bundle data = new Bundle();
                        data.putString("message", cardType);
                        m.setData(data);
                        m.sendToTarget();
                    }
                }
            }
            else if(cardType.equals("Debit"))
            {
                if(!BankService.DebitCard.InteracFlashEnabled)
                    Toast.makeText(pubnubService.currentActivity, "Debit cards tap is disabled!", Toast.LENGTH_SHORT).show();
                else if (BankService.DebitCard.InteracFlashEnabled && !BankService.DebitCard.TapSecureEnabled)
                    Toast.makeText(pubnubService.currentActivity, "Debit cards tapsecure is disabled!", Toast.LENGTH_SHORT).show();
                else if(BankService.DebitCard.InteracFlashEnabled && BankService.DebitCard.TapSecureEnabled)
                {
                    if(pubnubService.currentActivity.getClass() != SettingsActivity.class) {
                        Intent myIntent = new Intent(av, SettingsActivity.class);
                        myIntent.putExtra("name", cardType);
                        av.startActivity(myIntent);
                    }
                    else
                    {
                        Message m = ((SettingsActivity)pubnubService.currentActivity).getTapHandle().obtainMessage();
                        Bundle data = new Bundle();
                        data.putString("message", cardType);
                        m.setData(data);
                        m.sendToTarget();
                    }
                }
            }
            else if(cardType.equals("nocard")) {
                Toast.makeText(pubnubService.currentActivity, "Invalid Card", Toast.LENGTH_SHORT).show();
            }
        }else
        {
            Toast.makeText(pubnubService.currentActivity, "No Message!!", Toast.LENGTH_SHORT).show();
        }
    }

    private static String readTextFromMessage(NdefMessage ndef) {
        NdefRecord[] ndefRecords = ndef.getRecords();
        if(ndefRecords != null && ndefRecords.length > 0)
        {
            NdefRecord ndefrecord = ndefRecords[0];
            byte[] payload = ndefrecord.getPayload();
            String payloadString = new String(payload);
            payloadString = payloadString.substring(3);
            if(payloadString.equals(BankService.VisaCard.CardNumber))
                return "Visa";
            else if(payloadString.equals(BankService.DebitCard.CardNumber))
                return "Debit";
            else
                return "nocard";
        }else {
            Toast.makeText(pubnubService.currentActivity, "No Records!!", Toast.LENGTH_SHORT).show();
            return "nocard";
        }
    }
    public static void NFConResume(Class c, Activity av) {
        pubnubService.currentActivity = av;
        Intent intent = new Intent(pubnubService.currentActivity, c);
        intent.addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
        PendingIntent pendingIntent = PendingIntent.getActivity(pubnubService.currentActivity, 0, intent, 0);
        IntentFilter[] intentFilter = new IntentFilter[]{};
        nfc.enableForegroundDispatch(av, pendingIntent, intentFilter, null);
    }

    public static void NFConPause(Activity av)
    {
        nfc.disableForegroundDispatch(av);
    }
}
