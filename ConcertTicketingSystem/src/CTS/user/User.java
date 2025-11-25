package CTS.user;

public abstract class User {

    private int userId;
    private String name;
    private String email;
    private String passwordHash; // stores the HASH, not the real password

    public User(int userId, String name, String email, String plainPassword) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.passwordHash = hashPassword(plainPassword);
    }

    private String hashPassword(String password) {
        if (password == null) return "";
        return String.valueOf(password.hashCode());
    }

    public boolean authenticate(String password) {
        String attemptedHash = hashPassword(password);
        return this.passwordHash.equals(attemptedHash);
    }

    public void changePassword(String oldPwd, String newPwd) {
        if (authenticate(oldPwd)) {
            this.passwordHash = hashPassword(newPwd);
            System.out.println("Password changed successfully for " + this.name);
        } else {
            System.out.println("Error: Old password was incorrect. Password not changed.");
        }
    }

    // Package-private helper used by User.fromCSVString
    void setPasswordHashDirect(String hash) {
        this.passwordHash = (hash == null) ? "" : hash;
    }

    public int getUserId() { return this.userId; }
    public String getName() { return this.name; }
    public String getEmail() { return this.email; }

    public String toCSVString() {
        String role = "USER";
        if (this instanceof VenueAdmin) {
            role = "ADMIN";
        }
        return this.getUserId() + "," +
               this.getName() + "," +
               this.getEmail() + "," +
               this.passwordHash + "," +
               role;
    }

    
    public static User fromCSVString(String line) {
        if (line == null || line.isEmpty()) {
            return null;
        }
        String[] parts = line.split(",", 5);
        if (parts.length < 5) {
            return null;
        }
        int id = Integer.parseInt(parts[0]);
        String name = parts[1];
        String email = parts[2];
        String storedHash = parts[3];
        String role = parts[4];

        User user;
        if ("ADMIN".equalsIgnoreCase(role)) {
            user = new VenueAdmin(id, name, email, "TEMP");
        } else {
            user = new ConcertGoer(id, name, email, "TEMP");
        }
        // override the temporary hash with the stored hash from file
        user.setPasswordHashDirect(storedHash);
        return user;
    }
}
