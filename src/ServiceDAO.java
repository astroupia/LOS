import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ServiceDAO {

    public void addDryCleaningService(int customerId, DryCleaningService service) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseUtil.getConnection();
            String sql = "INSERT INTO services (customerId, type, details, price) VALUES (?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, customerId);
            stmt.setString(2, "Dry Cleaning");
            stmt.setString(3, String.join(",", service.listItems()));
            stmt.setDouble(4, service.getPrice());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeStatement(stmt);
            DatabaseUtil.closeConnection(conn);
        }
    }

    public void addWetCleaningService(int customerId, WetCleaningService service) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseUtil.getConnection();
            String sql = "INSERT INTO services (customerId, type, details, price) VALUES (?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, customerId);
            stmt.setString(2, "Wet Cleaning");
            stmt.setString(3, service.getWeight() + "kg");
            stmt.setDouble(4, service.getPrice());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeStatement(stmt);
            DatabaseUtil.closeConnection(conn);
        }
    }
}
