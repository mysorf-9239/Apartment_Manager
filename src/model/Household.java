package model;

public class Household {

    public int id;
    public String address;
    public Resident head_of_household;
    public int head_of_household_id;

    public Household(int id, String address, Resident head_of_household) {

        this.id = id;
        this.address = address;
        this.head_of_household = head_of_household;
    }

    public Household(int id, String address) {

        this.id = id;
        this.address = address;
    }

    public Household(int id, String address, int head_of_household_id) {

        this.id = id;
        this.address = address;
        this.head_of_household_id = head_of_household_id;
    }

}
