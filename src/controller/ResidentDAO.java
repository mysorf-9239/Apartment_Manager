package controller;

import model.Resident;

import java.sql.*;
import java.util.ArrayList;

public class ResidentDAO {
    /* Lấy dữ liệu cư dân */
    public static ArrayList<Object[]> getResidentsData() {
        String query = "SELECT id, full_name, date_of_birth, gender, id_card, is_temp_resident, household_id FROM residents";
        ArrayList<Object[]> residentsData = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
             ResultSet resultSet = statement.executeQuery(query)) {

            int index = 0;
            while (resultSet.next()) {
                Object[] row = new Object[8];
                row[0] = String.format("%02d", index + 1);
                row[1] = resultSet.getString("full_name");
                row[2] = resultSet.getString("date_of_birth");
                row[3] = resultSet.getString("gender");
                row[4] = resultSet.getString("id_card");
                row[5] = resultSet.getString("is_temp_resident");
                row[6] = resultSet.getString("household_id") != null ? resultSet.getString("household_id") : "";
                row[7] = resultSet.getString("id");
                residentsData.add(row);
                index++;
            }
        } catch (SQLException | DatabaseConnectionException e) {
            e.printStackTrace();
        }
        return residentsData;
    }

    /* Thêm cư dân */
    public static int addResident(String name, String birthDate, String gender, String idCard) {
        String query = "INSERT INTO residents (full_name, date_of_birth, gender, id_card) VALUES (?, ?, ?, ?)";
        int generatedId = -1;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, name);
            preparedStatement.setDate(2, java.sql.Date.valueOf(birthDate));
            preparedStatement.setString(3, gender);
            preparedStatement.setString(4, idCard);

            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                generatedId = generatedKeys.getInt(1);
            }
        } catch (SQLException | DatabaseConnectionException e) {
            e.printStackTrace();
        }
        return generatedId;
    }

    /* Cập nhật cư dân */
    public static boolean updateResident(int residentID, String name, String birthDate, String gender, String idCard) {
        String query = "UPDATE residents SET full_name = ?, date_of_birth = ?, gender = ?, id_card = ? WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, name);
            pstmt.setString(2, birthDate);
            pstmt.setString(3, gender);
            pstmt.setString(4, idCard);
            pstmt.setInt(5, residentID);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException | DatabaseConnectionException e) {
            e.printStackTrace();
        }
        return false;
    }

    /* Xóa cư dân */
    public static boolean deleteResident(int residentID) {
        String query = "DELETE FROM residents WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setInt(1, residentID);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException | DatabaseConnectionException e) {
            e.printStackTrace();
        }
        return false;
    }

    /* Lấy dữ liệu hộ khẩu */
    public static ArrayList<Object[]> getHouseholdsData() {
        String query = "SELECT h.id, h.address, r.id, r.full_name, r.date_of_birth, r.gender, r.id_card " +
                "FROM households h JOIN residents r ON h.head_of_household = r.id";
        ArrayList<Object[]> householdsData = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            int index = 0;
            while (rs.next()) {
                Object[] row = new Object[4];
                row[0] = String.format("%02d", index + 1);
                row[1] = rs.getInt("h.id");
                row[2] = rs.getString("h.address");
                Resident headOfHousehold = new Resident(
                        rs.getInt("r.id"),
                        rs.getString("r.full_name"),
                        rs.getString("r.date_of_birth"),
                        rs.getString("r.gender"),
                        rs.getString("r.id_card"));
                row[3] = headOfHousehold;
                householdsData.add(row);
                index++;
            }
        } catch (SQLException | DatabaseConnectionException e) {
            e.printStackTrace();
        }
        return householdsData;
    }
}
