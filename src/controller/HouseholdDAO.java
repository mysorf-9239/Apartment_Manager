package controller;

import model.Household;
import model.HouseholdMember;
import model.Resident;

import java.sql.*;
import java.util.ArrayList;

public class HouseholdDAO {
    /* Đếm */
    public static int countHouseholds() {
        String query = "SELECT COUNT(*) FROM households";
        int count = 0;

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            if (resultSet.next()) {
                count = resultSet.getInt(1); // Lấy giá trị COUNT(*) từ kết quả truy vấn
            }
        } catch (SQLException | DatabaseConnectionException e) {
            e.printStackTrace();
        }

        return count;
    }

    /* Lấy dữ liệu hộ khẩu với id */
    public static Household getHouseholdById(int householdId) {
        String query = "SELECT h.id AS household_id, h.address, h.acreage, r.id AS resident_id, r.full_name, r.date_of_birth, " +
                "r.gender, r.id_card, r.is_temp_resident " +
                "FROM households h " +
                "JOIN residents r ON h.head_of_household = r.id " +
                "WHERE h.id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            // Set the household ID parameter
            preparedStatement.setInt(1, householdId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    // Extract head of household data
                    Resident headOfHousehold = new Resident(
                            resultSet.getInt("resident_id"),
                            resultSet.getString("full_name"),
                            resultSet.getString("date_of_birth"),
                            resultSet.getString("gender"),
                            resultSet.getString("id_card"),
                            resultSet.getBoolean("is_temp_resident"),
                            resultSet.getInt("household_id")
                    );

                    // Create and return the Household object
                    return new Household(
                            resultSet.getInt("household_id"),
                            resultSet.getString("address"),
                            resultSet.getDouble("acreage"),
                            headOfHousehold
                    );
                }
            }
        } catch (SQLException | DatabaseConnectionException e) {
            e.printStackTrace();
        }

        // Return null if no household is found or an error occurs
        return null;
    }

    // Retrieve Head of Household Information
    public static Resident getHeadOfHouseholdInfo(String fullName, String idCard) {
        String sql = "SELECT id, full_name, date_of_birth, gender, id_card, is_temp_resident, household_id FROM residents WHERE full_name = ? AND id_card = ?";
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
                            rs.getString("id_card"),
                            rs.getBoolean("is_temp_resident"),
                            rs.getInt("household_id")
                    );
                }
            }
        } catch (SQLException | DatabaseConnectionException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Add Household
    public static int addHousehold(String address, Double acreage, int headOfHouseholdId) {
        String insertHouseholdQuery = "INSERT INTO households (address, acreage, head_of_household) VALUES (?, ?, ?)";
        String updateResidentQuery = "UPDATE residents SET household_id = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement insertStmt = conn.prepareStatement(insertHouseholdQuery, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement updateStmt = conn.prepareStatement(updateResidentQuery)) {

                insertStmt.setString(1, address);
                insertStmt.setDouble(2, acreage);
                insertStmt.setInt(3, headOfHouseholdId);

                int affectedRows = insertStmt.executeUpdate();

                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int householdId = generatedKeys.getInt(1);

                            updateStmt.setInt(1, householdId);
                            updateStmt.setInt(2, headOfHouseholdId);
                            int rowsUpdated = updateStmt.executeUpdate();

                            if (rowsUpdated > 0) {
                                conn.commit();
                                return householdId;
                            }
                        }
                    }
                }
                conn.rollback();
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException | DatabaseConnectionException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // Update Household
    public static boolean updateHousehold(int householdID, String newAddress, Double newAcreage, int newHeadOfHouseholdID) {
        String sql = "UPDATE households SET address = ?, head_of_household = ?, acreage = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newAddress);
            pstmt.setInt(2, newHeadOfHouseholdID);
            pstmt.setDouble(3, newAcreage);
            pstmt.setInt(4, householdID);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException | DatabaseConnectionException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Delete Household
    public static boolean deleteHousehold(int householdID) {
        String updateSql = "UPDATE residents SET household_id = NULL WHERE household_id = ?";
        String updateRelationshipsSql = "UPDATE relationships SET household_id = NULL WHERE household_id = ?";
        String deleteSql = "DELETE FROM households WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false); // Start a transaction

            // First, remove references in the relationships table
            try (PreparedStatement updateRelationshipsPstmt = conn.prepareStatement(updateRelationshipsSql)) {
                updateRelationshipsPstmt.setInt(1, householdID);
                updateRelationshipsPstmt.executeUpdate();
            }

            // Update residents table
            try (PreparedStatement updatePstmt = conn.prepareStatement(updateSql)) {
                updatePstmt.setInt(1, householdID);
                updatePstmt.executeUpdate();
            }

            // Delete from households table
            try (PreparedStatement deletePstmt = conn.prepareStatement(deleteSql)) {
                deletePstmt.setInt(1, householdID);
                int rowsDeleted = deletePstmt.executeUpdate();

                conn.commit(); // Commit the transaction
                return rowsDeleted > 0;
            } catch (SQLException e) {
                conn.rollback(); // Rollback if delete fails
                e.printStackTrace();
            }
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
                            rs.getString("id_card"),
                            rs.getBoolean("is_temp_resident"),
                            rs.getInt("household_id")
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
                            rs.getString("id_card"),
                            rs.getBoolean("is_temp_resident"),
                            rs.getInt("household_id")
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
        String insertQuery = "INSERT INTO relationships (resident_id, head_of_household_id, relationship_type, household_id) VALUES (?, ?, ?, ?)";
        String updateQuery = "UPDATE residents SET household_id = ? WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection()) {
            // Bắt đầu transaction
            connection.setAutoCommit(false);

            try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery);
                 PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {

                // Thực hiện INSERT
                insertStmt.setInt(1, residentID);
                insertStmt.setInt(2, headOfHouseholdID);
                insertStmt.setString(3, relationshipType);
                insertStmt.setInt(4, householdID);
                int rowsInserted = insertStmt.executeUpdate();

                // Thực hiện UPDATE
                updateStmt.setInt(1, householdID);
                updateStmt.setInt(2, residentID);
                int rowsUpdated = updateStmt.executeUpdate();

                // Kiểm tra cả hai thao tác
                if (rowsInserted > 0 && rowsUpdated > 0) {
                    connection.commit();
                    return true;
                } else {
                    connection.rollback();
                }
            } catch (SQLException e) {
                connection.rollback();
                e.printStackTrace();
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException | DatabaseConnectionException e) {
            e.printStackTrace();
        }
        return false;
    }

}
