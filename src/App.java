import javafx.application.Application;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class App extends Application {
    private Admin admin;
    private List<Customer> customers;
    private Customer loggedInCustomer; // Track logged-in customer
    private Stage primaryStage;
    private Scene mainScene;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        admin = new Admin("Admin", "Best Laundromat");
        customers = new ArrayList<>();

        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        mainScene = new Scene(root, 400, 400);

        Button customerModeButton = new Button("Customer Mode");
        customerModeButton.setOnAction(e -> switchToCustomerMode());

        Button adminModeButton = new Button("Administration Mode");
        adminModeButton.setOnAction(e -> switchToAdminMode());

        root.getChildren().addAll(customerModeButton, adminModeButton);

        primaryStage.setScene(mainScene);
        primaryStage.setTitle("Laundromat System");
        primaryStage.show();
    }

    private void switchToCustomerMode() {
        GridPane customerLayout = new GridPane();
        customerLayout.setPadding(new Insets(20));
        customerLayout.setHgap(10);
        customerLayout.setVgap(10);
        customerLayout.setAlignment(Pos.CENTER);

        Scene customerScene = new Scene(customerLayout, 600, 400);

        Label nameLabel = new Label("Enter your name:");
        TextField nameField = new TextField();

        Label idLabel = new Label("Enter your ID:");
        TextField idField = new TextField(); // Placeholder for ID field

        Label serviceTypeLabel = new Label("Select service type:");
        ComboBox<String> serviceTypeBox = new ComboBox<>();
        serviceTypeBox.getItems().addAll("Dry Cleaning", "Wet Cleaning");

        Label serviceDateLabel = new Label("Select service date:");
        DatePicker serviceDatePicker = new DatePicker();

        Label deliveryDateLabel = new Label("Select delivery date:");
        DatePicker deliveryDatePicker = new DatePicker();

        Button loginButton = new Button("Login");
        TextArea receiptArea = new TextArea();
        receiptArea.setEditable(false);
        Button registerButton = new Button("Register Service");

        registerButton.setOnAction(e -> {
            String serviceType = serviceTypeBox.getValue();
            LocalDate serviceDateValue = serviceDatePicker.getValue();
            LocalDate deliveryDateValue = deliveryDatePicker.getValue();

            if (serviceType == null || serviceDateValue == null || deliveryDateValue == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("All fields must be filled.");
                alert.show();
                return;
            }

            Date serviceDate = Date.valueOf(serviceDateValue);
            Date deliveryDate = Date.valueOf(deliveryDateValue);
            Service service;

            if ("Dry Cleaning".equals(serviceType)) {
                service = new DryCleaningService();
                // Example items for dry cleaning service
                ((DryCleaningService) service).addItem("Fur item", 100);
                ((DryCleaningService) service).addItem("Leather item", 150);
            } else {
                service = new WetCleaningService(200); // Example price per kg
                ((WetCleaningService) service).setWeight(2); // Example weight
            }

            Customer customer = loggedInCustomer; // Use logged-in customer
            customer.setServiceType(serviceType);
            customer.setServiceDate(serviceDate);
            customer.setDeliveryDate(deliveryDate);
            customer.setService(service);

            customer.registerService();
            customers.add(customer);

            receiptArea.setText(customer.displayReceipt());
        });

        Button viewServicesButton = new Button("View Current Services");
        TextArea servicesArea = new TextArea();
        servicesArea.setEditable(false);

        viewServicesButton.setOnAction(e -> {
            if (loggedInCustomer == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Please log in to view your services.");
                alert.show();
                return;
            }

            StringBuilder servicesText = new StringBuilder();
            for (Customer customer : customers) {
                if (customer.getName().equals(loggedInCustomer.getName())) {
                    servicesText.append(customer.displayReceipt()).append("\n");
                }
            }

            if (servicesText.length() == 0) {
                servicesText.append("No services found for the logged-in customer.");
            }

            servicesArea.setText(servicesText.toString());

            // Display services in a new window
            Stage servicesStage = new Stage();
            servicesStage.setTitle("Your Services");
            Scene servicesScene = new Scene(new VBox(new Label("Your Services:"), servicesArea), 400, 300);
            servicesStage.setScene(servicesScene);
            servicesStage.show();
        });

        loginButton.setOnAction(e -> {
            String name = nameField.getText();
            int id = Integer.parseInt(idField.getText());// Get ID input

            // Simulate login by finding customer in list (replace with actual authentication)
            Customer customer = findCustomer(name, id);
            if (customer == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Customer not found. Please check your name and ID.");
                alert.show();
                return;
            }

            // loggedInCustomer = customer;

            customerLayout.getChildren().clear(); // Clear previous login fields

            customerLayout.add(serviceTypeLabel, 0, 0);
            customerLayout.add(serviceTypeBox, 1, 0);
            customerLayout.add(serviceDateLabel, 0, 1);
            customerLayout.add(serviceDatePicker, 1, 1);
            customerLayout.add(deliveryDateLabel, 0, 2);
            customerLayout.add(deliveryDatePicker, 1, 2);
            customerLayout.add(new Label("Welcome, " + customer.getName()), 0, 3, 2, 1);
            customerLayout.add(new Label("ID: " + customer.getId()), 0, 4, 2, 1);
            customerLayout.add(new Label("Receipt:"), 0, 5);
            customerLayout.add(receiptArea, 0, 6, 2, 1);
            customerLayout.add(registerButton, 0, 7, 2, 1);
            customerLayout.add(viewServicesButton, 0, 8, 2, 1);
            customerLayout.add(createBackButton("Back", mainScene), 0, 9, 2, 1);
        });

        customerLayout.add(nameLabel, 0, 0);
        customerLayout.add(nameField, 1, 0);
        customerLayout.add(idLabel, 0, 1);
        customerLayout.add(idField, 1, 1);
        customerLayout.add(loginButton, 0, 2, 2, 1);

        primaryStage.setScene(customerScene);
    }

    private Button createBackButton(String text, Scene sceneToSwitchTo) {
        Button backButton = new Button(text);
        backButton.setOnAction(e -> primaryStage.setScene(sceneToSwitchTo));
        return backButton;
    }

    private Customer findCustomer(String name, int id) {
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    Customer customer = null;

        try {
            conn = DatabaseUtil.getConnection(); // Establish database connection (implement this method)
            String sql = "SELECT * FROM customers WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                // Retrieve customer details from ResultSet
                String customerName = rs.getString("name");
                int db_id = rs.getInt("id");
                String serviceType = rs.getString("serviceType");
                Date serviceDate = rs.getDate("serviceDate");
                Date deliveryDate = rs.getDate("deliveryDate");

                // Verify if retrieved customer matches the provided name and ID
                if (customerName.equals(name) && db_id == id) {
                    customer = new Customer(customerName, serviceType, serviceDate, deliveryDate, null); // Service object is not fetched in this method
                    return customer; // Return customer if verification is successful
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle SQLException as needed
        } finally {
            // Close resources
            DatabaseUtil.closeResultSet(rs);
            DatabaseUtil.closeStatement(stmt);
            DatabaseUtil.closeConnection(conn);
        }

        return null; // Return null if customer is not found or verification fails
    }
    
    private List<Customer> fetchAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection(); // Implement this method to get database connection
            String sql = "SELECT * FROM customers";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                // Retrieve customer details from ResultSet
                String customerName = rs.getString("name");
                String serviceType = rs.getString("serviceType");
                Date serviceDate = rs.getDate("serviceDate");
                Date deliveryDate = rs.getDate("deliveryDate");
                double servicePrice = rs.getDouble("servicePrice");

                Service service;
                if ("Dry Cleaning".equals(serviceType)) {
                    DryCleaningService dryCleaningService = new DryCleaningService();
                    dryCleaningService.addItem("Example Item", servicePrice); // Example item and price
                    service = dryCleaningService;
                } else {
                    WetCleaningService wetCleaningService = new WetCleaningService(servicePrice); // Example price per kg
                    wetCleaningService.setWeight(2); // Example weight
                    service = wetCleaningService;
                }

                Customer customer = new Customer(customerName, serviceType, serviceDate, deliveryDate, service);
                customers.add(customer);
            }
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            // Close resources
            DatabaseUtil.closeResultSet(rs);
            DatabaseUtil.closeConnection(conn);
        }
        return customers;
    }

    private void switchToAdminMode() {
        GridPane adminLayout = new GridPane();
        adminLayout.setPadding(new Insets(20));
        adminLayout.setHgap(10);
        adminLayout.setVgap(10);
        adminLayout.setAlignment(Pos.CENTER);

        Scene adminScene = new Scene(adminLayout, 800, 600);

        Label filterLabel = new Label("Filter Services by Date Range:");
        DatePicker startDatePicker = new DatePicker();
        DatePicker endDatePicker = new DatePicker();

        Button filterButton = new Button("Filter");
        TableView<Customer> servicesTable = new TableView<>();
        servicesTable.setEditable(false);

        TableColumn<Customer, String> nameColumn = new TableColumn<>("Customer Name");
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));

        TableColumn<Customer, String> serviceTypeColumn = new TableColumn<>("Service Type");
        serviceTypeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getServiceType()));

        TableColumn<Customer, Date> serviceDateColumn = new TableColumn<>("Service Date");
        serviceDateColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getServiceDate()));

        TableColumn<Customer, Date> deliveryDateColumn = new TableColumn<>("Delivery Date");
        deliveryDateColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDeliveryDate()));

        TableColumn<Customer, Double> priceColumn = new TableColumn<>("Total Price");
        priceColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getService().getPrice()).asObject());

        servicesTable.getColumns().addAll(nameColumn, serviceTypeColumn, serviceDateColumn, deliveryDateColumn, priceColumn);

        List<Customer> allCustomers = fetchAllCustomers();
        servicesTable.getItems().addAll(allCustomers);

        filterButton.setOnAction(e -> {
            LocalDate startDateValue = startDatePicker.getValue();
            LocalDate endDateValue = endDatePicker.getValue();

            if (startDateValue == null || endDateValue == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Both start date and end date must be selected.");
                alert.show();
                return;
            }

            Date startDate = Date.valueOf(startDateValue);
            Date endDate = Date.valueOf(endDateValue);
            List<Customer> filteredServices = admin.getServicesInRange(startDate, endDate);
            servicesTable.getItems().setAll(filteredServices);
        });

        adminLayout.add(filterLabel, 0, 0, 2, 1);
        adminLayout.add(startDatePicker, 0, 1);
        adminLayout.add(endDatePicker, 1, 1);
        adminLayout.add(filterButton, 0, 2, 2, 1);
        adminLayout.add(servicesTable, 0, 3, 2, 1);
        adminLayout.add(createBackButton("Back", mainScene), 0, 4, 2, 1);

        primaryStage.setScene(adminScene);
    }    
    public static void main(String[] args) {
        launch(args);
    }
}
