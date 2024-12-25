package controller;

import model.HouseholdMember;
import model.Resident;

import java.sql.*;
import java.util.ArrayList;

public class HouseholdDAO {
    // Retrieve Head of Household Information
    public static Resident getHeadOfHouseholdInfo(String fullName, String idCard) {
        String sql = "SELECT id, full_name, date_of_birth, gender, id_card FROM residents WHERE full_name = ? AND id_card = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, fullName);
            pstmt.setString(2, idCard);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Resident(
                            rs.getInt("id"),
                            rs.getString("full_name"),
                            rs.getString("date_of_birth"),
                            rs.getString("gender"),
                            rs.getString("id_card")
                    );
                }
            }
        } catch (SQLException | DatabaseConnectionException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Add Household
    public static int addHousehold(String address, int headOfHouseholdId) {
        String sql = "INSERT INTO households (address, head_of_household) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, address);
            pstmt.setInt(2, headOfHouseholdId);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException | DatabaseConnectionException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // Update Household
    public static boolean updateHousehold(int householdID, String newAddress, int newHeadOfHouseholdID) {
        String sql = "UPDATE households SET address = ?, head_of_household = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newAddress);
            pstmt.setInt(2, newHeadOfHouseholdID);
            pstmt.setInt(3, householdID);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException | DatabaseConnectionException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Delete Household
    public static boolean deleteHousehold(int householdID) {
        String sql = "DELETE FROM households WHERE head_of_household = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, householdID);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException | DatabaseConnectionException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Get Resident by ID
    public static Resident getResidentById(int residentId) {
        String query = "SELECT * FROM residents WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, residentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Resident(
                            rs.getInt("id"),
                            rs.getString("full_name"),
                            rs.getString("date_of_birth"),
                            rs.getString("gender"),
                            rs.getString("id_card")
                    );
                }
            }
        } catch (SQLException | DatabaseConnectionException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Get Household Members with Relationships
    public static ArrayList<HouseholdMember> getHouseholdMembersWithRelationships(int householdID) {
        ArrayList<HouseholdMember> members = new ArrayList<>();
        String query = "SELECT r.*, h.relationship_type " +
                "FROM relationships h " +
                "JOIN residents r ON h.resident_id = r.id " +
                "WHERE h.household_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, householdID);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Resident resident = new Resident(
                            rs.getInt("id"),
                            rs.getString("full_name"),
                            rs.getString("date_of_birth"),
                            rs.getString("gender"),
                            rs.getString("id_card")
                    );
                    String relationshipType = rs.getString("relationship_type");
                    members.add(new HouseholdMember(resident, relationshipType));
                }
            }
        } catch (SQLException | DatabaseConnectionException e) {
            e.printStackTrace();
        }

        return members;
    }

    // Add Relationship
    public static boolean addRelationship(int residentID, int headOfHouseholdID, String relationshipType, int householdID) {
        String query = "INSERT INTO relationships (resident_id, head_of_household_id, relationship_type, household_id) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, residentID);
            stmt.setInt(2, headOfHouseholdID);
            stmt.setString(3, relationshipType);
            stmt.setInt(4, householdID);

            return stmt.executeUpdate() > 0;

        } catch (SQLException | DatabaseConnectionException e) {
            e.printStackTrace();
        }
        return false;
    }
}
