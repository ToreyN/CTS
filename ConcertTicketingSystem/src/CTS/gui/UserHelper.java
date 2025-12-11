// In CTS.user package, update UserHelper.java

package CTS.gui; 

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import CTS.user.User;

import java.util.ArrayList;

public class UserHelper {
    
    /**
     * Retrieves the master list of all User objects by calling the correct 
     * persistence method defined in the User class.
     */
    public static List<User> getAllUsers() {
        try {
            // FIX: Call the actual method name defined in your User.java
            return User.loadFromCsv(Paths.get("users.csv")); 
        } catch (Exception e) {
            System.err.println("Error loading all users: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}