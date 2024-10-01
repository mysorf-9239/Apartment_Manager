package model;

public class HouseholdMember {
    public Resident resident;
    public String relationshipType;

    public HouseholdMember(Resident resident, String relationshipType) {
        this.resident = resident;
        this.relationshipType = relationshipType;
    }
}
