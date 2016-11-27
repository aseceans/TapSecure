package com.example.alexandryan.tapsecure;

import android.content.SharedPreferences;

/**
 * Created by asece on 11/25/2016.
 */
public class BankService {
    public static Card VisaCard;
    public static Card DebitCard;
    private static SharedPreferences tapSettings;
    private static BankService bs = new BankService();

    public static BankService getBankService() {return bs;}

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
        
    }

    public static String processRequest(ARMessageRequest req) {
        String response = "";
        if(VisaCard.CardNumber.equals(req.CardNumber))
        {
            if(!VisaCard.InteracFlashEnabled)
                response = "Cards tap feature is disabled!";
            else if (VisaCard.InteracFlashEnabled && !VisaCard.TapSecureEnabled)
                response = processVisa(req.Amount);
            else if(VisaCard.InteracFlashEnabled && VisaCard.TapSecureEnabled)
            {
                if(!VisaCard.TapSecure1MinActive)
                    response = "TapSecure enabled: Please tap to phone then terminal!";
                else
                    response = processVisa(req.Amount);
            }
        }
        else if(DebitCard.CardNumber.equals(req.CardNumber))
        {
            if(!DebitCard.InteracFlashEnabled)
                response = "Cards tap feature is disabled!";
            else if (DebitCard.InteracFlashEnabled && !DebitCard.TapSecureEnabled)
                response = processDebit(req.Amount);
            else if(DebitCard.InteracFlashEnabled && DebitCard.TapSecureEnabled)
            {
                if(!DebitCard.TapSecure1MinActive)
                    response = "TapSecure enabled: Please tap to phone then terminal!";
                else
                    response = processDebit(req.Amount);
            }
        }
        else
            response = "Invalid Card: Please Try Again!";

        return response;
    }

    private static String processVisa(double amount)
    {
        String result = "";
        if(VisaCard.CumModeEnabled)
        {
            if(VisaCard.CumAmount + amount > VisaCard.TapLimit)
                result = "Cumulative tap amount exceeded, cumulative amonunt: " + VisaCard.getCumAmount();
            else
                result = "Transaction Successful";
        }
        else {
            if (VisaCard.TapLimit < amount)
                result = "Tap limited exceeded, Please insert card!";
            else
                result = "Transaction Successful";
        }

        return result;
    }

    public static String processDebit(double amount)
    {
        String result = "";
        if(DebitCard.CumModeEnabled)
        {
            if(DebitCard.CumAmount + amount > DebitCard.TapLimit) {
                result = "Cumulative tap amount exceeded, cumulative amonunt: " + DebitCard.getCumAmount();
            } else if (DebitCard.Balance < amount) {
                result = "Transaction Denied: Insufficient funds";
            }
            else
                result = "Transaction Successful";
        }
        else {
            if (DebitCard.TapLimit < amount) {
                result = "Tap limited exceeded, Please insert card!";
            } else if (DebitCard.Balance < amount) {
                result = "Transaction Denied: Insufficient funds";
            }
            else
                result = "Transaction Successful";
        }

        return result;
    }
    

}
