package model;

public class Household {
    public int id;
    public String address;
    public Double acreage;
    public Resident head_of_household;
    public int head_of_household_id;

    public Household(int id, String address, Double acreage,  Resident head_of_household) {

        this.id = id;
        this.address = address;
        this.acreage = acreage;
        this.head_of_household = head_of_household;
        this.head_of_household_id = head_of_household.id;
    }
}
