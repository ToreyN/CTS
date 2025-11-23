package CTS.user;

//import java.util.List;
//import java.util.ArrayList;

public abstract class User {

	private int userId;
    private String name;
    private String email;
    private String passwordHash; // stores the HASH, not the real password
    
    public User(int userId, String name, String email, String plainPassword) {  // method called by main to set user info
        this.userId = userId;
        this.name = name;
        this.email = email;
        // Hash the password on creation
        this.passwordHash = hashPassword(plainPassword); // hashPassword is a new method for encrypting the string
    } // not sure if this was what you meant by "passwordHash" Torey, lmk :)
    
    
   private String hashPassword(String password) {  // encryption
        if (password == null) return "";
        // Just returns a number (as a String) based on the password's content
        return String.valueOf(password.hashCode());
    }
    
   
   public boolean authenticate(String password) { // compare encryptions, does the input match the stored?
       // Hash the password they typed in
       String attemptedHash = hashPassword(password);
       // see if it matches the one we have stored.
       return this.passwordHash.equals(attemptedHash);
   }
   
   
   public void changePassword(String oldPwd, String newPwd) {
       if (authenticate(oldPwd)) {
           // Verification passed! Store the HASH of the new password.
           this.passwordHash = hashPassword(newPwd);
           System.out.println("Password changed successfully for " + this.name);
       } else {
           // Verification failed.
           System.out.println("Error: Old password was incorrect. Password not changed.");
       }
   }
   
   
   public int getUserId() { return this.userId; }
   public String getName() { return this.name; }
   public String getEmail() { return this.email; }
	
	
   public String toCSVString() {
	    // Determine the role based on the object's class
	    String role = "USER"; // Default to USER
	    if (this instanceof VenueAdmin) {
	        role = "ADMIN";
	    }
	    
	    return this.getUserId() + "," + this.getName() + "," + this.getEmail() + "," + this.passwordHash + "," + role;
	}
	
}
