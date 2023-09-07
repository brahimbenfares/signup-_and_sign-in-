package signupproject.Service;


import signupproject.DAO.AccountDAO;
import signupproject.Model.Account;
//import com.google.cloud.storage.Storage;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
public class AccountService {
    private AccountDAO accountDAO;
    //private Storage storage;
    //private static final Logger logger = LoggerFactory.getLogger(AccountService.class);


    public AccountService() {
        this.accountDAO = new AccountDAO();
    }

    //public void setStorage(Storage storage) {
    //    this.storage = storage;
    //}





    public Account createNewUserAccount(Account account) {
        if (!isStrongPassword(account.getPassword())) {
            return null; // Password doesn't meet the strength criteria
        }
    
        if (doesUsernameExist(account.getUsername())) {
            return null; // Username already exists
        }
    
        if (doesEmailAddressExist(account.getEmail())) {
            return null; // Email address already exists
        }
    
         Account createdAccount = accountDAO.createNewUserAccount(account);
        if (createdAccount != null) {
            return createdAccount;
        }
        return null;
    }
    

    public Account getUserAccountLoginByUsername(String username, String password) {
        return accountDAO.getUserAccountByLogin(username, password);
    }

    public Account getUserAccountLoginByEmail(String email, String password) {
        return accountDAO.getUserAccountLoginByEmail(email, password);
    }



    public boolean doesUsernameExist(String username) {
        return accountDAO.doesUsernameExist(username);
    }

    public boolean doesEmailAddressExist(String email) {
        return accountDAO.doesEmailAddressExist(email);
    }

    private boolean isStrongPassword(String password) {
        // Implement your password strength validation logic here
        // Check for at least 8 characters, uppercase, lowercase, and special characters
        String pattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        return password.matches(pattern);
    }



}