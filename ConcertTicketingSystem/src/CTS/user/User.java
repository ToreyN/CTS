package CTS.user;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

/**
 * Abstract base class for all user types.
 * Contains user data, authentication, and CSV persistence.
 */
public abstract class User {

    // =========================================================================
    //  USER FIELDS
    // =========================================================================
    private int userId;
    private String name;
    private String email;
    private String passwordHash;
    private String role;
    
    // ### 1. ADD THE PRIVATE ENUM TAG ###
    /** A private enum used *only* to create a unique constructor signature. */
    protected enum LoadFrom { CSV_ROW } // <-- CHANGED

    // =========================================================================
    //  CONSTRUCTORS
    // =========================================================================

    /** Used when creating a brand-new user with a plain password */
    public User(int userId, String name, String email, String plainPassword, String role) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.role = role;
        this.passwordHash = hashPassword(plainPassword); // Hashes the plain password
    }

    // ### 2. UPDATE THE PROTECTED CONSTRUCTOR ###
    /** * Used ONLY when loading an existing user from CSV.
     * The 'LoadFrom' tag makes its signature unique.
     */
    protected User(int userId, String name, String email, String passwordHash, String role, LoadFrom tag) { // <-- CHANGED
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.role = role;
        this.passwordHash = passwordHash; // Assigns the pre-existing hash
    }


    // =========================================================================
    //  PASSWORD AUTHENTICATION
    // =========================================================================

    public boolean checkPassword(String plainPassword) {
        return hashPassword(plainPassword).equals(this.passwordHash);
    }

    /** Computes SHA-256 hash (simple version for coursework) */
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }


    // =========================================================================
    //  GETTERS
    // =========================================================================

    public int getUserId()     { return userId; }
    public String getName()    { return name; }
    public String getEmail()   { return email; }
    public String getRole()    { return role; }
    protected String getPasswordHash() { return passwordHash; }


    // =========================================================================
    //  CSV ROW SERIALIZATION (convert user <-> CSV line)
    // =========================================================================

    /** Converts user to a single CSV row */
    public String toCsvRow() {
        return userId + "," +
               escape(name) + "," +
               escape(email) + "," +
               passwordHash + "," +
               role;
    }

    // ### 3. UPDATE THE fromCsvRow METHOD ###
    /**
     * Creates a User subclass (ConcertGoer or VenueAdmin) from a CSV row.
     */
    public static User fromCsvRow(String line) {
        // Split into exactly 5 parts, ignoring escaped commas
        String[] parts = line.split("(?<!\\\\),", 5);
        if (parts.length < 5) {
            throw new IllegalArgumentException("Malformed CSV line: " + line);
        }

        int id = Integer.parseInt(parts[0]);
        String name = unescape(parts[1]);
        String email = unescape(parts[2]);
        String passwordHash = parts[3];
        String role = parts[4];

        // We pass this tag to the subclass constructors
        LoadFrom tag = LoadFrom.CSV_ROW; // <-- CHANGED

        switch (role) {
            // These constructors now accept the 'tag'
            case "USER":  return new ConcertGoer(id, name, email, passwordHash, tag); // <-- CHANGED
            case "ADMIN": return new VenueAdmin(id, name, email, passwordHash, tag); // <-- CHANGED
            default:
                throw new IllegalArgumentException("Unknown role: " + role);
        }
    }


    // =========================================================================
    //  CSV FILE PERSISTENCE (load / save whole user database)
    // =========================================================================

    /** Loads all users from a CSV file */
    public static List<User> loadFromCsv(Path path) throws IOException {
        List<User> result = new ArrayList<>();

        if (!Files.exists(path)) {
            System.out.println("INFO: User file not found. Empty database.");
            return result;
        }

        List<String> lines = Files.readAllLines(path);

        for (String line : lines.stream()
                                .filter(l -> !l.trim().isEmpty() && !l.startsWith("#"))
                                .collect(Collectors.toList())) {

            try {
                result.add(fromCsvRow(line));
            } catch (Exception e) {
                System.err.println("Error reading user: " + line + " — " + e.getMessage());
            }
        }

        System.out.println("INFO: Loaded " + result.size() + " users.");
        return result;
    }


    /** Writes all users to a CSV file */
    public static void saveToCsv(Path path, List<User> users) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("# userId,name,email,passwordHash,role");

        for (User u : users) {
            lines.add(u.toCsvRow());
        }

        Files.write(path, lines);
        System.out.println("INFO: Saved " + users.size() + " users.");
    }


    // =========================================================================
    //  CSV UTILITY METHODS
    // =========================================================================

    /** Escapes commas and backslashes so CSV parsing is safe */
    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace(",", "\\,");
    }

    /** Reverses the escaping rules */
    private static String unescape(String s) {
        return s.replace("\\\\", "\\").replace("\\,", ",");
    }
}