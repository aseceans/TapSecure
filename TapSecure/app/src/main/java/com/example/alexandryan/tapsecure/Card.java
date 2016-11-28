package com.example.alexandryan.tapsecure;

import android.content.SharedPreferences;

/**
 * Created by asece on 11/25/2016.
 */
public class Card {
    String Type;
    boolean IsCredit; //if this is credit, then the updateBalance method will be adding and balance will represent balance owing
    String CardNumber;
    String CardDescription;

    boolean InteracFlashEnabled; //whether this card has interac flash enabled at all
    boolean TapSecureEnabled; //if this is false, there is no one minute timer is false , if this is true, then 1min timer is false
    boolean TapSecure1MinActive; //the 1 minute active one
    boolean CumModeEnabled;

    float CumAmount;
    float TapLimit;
    float Balance; //for debit it is balance on card, for credit it is balance owing

    public Card(Boolean isCredit){
        this.IsCredit = isCredit;
    }

    public String getType() {return Type;}
    public boolean getIsCredit() {return IsCredit;}
    public String getCardNumber() {return CardNumber;}
    public String getCardDescription() {return CardDescription;}

    public void setInteracFlashEnabled(boolean b) {
        this.InteracFlashEnabled = b;

        SharedPreferences.Editor editor = BankService.getSharedPrefs().edit();
        if(IsCredit) //visa
            editor.putBoolean("VInteracFlashEnabled", this.InteracFlashEnabled);
        else
            editor.putBoolean("DInteracFlashEnabled", this.InteracFlashEnabled);
        editor.commit();
    }
    public boolean getInteracFlashEnabled() {return this.InteracFlashEnabled;}

    public void setTapSecureEnabled(boolean switchIsOn) {
        this.TapSecureEnabled = switchIsOn;
        //when TapSecure is off, set values for default Interac Flash Behaviour
        if (!switchIsOn){
            setTapLimit(100.0f);
            setTapSecure1MinActive(false);
            setCumModeEnabled(true);
            setCumAmount(0.0f);
        }
        SharedPreferences.Editor editor = BankService.getSharedPrefs().edit();
        if(IsCredit){ //visa
            editor.putBoolean("VTapSecureEnabled", this.TapSecureEnabled);
            editor.putFloat("VTapLimit", this.TapLimit);
            editor.putBoolean("VTapSecure1MinActive", this.TapSecure1MinActive);
            editor.putBoolean("VCumModeEnabled", this.CumModeEnabled);
            editor.putFloat("VCumAmount", this.CumAmount);
        }
        else {
            editor.putBoolean("DTapSecureEnabled", this.TapSecureEnabled);
            editor.putFloat("DTapLimit", this.TapLimit);
            editor.putBoolean("DTapSecure1MinActive", this.TapSecure1MinActive);
            editor.putBoolean("DCumModeEnabled", this.CumModeEnabled);
            editor.putFloat("DCumAmount", this.CumAmount);
        }
        editor.commit();
    }
    public boolean getTapSecureEnabled() {return this.TapSecureEnabled;}

    public void setTapSecure1MinActive(boolean b) {
        this.TapSecure1MinActive = b;

        SharedPreferences.Editor editor = BankService.getSharedPrefs().edit();
        if(IsCredit) //visa
            editor.putBoolean("VTapSecure1MinActive", this.TapSecure1MinActive);
        else
            editor.putBoolean("DTapSecure1MinActive", this.TapSecure1MinActive);
        editor.commit();
    }

    public boolean getTapSecure1MinActive() {return this.TapSecure1MinActive;}

    public void setCumModeEnabled(boolean b) {
        this.CumModeEnabled = b;

        SharedPreferences.Editor editor = BankService.getSharedPrefs().edit();
        if(IsCredit) //visa
            editor.putBoolean("VCumModeEnabled", this.CumModeEnabled);
        else
            editor.putBoolean("DCumModeEnabled", this.CumModeEnabled);
        editor.commit();
    }
    public boolean getCumModeEnabled() {return this.CumModeEnabled;}

    public void setCumAmount(float d) {
        this.CumAmount = d;

        SharedPreferences.Editor editor = BankService.getSharedPrefs().edit();
        if(IsCredit) //visa
            editor.putFloat("VCumAmount", this.CumAmount);
        else
            editor.putFloat("DCumAmount", this.CumAmount);
        editor.commit();
    }
    public float getCumAmount() { return this.CumAmount;}

    public void setTapLimit(float d) {
        this.TapLimit = d;

        SharedPreferences.Editor editor = BankService.getSharedPrefs().edit();
        if(IsCredit) //visa
            editor.putFloat("VTapLimit", this.TapLimit);
        else
            editor.putFloat("DTapLimit", this.TapLimit);
        editor.commit();
    }
    public Float getTapLimit() { return this.TapLimit;}

    public void setBalance(Float d) {
        this.Balance = d;

        SharedPreferences.Editor editor = BankService.getSharedPrefs().edit();
        if(IsCredit) //visa
            editor.putFloat("VBalance", this.Balance);
        else
            editor.putFloat("DBalance", this.Balance);
        editor.commit();
    }
    public float getBalance() {return this.Balance;}

    public boolean updateBalance(double transactionAmt) {
        SharedPreferences.Editor editor = BankService.getSharedPrefs().edit();
        if (!IsCredit) { //debit
            if (this.Balance > transactionAmt ) {
                this.Balance -= transactionAmt;
                editor.putFloat("DBalance", this.Balance);
                editor.commit();
                return true;
            } else
                return false;
        }
        else {//visa
            this.Balance += transactionAmt; //balance represents balance owing
            editor.putFloat("VBalance", this.Balance);
            editor.commit();
            return true;
        }
    }

    public void addToCumAmount(double d)  {
        this.CumAmount += d;
        SharedPreferences.Editor editor = BankService.getSharedPrefs().edit();
        if(IsCredit) //visa
            editor.putFloat("VCumAmount", this.CumAmount);
        else
            editor.putFloat("DCumAmount", this.CumAmount);
        editor.commit();
    }
}
