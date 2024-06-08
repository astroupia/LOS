import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Customer {
    private String name;
    private int id; // Added ID field
    private String serviceType;
    private Date serviceDate;
    private Date deliveryDate;
    private Service service;

    public Customer(String name, String serviceType, Date serviceDate, Date deliveryDate, Service service) {
        this.name = name;
        this.serviceType = serviceType;
        this.serviceDate = serviceDate;
        this.deliveryDate = deliveryDate;
        this.service = service;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public void setServiceDate(Date serviceDate) {
        this.serviceDate = serviceDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public void registerService() {
        service.calculatePrice();
        int customerId = getId(); // Retrieve customer ID from database

        // Add customer to database (if needed)
        CustomerDAO customerDAO = new CustomerDAO();
        customerDAO.addCustomer(this, customerId);

        // Add service to database based on type
        ServiceDAO serviceDAO = new ServiceDAO();
        if (service instanceof DryCleaningService) {
            serviceDAO.addDryCleaningService(customerId, (DryCleaningService) service);
        } else if (service instanceof WetCleaningService) {
            serviceDAO.addWetCleaningService(customerId, (WetCleaningService) service);
        }
    }

    public String displayReceipt() {
        StringBuilder receipt = new StringBuilder();
        receipt.append("Customer Name: ").append(name).append("\n");
        receipt.append("Customer ID: ").append(id).append("\n");
        receipt.append("Service Type: ").append(serviceType).append("\n");
        receipt.append("Service Date: ").append(serviceDate).append("\n");
        receipt.append("Delivery Date: ").append(deliveryDate).append("\n");
        receipt.append("Items/Weight: ");

        // Display service details based on type
        if (service instanceof DryCleaningService) {
            for (String item : ((DryCleaningService) service).listItems()) {
                receipt.append(item).append(", ");
            }
        } else if (service instanceof WetCleaningService) {
            receipt.append(((WetCleaningService) service).getWeight()).append(" kg");
        }

        receipt.append("\nTotal Price: ").append(service.getPrice()).append(" ETB\n");
        return receipt.toString();
    }

    // Getters for customer details
    public String getName() {
        return name;
    }

    public String getServiceType() {
        return serviceType;
    }

    public Date getServiceDate() {
        return serviceDate;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public Service getService() {
        return service;
    }

    // Method to retrieve customer ID (to be implemented)
    public int getId() {
            Connection conn = null;
            PreparedStatement stmt = null;
            ResultSet rs = null;
            int customerId = 0; // Default value if no customer ID found

            try {
                conn = DatabaseUtil.getConnection(); // Assuming this method returns a Connection
                String sql = "SELECT customerId FROM customers WHERE name = ? AND serviceDate = ?";
                stmt = conn.prepareStatement(sql);
                stmt.setString(1, name); // Assuming 'name' is a field in Customer class
                stmt.setDate(2, new java.sql.Date(serviceDate.getTime())); // Assuming 'serviceDate' is a field in Customer class
                rs = stmt.executeQuery();

                if (rs.next()) {
                    customerId = rs.getInt("customerId");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                // Handle SQLException as needed
            } finally {
                DatabaseUtil.closeResultSet(rs);
                DatabaseUtil.closeStatement(stmt);
                DatabaseUtil.closeConnection(conn);
            }

            return customerId;
        }
    }

