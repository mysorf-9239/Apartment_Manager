package model;

import java.sql.Timestamp;

public class Payment {

    public int id;
    public int payment_amount;
    public Timestamp payment_date;
    public String payment_method;
    public String note;


    public Payment(int id, int payment_amount, Timestamp payment_date, String payment_method, String note) {
        this.id = id;
        this.payment_amount = payment_amount;
        this.payment_date = payment_date;
        this.payment_method = payment_method;
        this.note = note;
    }

}
