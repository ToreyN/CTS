package CTS.seating;

public class Section {

    // --- Attributes matching the UML Diagram 
    private final int sectionId;     // Unique ID for the section
    private String name;             // "Main Floor"
    private String description;      // Detailed description of the section
    private int tierId;              // ID link to the PricingTier
    
    
    
    // --- Constructor 
    
    
     // Creates a new Section object.
     
    public Section(int sectionId, String name, String description, int tierId) {
        this.sectionId = sectionId;
        this.name = name;
        this.description = description;
        this.tierId = tierId; // Links this section to a specific PricingTier
    }

    // --- Getters & Setters 
    
    public int getSectionId() {
        return sectionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTierId() {
        return tierId;
    }
    
    // Allows admin to remap a section to a different tier 
    public void setTierId(int tierId) {
        this.tierId = tierId;
    }
    
    @Override
    public String toString() {
        return String.format("Section [%d]: %s (Tier ID: %d)", sectionId, name, tierId);
    }
}