package CTS.user;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Manages all User objects (ConcertGoer, VenueAdmin) and handles all
 * persistence logic by reading from and writing to the users.csv file.
 * * This class acts as the "engine"/"controller" for the user module,
 * hiding all data logic from the Main/GUI classes.
 */
public class userDatabase {

    // --- Attributes ---
    
    private ArrayList<User> users;
    private String filePath = "users.csv";
    private int nextUserId = 1;

    // --- Constructor ---

    public userDatabase() {
        this.users = new ArrayList<>();
        loadFromFile(); // Load all users from the file on startup
    }

    //  I/O  Methods  ---
    public java.util.List<User> getAllUsers() {
        return this.users;
    }
 
    private void loadFromFile() {
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("UserDatabase: No users.csv found. Starting with a fresh database.");
            return; // No file to load, just start with an empty list
        }

        // try-with-resources block to auto-close the reader
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int maxId = 0; // Keep track of the highest ID found
            
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty() || line.startsWith("//")) {
                    continue; // Skip empty lines or comments
                }
                
                //  static helper from User.java to parse the line
                User user = User.fromCsvRow(line);
                
                if (user != null) {
                    this.users.add(user);
                    // Update maxId to ensure the next ID is unique
                    if (user.getUserId() > maxId) {
                        maxId = user.getUserId();
                    }
                }
            }
            
            // Set the next user ID to be one higher than the max
            this.nextUserId = maxId + 1;
            
        } catch (IOException | NumberFormatException e) {
            // Handle errors related to file reading or bad number formats in the CSV
            System.err.println("UserDatabase Error: Failed to load from file: " + e.getMessage());
        }
    }

    
    public void saveToFile() {
        // try-with-resources block to auto-close the writer same as above!
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (User user : this.users) {
                // Use the helper method from ConcertGoer/VenueAdmin
            	writer.write(user.toCsvRow());
                writer.newLine(); // Add a new line for the next user
            }
        } catch (IOException e) {
            System.err.println("UserDatabase Error: Failed to save to file: " + e.getMessage());
        }
    }

 
    public User login(String email, String password) {
        //  find the user by their email
        User userToFind = getUserByEmail(email);
        
        // Check if user exists AND password is correct
        if (userToFind != null && userToFind.checkPassword(password)) {
            // Success!
            return userToFind; 
        }
        
        // failure
        return null; 
    }


    public User registerUser(String name, String email, String password, String role) {
        // check for duplicate email ---
        if (getUserByEmail(email) != null) {
            System.err.println("UserDatabase Error: User with email " + email + " already exists.");
            return null;
        }

        
        User newUser;
        int newId = this.nextUserId; // Get the next available unique ID
        
        if (role.equalsIgnoreCase("ADMIN")) {
            newUser = new VenueAdmin(newId, name, email, password);
        } else {
            // Default to ConcertGoer
            newUser = new ConcertGoer(newId, name, email, password);
        }
        
        // --- Update state 
        this.nextUserId++; // Increment the ID for the next registration
        this.users.add(newUser); // Add to the in-memory list
        
        // --- Persistence ---
        saveToFile(); // Save the new user to the users.csv file
        
        System.out.println("UserDatabase: New user registered and saved: " + newUser.getName());
        return newUser;
    }

    //  Helper Methods 


    public User getUserByEmail(String email) {
        for (User user : this.users) {
            if (user.getEmail().equalsIgnoreCase(email)) {
                return user;
            }
        }
        return null; // Not found
    }
    
 
    public User getUserById(int userId) {
        for (User user : this.users) {
            if (user.getUserId() == userId) {
                return user;
            }
        }
        return null; // Not found
    }
}