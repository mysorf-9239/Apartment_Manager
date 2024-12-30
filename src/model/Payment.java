package model;

import java.sql.Timestamp;

public class Payment {

    public int id;
    public Household household;
    public Fee fee;
    public double payment_amount;
    public Timestamp payment_date;
    public String payment_method;
    public String note;

    public Payment(int id, Household household, Fee fee, double payment_amount, Timestamp payment_date, String payment_method, String note) {
        this.id = id;
        this.household = household;
        this.fee = fee;
        this.payment_amount = payment_amount;
        this.payment_date = payment_date;
        this.payment_method = payment_method;
        this.note = note;
    }
}
