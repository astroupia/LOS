import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    public void addCustomer(Customer customer, int customerId) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseUtil.getConnection();
            String sql = "INSERT INTO customers (name, serviceType, serviceDate, deliveryDate, amountPaid) VALUES (?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getServiceType());
            stmt.setDate(3, new java.sql.Date(customer.getServiceDate().getTime()));
            stmt.setDate(4, new java.sql.Date(customer.getDeliveryDate().getTime()));
            stmt.setDouble(5, customer.getService().getPrice());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeStatement(stmt);
            DatabaseUtil.closeConnection(conn);
        }
    }


    public List<Customer> getCustomers(Date startDate, Date endDate) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Customer> customers = new ArrayList<>();

        try {
            conn = DatabaseUtil.getConnection();
            String sql = "SELECT * FROM customers WHERE serviceDate BETWEEN ? AND ?";
            stmt = conn.prepareStatement(sql);
            stmt.setDate(1, startDate);
            stmt.setDate(2, endDate);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Customer customer = new Customer(
                    rs.getString("name"),
                    rs.getString("serviceType"),
                    rs.getDate("serviceDate"),
                    rs.getDate("deliveryDate"),
                    null // We'll retrieve service details separately
                );
                customers.add(customer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.closeResultSet(rs);
            DatabaseUtil.closeStatement(stmt);
            DatabaseUtil.closeConnection(conn);
        }

        return customers;
    }
}