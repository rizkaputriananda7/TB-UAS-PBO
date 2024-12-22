import java.util.*;
import java.text.SimpleDateFormat;
import java.sql.*; // Import JDBC for database operations

// Interface for Delivery management
interface Delivery {
    void calculateShippingCost(); // Method to calculate shipping cost
    void displayPackageDetails(); // Method to display package details
}

// Superclass representing a package
class Package {
    String senderName;
    String receiverName;
    String destination;
    double weight;
    java.util.Date shippingDate;

    // Constructor for the Package class
    public Package(String senderName, String receiverName, String destination, double weight, java.util.Date shippingDate) {
        this.senderName = senderName;
        this.receiverName = receiverName;
        this.destination = destination;
        this.weight = weight;
        this.shippingDate = shippingDate;
    }

    // Method to display basic package details
    public void displayBasicDetails() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        System.out.println("Sender: " + senderName.toUpperCase()); // String manipulation
        System.out.println("Receiver: " + receiverName.toUpperCase()); // String manipulation
        System.out.println("Destination: " + destination);
        System.out.println("Weight: " + weight + " kg");
        System.out.println("Shipping Date: " + dateFormat.format(shippingDate));
    }
}

// Subclass representing a standard delivery package
class StandardPackage extends Package implements Delivery {
    double baseCostPerKg = 5.0; // Base cost per kg

    // Constructor for the StandardPackage class
    public StandardPackage(String senderName, String receiverName, String destination, double weight, java.util.Date shippingDate) {
        super(senderName, receiverName, destination, weight, shippingDate);
    }

    // Implementation of the calculateShippingCost method
    @Override
    public void calculateShippingCost() {
        double totalCost = weight * baseCostPerKg; // Simple calculation for cost
        System.out.println("Shipping Cost: $" + totalCost);
    }

    // Implementation of the displayPackageDetails method
    @Override
    public void displayPackageDetails() {
        displayBasicDetails();
        calculateShippingCost();
    }
}

// Subclass representing an express delivery package
class ExpressPackage extends Package implements Delivery {
    double baseCostPerKg = 10.0; // Higher base cost per kg for express
    double expressFee = 15.0;    // Additional express fee

    // Constructor for the ExpressPackage class
    public ExpressPackage(String senderName, String receiverName, String destination, double weight, java.util.Date shippingDate) {
        super(senderName, receiverName, destination, weight, shippingDate);
    }

    // Implementation of the calculateShippingCost method
    @Override
    public void calculateShippingCost() {
        double totalCost = (weight * baseCostPerKg) + expressFee; // Calculation includes express fee
        System.out.println("Shipping Cost: $" + totalCost);
    }

    // Implementation of the displayPackageDetails method
    @Override
    public void displayPackageDetails() {
        displayBasicDetails();
        calculateShippingCost();
    }
}

// Utility class for database operations
class DatabaseUtility {
    private Connection connection;

    // Constructor to establish database connection
    public DatabaseUtility(String url, String user, String password) throws SQLException {
        connection = DriverManager.getConnection(url, user, password);
    }

    // Method to create a new package record in the database
    public void createPackage(String senderName, String receiverName, String destination, double weight, java.util.Date shippingDate) throws SQLException {
        String sql = "INSERT INTO packages (sender_name, receiver_name, destination, weight, shipping_date) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, senderName);
            statement.setString(2, receiverName);
            statement.setString(3, destination);
            statement.setDouble(4, weight);
            statement.setDate(5, new java.sql.Date(shippingDate.getTime()));
            statement.executeUpdate();
            System.out.println("Package added successfully.");
        }
    }

    // Method to read all package records from the database
    public void readPackages() throws SQLException {
        String sql = "SELECT * FROM packages";
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                System.out.println("ID: " + resultSet.getInt("id"));
                System.out.println("Sender: " + resultSet.getString("sender_name"));
                System.out.println("Receiver: " + resultSet.getString("receiver_name"));
                System.out.println("Destination: " + resultSet.getString("destination"));
                System.out.println("Weight: " + resultSet.getDouble("weight") + " kg");
                System.out.println("Shipping Date: " + resultSet.getDate("shipping_date"));
                System.out.println("--------------------------");
            }
        }
    }

    // Method to update a package record in the database
    public void updatePackage(int id, String newDestination, double newWeight) throws SQLException {
        String sql = "UPDATE packages SET destination = ?, weight = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, newDestination);
            statement.setDouble(2, newWeight);
            statement.setInt(3, id);
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Package updated successfully.");
            } else {
                System.out.println("No package found with the given ID.");
            }
        }
    }

    // Method to delete a package record from the database
    public void deletePackage(int id) throws SQLException {
        String sql = "DELETE FROM packages WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Package deleted successfully.");
            } else {
                System.out.println("No package found with the given ID.");
            }
        }
    }

    // Method to close the database connection
    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}

// Main class for managing deliveries
public class PackageManagement {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Database connection details
        String url = "jdbc:postgresql://localhost:5432/Package";
        String user = "postgres";
        String password = "password";

        try {
            // Initialize database utility
            DatabaseUtility dbUtility = new DatabaseUtility(url, user, password);

            System.out.println("=== Package Management System ===");

            while (true) {
                System.out.println("\n1. Add Package\n2. View Packages\n3. Update Package\n4. Delete Package\n5. Exit");
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        System.out.print("Sender Name: ");
                        String senderName = scanner.nextLine();

                        System.out.print("Receiver Name: ");
                        String receiverName = scanner.nextLine();

                        System.out.print("Destination: ");
                        String destination = scanner.nextLine();

                        System.out.print("Weight (kg): ");
                        double weight = scanner.nextDouble();

                        System.out.print("Shipping Date (dd-MM-yyyy): ");
                        scanner.nextLine(); // Consume newline
                        String dateInput = scanner.nextLine();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                        java.util.Date shippingDate = dateFormat.parse(dateInput);

                        dbUtility.createPackage(senderName, receiverName, destination, weight, shippingDate);
                        break;

                    case 2:
                        dbUtility.readPackages();
                        break;

                    case 3:
                        System.out.print("Enter Package ID to Update: ");
                        int updateId = scanner.nextInt();
                        scanner.nextLine(); // Consume newline

                        System.out.print("New Destination: ");
                        String newDestination = scanner.nextLine();

                        System.out.print("New Weight (kg): ");
                        double newWeight = scanner.nextDouble();

                        dbUtility.updatePackage(updateId, newDestination, newWeight);
                        break;

                    case 4:
                        System.out.print("Enter Package ID to Delete: ");
                        int deleteId = scanner.nextInt();
                        dbUtility.deletePackage(deleteId);
                        break;

                    case 5:
                        dbUtility.closeConnection();
                        System.out.println("Exiting system. Goodbye!");
                        scanner.close();
                        return;

                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage()); // Handle exceptions
        }
    }
}