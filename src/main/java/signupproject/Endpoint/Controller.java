package signupproject.Endpoint;

import java.util.Map;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;
import signupproject.Model.Account;
import signupproject.Model.JwtUtil;
import signupproject.Service.AccountService;

import java.io.IOException;
import java.io.File;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;


public class Controller {
   
    private AccountService accountService;

    //private static final Logger logger = LoggerFactory.getLogger(Controller.class);


    
    public Controller() {
        this.accountService = new AccountService();
    }

    public io.javalin.Javalin startAPI() throws IOException {


        try {
            System.out.println("Current Path: " + new File(".").getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        

        // In your Javalin initialization
        Javalin app = Javalin.create(config -> {
            //config.addStaticFiles("C:/Users/br3mt/OneDrive/Desktop/brahim/start/start/src/main/webapp", null);
            config.enableCorsForAllOrigins();
        }).start(8085);
        








                //user
        app.post("/register", this::createAccountHandler);
        app.post("/login", this::userAccountLogin);
    
        app.get("/protected-endpoint", this::someProtectedEndpoint); // Add this line for the protected endpoint
    




        return app;
    }

    private void createAccountHandler(Context ctx) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Account account = mapper.readValue(ctx.body(), Account.class);

            // Check if username already exists
            if (accountService.doesUsernameExist(account.getUsername())) {
                ctx.status(400).result("Username already exists. Please choose another one.");
                return;
            }

            // Check if email address already exists
            if (accountService.doesEmailAddressExist(account.getEmail())) {
                ctx.status(400).result("Email address already exists. Please use a different one or reset your password.");
                return;
            }

            Account createdAccount = accountService.createNewUserAccount(account);
            if (createdAccount != null && createdAccount.getUsername() != null && createdAccount.getPassword() != null
                    && !createdAccount.getUsername().isEmpty() && createdAccount.getPassword().length() >= 4) {
                ctx.json(createdAccount); // Send the created account as JSON response
            } else {
                ctx.status(400).result("Failed to create account. Please try again.");
            }
        } catch (JsonProcessingException | NullPointerException e) {
            ctx.status(400).result("Invalid request. Please provide valid account details.");
            e.printStackTrace(); // Print the stack trace for debugging
        }
    }



    private void someProtectedEndpoint(Context ctx) {
        String authHeader = ctx.header("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
              //  String token = authHeader.substring(7);
               // String username = JwtUtil.validateToken(token);
                // Perform additional authorization or logic here
                ctx.result("Access granted"); // Example response for a successful protected endpoint access
            } catch (io.jsonwebtoken.JwtException e) {
                ctx.status(401).result("Invalid token. Please log in again.");
            }
        } else {
            ctx.status(401).result("Missing token. Please log in.");
        }
    }
    

    private void userAccountLogin(Context ctx) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Account account = mapper.readValue(ctx.body(), Account.class);
    
            Account loggedInAccount;
    
            if(account.getLoginIdentifier().contains("@")) {
                loggedInAccount = accountService.getUserAccountLoginByEmail(account.getLoginIdentifier(), account.getPassword());
            } else {
                loggedInAccount = accountService.getUserAccountLoginByUsername(account.getLoginIdentifier(), account.getPassword());
            }
    
            if (loggedInAccount != null) {
                String token = JwtUtil.generateToken(loggedInAccount.getUsername());
                ctx.json(Map.of("token", token)); // Return the token as JSON
            } else {
                ctx.status(401).result("Invalid username or password. Please try again.");
            }
        } catch (Exception e) {
            System.err.println("Error in userAccountLogin: " + e.getMessage());
            e.printStackTrace();
            ctx.status(500).result(e.getMessage());
        }
    }    




    
}