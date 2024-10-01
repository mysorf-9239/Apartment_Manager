package model;

public class Resident {

    public int id;
    public String full_name;
    public String date_of_birth;
    public String gender;
    public String idCard;
    public boolean is_temp_resident;
    public int household_id;
    public String relationshipType;

    public Resident(int id, String full_name, String date_of_birth, String gender, String idCard) {
        this.id = id;
        this.full_name = full_name;
        this.date_of_birth = date_of_birth;
        this.gender = gender;
        this.idCard = idCard;
        this.is_temp_resident = false;
    }


}
