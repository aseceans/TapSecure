package com.example.alexandryan.tapsecure;
/**
 * Created by asece on 11/25/2016.
 */
public class Card {
    String Type;
    String CardNumber;
    String CardDescription;

    boolean InteracFlashEnabled; //whether this card has interac flash enabled at all
    boolean TapSecureEnabled; //if this is false, there is no one minute timer is false , if this is true, then 1min timer is false
    boolean TapSecure1MinActive; //the 1 minute active one
    boolean CumModeEnabled;

    double CumAmount;
    double TapLimit;
    double Balance; //in case they don't have enough money on account to pay


    public Card (String type, String cardNumber, double balance)
    {
        Type = type;
        CardNumber = cardNumber;
        CardDescription = type + " " + cardNumber;
        InteracFlashEnabled = false;
        TapSecureEnabled = false;
        TapSecure1MinActive = false;
        CumAmount = 0.00;
        TapLimit = 100;
        Balance = balance;
        CumModeEnabled = true; //if cumulative mode
    }

    public void setInteracFlashEnabled(boolean b) {
        this.InteracFlashEnabled = b;
    }
    public boolean getInteracFlashEnabled() {return this.InteracFlashEnabled;}

    public void setTapSecureEnabled(boolean switchIsOn) {
        //when TapSecure is off, set values for default Interac Flash Behaviour
        if (!switchIsOn){
            setTapLimit(100.0);
            setTapSecure1MinActive(false);
            setCumModeEnabled(true);
            setCumAmount(0.0);
        }
        this.TapSecureEnabled = switchIsOn;}
    public boolean getTapSecureEnabled() {return this.TapSecureEnabled;}

    public void setTapSecure1MinActive(boolean b) {this.TapSecure1MinActive = b;}
    public boolean getTapSecure1MinActive() {return this.TapSecure1MinActive;}

    public void setCumModeEnabled(boolean b) {this.CumModeEnabled = b;}
    public boolean getCumModeEnabled() {return this.CumModeEnabled;}

    public void setCumAmount(double d) { this.CumAmount = d;}
    public double getCumAmount() { return this.CumAmount;}

    public void setTapLimit(double d) {this.TapLimit = d;}
    public double getTapLimit() { return this.TapLimit;}

    public void setBalance(double d) {this.Balance = d;}
    public double getBalance() {return this.Balance;}

    public void updateBalance(double d) {this.Balance += d;}
    public void addToCumAmount(double d)  { this.CumAmount += d;}
}
