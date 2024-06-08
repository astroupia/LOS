import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Admin {
    private String name;
    private String businessName;
    private List<Customer> customers;

    public Admin(String name, String businessName) {
        this.name = name;
        this.businessName = businessName;
        this.customers = new ArrayList<>();
    }

    public void addCustomer(Customer customer) {
        customers.add(customer);
    }

    public String generateReport(Date startDate, Date endDate) {
        int numberOfCustomers = 0;
        double totalRevenue = 0;

        StringBuilder report = new StringBuilder();
        report.append("Business Name: ").append(businessName).append("\n");
        report.append("Report Duration: ").append(startDate).append(" to ").append(endDate).append("\n");

        for (Customer customer : customers) {
            // Assume serviceDate is within the range
            numberOfCustomers++;
            totalRevenue += customer.getService().getPrice();
        }

        report.append("Number of Customers: ").append(numberOfCustomers).append("\n");
        report.append("Total Revenue: ").append(totalRevenue).append(" birr\n");

        return report.toString();
    }
    private boolean isServiceWithinRange(Customer customer, Date startDate, Date endDate) {
        Date serviceDate = customer.getServiceDate();
        return serviceDate.after(startDate) && serviceDate.before(endDate);
    }

    public List<Customer> getServicesInRange(Date startDate, Date endDate) {
        List<Customer> filteredServices = new ArrayList<>();
        for (Customer customer : customers) {
            if (isServiceWithinRange(customer, startDate, endDate)) {
                filteredServices.add(customer);
            }
        }
        return filteredServices;
    }
}