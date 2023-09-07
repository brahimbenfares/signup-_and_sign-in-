package signupproject.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.mindrot.jbcrypt.BCrypt; 

import signupproject.Model.Account;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AccountDAO {

    private static final Logger logger = LoggerFactory.getLogger(AccountDAO.class);
    private static String DB_URL;
    private static String DB_USERNAME;
    private static String DB_PASSWORD;
    
    static {
        // Load DB credentials from config
        Properties properties = new Properties();
        try (InputStream input = AccountDAO.class.getClassLoader().getResourceAsStream("dbconfig.properties")) {
            properties.load(input);
            DB_URL = properties.getProperty("dbUrl");
            DB_USERNAME = properties.getProperty("dbUsername");
            DB_PASSWORD = properties.getProperty("dbPassword");

            // Test the connection
            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
                logger.info("Successfully connected to database in AccountDAO.");
            } catch (SQLException e) {
                e.printStackTrace();
                logger.error("Failed to connect to the database in AccountDAO.");
            }
        } catch (IOException ex) {
            throw new RuntimeException("Failed to read DB credentials from config", ex);
        }
    }

    private Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }


    /************************************************************************************ */
    /*                        creation of account start                                   */
    /************************************************************************************ */
    public Account createNewUserAccount(Account account) {
        try (Connection connection = getConnection()) {
    
            String sql = "INSERT INTO accounts (username, password, email_address, phone_number, profile_picture_url) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
    
            // Hash the password before storing it
            String hashedPassword = BCrypt.hashpw(account.getPassword(), BCrypt.gensalt());
         
    
            preparedStatement.setString(1, account.getUsername());
            preparedStatement.setString(2, hashedPassword);  // Storing hashed password
            preparedStatement.setString(3, account.getEmail());
            preparedStatement.setString(4, account.getPhoneNumber());
            preparedStatement.setString(5, account.getProfilePictureUrl());
    
            int affectedRows = preparedStatement.executeUpdate();
    
            if (affectedRows > 0) {
                ResultSet pkeyResultSet = preparedStatement.getGeneratedKeys();
                if (pkeyResultSet.next()) {
                    int generatedAccountId = pkeyResultSet.getInt(1);
                    return new Account(generatedAccountId, account.getUsername(), hashedPassword,
                            account.getEmail(), account.getPhoneNumber(), account.getProfilePictureUrl());
                }
            }
        } catch (SQLException e) {
            System.out.println("SQLException Message: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("VendorError: " + e.getErrorCode());
            e.printStackTrace();
        }
        
        return null;
    }
    


    
/********************************************************************************** */
/* checking if the username or email is exist in database when creating new account */
/********************************************************************************** */
    public boolean isUsernameTaken(String username) {
        try (Connection connection = getConnection()) {
            String sql = "SELECT COUNT(*) AS count FROM accounts WHERE username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt("count");
                return count > 0;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }


    public boolean isEmailTaken(String email) {
        try (Connection connection = getConnection()) {
            String sql = "SELECT COUNT(*) AS count FROM accounts WHERE email_address = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt("count");
                return count > 0;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }


    public boolean doesUsernameExist(String username) {
        try (Connection connection = getConnection()) {
            String sql = "SELECT COUNT(*) AS count FROM accounts WHERE username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt("count");
                return count > 0;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public boolean doesEmailAddressExist(String email) {
        try (Connection connection = getConnection()) {
            String sql = "SELECT COUNT(*) AS count FROM accounts WHERE email_address = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt("count");
                return count > 0;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }
        /****************************************************************************** */
         /*                    creation of account end                                  */
        /****************************************************************************** */




    /************************************************************************************ */
    /*                                   logins start                                     */
    /************************************************************************************ */
 //login by username 
 public Account getUserAccountByLogin(String username, String password) {
    try (Connection connection = getConnection()) {
        String sql = "SELECT * FROM accounts WHERE username = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, username);
        ResultSet rs = preparedStatement.executeQuery();
        if (rs.next()) {
            String hashedPassword = rs.getString("password");
            if (BCrypt.checkpw(password, hashedPassword)) {
                int accountId = rs.getInt("account_id");
                String retrievedUsername = rs.getString("username");
                String retrievedEmailAddress = rs.getString("email_address");
                String retrievedPhoneNumber = rs.getString("phone_number");
                return new Account(accountId, retrievedUsername, hashedPassword, retrievedEmailAddress, retrievedPhoneNumber);
            }
        }
    } catch (SQLException e) {
        System.err.println("Error accessing the database: " + e.getMessage());
        throw new RuntimeException("An error occurred while accessing the user account: " + e.getMessage());
    }
    return null;
}

   // by email address 
   public Account getUserAccountLoginByEmail(String email, String password) {
    try (Connection connection = getConnection()) {
        String sql = "SELECT * FROM accounts WHERE email_address = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, email);
        ResultSet rs = preparedStatement.executeQuery();
        if (rs.next()) {
            String hashedPassword = rs.getString("password");
            if (BCrypt.checkpw(password, hashedPassword)) {
                int accountId = rs.getInt("account_id");
                String retrievedUsername = rs.getString("username");
                String retrievedEmailAddress = rs.getString("email_address");
                String retrievedPhoneNumber = rs.getString("phone_number");
                return new Account(accountId, retrievedUsername, hashedPassword, retrievedEmailAddress, retrievedPhoneNumber);
            }
        }
    } catch (SQLException e) {
    System.err.println("Error accessing the database: " + e.getMessage());
    throw new RuntimeException("An error occurred while accessing the user account. Please try again later.");
  }
    return null;
}



}