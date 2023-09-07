package  signupproject;

import java.io.File;
import java.io.IOException;

import signupproject.Endpoint.Controller;


public class Main {
    public static void main(String[] args) throws IOException {
        try {
            System.out.println("Current directory: " + new File(".").getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Controller controller =new Controller();
        controller.startAPI();

    }
}
