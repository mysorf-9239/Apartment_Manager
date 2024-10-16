package model;

import java.sql.Timestamp;

public class Fee {

    public int id;
    public String fee_name;
	public String fee_description;
    public double amount;
    public Timestamp created_at;
    public Timestamp updated_at;
    public String status;
    public String type;

    public Fee(int id, String fee_name, String fee_description, double amount, Timestamp created_at, Timestamp updated_at, String status) {

        this.id = id;
        this.fee_name = fee_name;
        this.fee_description = fee_description;
        this.amount = amount;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.status = status;
    }

    public Fee(int id, String fee_name, String fee_description, double amount, Timestamp created_at, Timestamp updated_at, String status, String type) {

        this.id = id;
        this.fee_name = fee_name;
        this.fee_description = fee_description;
        this.amount = amount;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.status = status;
        this.type = type;
    }


}
