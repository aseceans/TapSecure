package com.example.alexandryan.tapsecure;
/**
 * Created by asece on 11/25/2016.
 */
public class BankService {
    public static Card VisaCard;
    public static Card DebitCard;
    private static BankService bs = new BankService();

    public static BankService getBankService() {return bs;}
    public static void setVisaInfo(Card c) { VisaCard = c; }
    public static void setDebitInfo(Card c) { DebitCard = c; }
}
