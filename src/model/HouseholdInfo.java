package model;

public class HouseholdInfo {
    public String headOfHouseholdName;
    public String address;
    public String feeStatus;

    public HouseholdInfo(String headOfHouseholdName, String address, String feeStatus) {
        this.headOfHouseholdName = headOfHouseholdName;
        this.address = address;
        this.feeStatus = feeStatus;
    }
}