package com.example.alexandryan.tapsecure;

import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.NotificationCompat;

/**
 * Created by asece on 11/25/2016.
 */
public class BankService {
    public static Card VisaCard;
    public static Card DebitCard;
    private static SharedPreferences tapSettings;
    private static BankService bs = new BankService();

    public static BankService getBankService() {return bs;}

    public static final int NOTIFICATION_ID = 1;
    public static void setVisaInfo(Card c) { VisaCard = c; }
    public static void setDebitInfo(Card c) { DebitCard = c; }

    public static void createSharedPref(SharedPreferences s) {tapSettings = s;}
    public static SharedPreferences getSharedPrefs() {return tapSettings;}

    public static void saveCardsToSharedPrefs() {
        //STORE CARD VALUES INTO SHARED PREFS
        SharedPreferences.Editor editor = tapSettings.edit();
        editor.putString("VType", VisaCard.getType());
        editor.putBoolean("VIsCredit", VisaCard.getIsCredit());
        editor.putString("VCardNumber", VisaCard.getCardNumber());
        editor.putString("VCardDescription", VisaCard.getCardDescription());
        editor.putBoolean("VInteracFlashEnabled", VisaCard.getInteracFlashEnabled());
        editor.putBoolean("VTapSecureEnabled", VisaCard.getTapSecureEnabled());
        editor.putBoolean("VTapSecure1MinActive", VisaCard.getTapSecure1MinActive());
        editor.putBoolean("VCumModeEnabled", VisaCard.getCumModeEnabled());
        editor.putFloat("VCumAmount", VisaCard.getCumAmount());
        editor.putFloat("VTapLimit", VisaCard.getTapLimit());
        editor.putFloat("VBalance", VisaCard.getBalance());
        editor.putBoolean("VNotificationsEnabled", VisaCard.getNotificationsEnabled());

        editor.putString("DType", DebitCard.getType());
        editor.putBoolean("DIsCredit", DebitCard.getIsCredit());
        editor.putString("DCardNumber", DebitCard.getCardNumber());
        editor.putString("DCardDescription", DebitCard.getCardDescription());
        editor.putBoolean("DInteracFlashEnabled", DebitCard.getInteracFlashEnabled());
        editor.putBoolean("DTapSecureEnabled", DebitCard.getTapSecureEnabled());
        editor.putBoolean("DTapSecure1MinActive", DebitCard.getTapSecure1MinActive());
        editor.putBoolean("DCumModeEnabled", DebitCard.getCumModeEnabled());
        editor.putFloat("DCumAmount", DebitCard.getCumAmount());
        editor.putFloat("DTapLimit", DebitCard.getTapLimit());
        editor.putFloat("DBalance", DebitCard.getBalance());
        editor.putBoolean("BNotificationsEnabled", DebitCard.getNotificationsEnabled());

        editor.commit();
    }

    public static void loadSharedPrefsIntoCards(){
        //visa
        VisaCard.Type = tapSettings.getString("VType", "TD VISA");
        VisaCard.IsCredit = tapSettings.getBoolean("VIsCredit", true);
        VisaCard.CardNumber = tapSettings.getString("VCardNumber", "2355336522565984");
        VisaCard.CardDescription = tapSettings.getString("VCardDescription", "TD VISA - 2355336522565984");
        VisaCard.InteracFlashEnabled = tapSettings.getBoolean("VInteracFlashEnabled", false);
        VisaCard.TapSecureEnabled = tapSettings.getBoolean("VTapSecureEnabled", false);
        VisaCard.TapSecure1MinActive = tapSettings.getBoolean("VTapSecure1MinActive", false);
        VisaCard.CumAmount = tapSettings.getFloat("VCumAmount", 0.0f);
        VisaCard.TapLimit = tapSettings.getFloat("VTapLimit", 100f);
        VisaCard.Balance = tapSettings.getFloat("VBalance", 0.00f);
        VisaCard.CumModeEnabled = tapSettings.getBoolean("VCumModeEnabled", true);
        VisaCard.NotificationsEnabled = tapSettings.getBoolean("VNotificationsEnabled", false);
        //debit
        DebitCard.Type = tapSettings.getString("DType", "EVERY DAY CHEQUING");
        DebitCard.IsCredit = tapSettings.getBoolean("DIsCredit", false);
        DebitCard.CardNumber = tapSettings.getString("DCardNumber", "3365864");
        DebitCard.CardDescription = tapSettings.getString("DCardDescription", "EVERY DAY CHEQUING - 3365864" );
        DebitCard.InteracFlashEnabled = tapSettings.getBoolean("DInteracFlashEnabled", false);
        DebitCard.TapSecureEnabled = tapSettings.getBoolean("DTapSecureEnabled", false);
        DebitCard.TapSecure1MinActive = tapSettings.getBoolean("DTapSecure1MinActive", false);
        DebitCard.CumAmount = tapSettings.getFloat("DCumAmount", 0.0f);
        DebitCard.TapLimit = tapSettings.getFloat("DTapLimit", 100f);
        DebitCard.Balance = tapSettings.getFloat("DBalance", 2000);
        DebitCard.CumModeEnabled = tapSettings.getBoolean("DCumModeEnabled", true);
        DebitCard.NotificationsEnabled = tapSettings.getBoolean("DNotificationsEnabled", false);
        
    }

    public static String processRequest(ARMessageRequest req) {
        String response = "";
        if(VisaCard.CardNumber.equals(req.CardNumber))
        {
            if(!VisaCard.InteracFlashEnabled)
                response = "Cards tap feature is disabled!";
            else if (VisaCard.InteracFlashEnabled && !VisaCard.TapSecureEnabled) {
                response = processVisa(req.Amount);
                if(response.equals("Transaction Successful"))
                    updateAccount(req, "Visa");
            }
            else if(VisaCard.InteracFlashEnabled && VisaCard.TapSecureEnabled)
            {
                if(!VisaCard.TapSecure1MinActive) {
                    response = "TapSecure enabled: Please tap to phone then terminal!";
                    if (VisaCard.getNotificationsEnabled()) {
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(pubnubService.currentActivity);
                        builder.setSmallIcon(R.drawable.ic_tdlogo);
                        builder.setContentTitle("TD TapSecure Fraud Notification");
                        builder.setContentText("Attempt on Visa card at " + req.Vendor);
                        builder.setAutoCancel(true);
                        NotificationManager notificationManager = (NotificationManager) pubnubService.currentActivity.getSystemService(pubnubService.currentActivity.NOTIFICATION_SERVICE);
                        notificationManager.notify(NOTIFICATION_ID, builder.build());
                    }
                }
                else {
                    response = processVisa(req.Amount);
                    if(response.equals("Transaction Successful"))
                        updateAccount(req, "Visa");
                    if(pubnubService.currentActivity.getClass() == SettingsActivity.class) {
                        Message m = ((SettingsActivity) pubnubService.currentActivity).getTapGUItoggle().obtainMessage();
                        Bundle data = new Bundle();
                        data.putString("message", "Visa");
                        m.setData(data);
                        m.sendToTarget();
                        if(response.equals("Transaction Successful"))
                        {
                            Message ma = ((SettingsActivity)  pubnubService.currentActivity).getCumulativeTotalHandle().obtainMessage();
                            Bundle data2 = new Bundle();
                            data2.putString("type", "Visa");
                            ma.setData(data2);
                            ma.sendToTarget();
                        }
                    }
                }
            }
        }
        else if(DebitCard.CardNumber.equals(req.CardNumber))
        {
            if(!DebitCard.InteracFlashEnabled)
                response = "Cards tap feature is disabled!";
            else if (DebitCard.InteracFlashEnabled && !DebitCard.TapSecureEnabled) {
                response = processDebit(req.Amount);
                if(response.equals("Transaction Successful"))
                    updateAccount(req, "Debit");
            }
            else if(DebitCard.InteracFlashEnabled && DebitCard.TapSecureEnabled)
            {
                if(!DebitCard.TapSecure1MinActive) {
                    response = "TapSecure enabled: Please tap to phone then terminal!";
                    if(DebitCard.getNotificationsEnabled())
                    {
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(pubnubService.currentActivity);
                        builder.setSmallIcon(R.drawable.ic_tdlogo);
                        builder.setContentTitle("TD TapSecure Fraud Notification");
                        builder.setContentText("Attempt on Debit card at " + req.Vendor);
                        builder.setAutoCancel(true);
                        NotificationManager notificationManager = (NotificationManager) pubnubService.currentActivity.getSystemService(pubnubService.currentActivity.NOTIFICATION_SERVICE);
                        notificationManager.notify(NOTIFICATION_ID, builder.build());
                    }
                }
                else {
                    response = processDebit(req.Amount);
                    if(response.equals("Transaction Successful"))
                        updateAccount(req, "Debit");
                    if(pubnubService.currentActivity.getClass() == SettingsActivity.class) {
                        Message m = ((SettingsActivity) pubnubService.currentActivity).getTapGUItoggle().obtainMessage();
                        Bundle data = new Bundle();
                        data.putString("message", "Debit");
                        m.setData(data);
                        m.sendToTarget();
                        if(response.equals("Transaction Successful"))
                        {
                            Message ma = ((SettingsActivity)  pubnubService.currentActivity).getCumulativeTotalHandle().obtainMessage();
                            Bundle data2 = new Bundle();
                            data2.putString("type", "Debit");
                            ma.setData(data2);
                            ma.sendToTarget();
                        }

                    }
                }
            }
        }
        else
            response = "Invalid Card: Please Try Again!";

        return response;
    }

    private static void updateAccount(ARMessageRequest req, String card)
    {
        if(card.equals("Debit"))
        {
            if(DebitCard.getCumModeEnabled()) {
                float currentCumAmount = DebitCard.getCumAmount();
                DebitCard.setCumAmount(currentCumAmount += (float) req.Amount);
            }
            float currentBalance = DebitCard.getBalance();
            DebitCard.setBalance(currentBalance -= (float)req.Amount);
        }
        else
        {
            if(VisaCard.getCumModeEnabled()) {
                float currentCumAmount = VisaCard.getCumAmount();
                VisaCard.setCumAmount(currentCumAmount += (float) req.Amount);
            }
            float currentBalance = VisaCard.getBalance();
            VisaCard.setBalance(currentBalance -= (float)req.Amount);
        }
    }

    public static String processVisa(double amount)
    {
        String result = "";
        if(VisaCard.TapLimit < amount)
            result = "Purchase exceeds tap limit, please insert card!";
        else if(VisaCard.CumModeEnabled)
        {
            if(VisaCard.CumAmount + amount > VisaCard.TapLimit)
                result = "Cumulative tap amount exceeded, Please Insert Card!";
            else
                result = "Transaction Successful";
        }
        else
            result = "Transaction Successful";
        return result;
    }

    public static String processDebit(double amount)
    {
        String result = "";
        if(DebitCard.TapLimit < amount)
            result = "Purchase exceeds tap limit, please insert card!";
        else if(DebitCard.Balance < amount)
            result = "Transaction Declined: Insufficient Funds!";
        else if(DebitCard.CumModeEnabled)
        {
            if(DebitCard.CumAmount + amount > DebitCard.TapLimit)
                result = "Cumulative tap amount exceeded, Please Insert Card!";
            else
                result = "Transaction Successful";
        }
        else
            result = "Transaction Successful";


        return result;
    }


    

}
