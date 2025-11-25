package CTS.user;

import CTS.event.Event;
import CTS.enums.EventStatus;
import CTS.event.Artist;
import CTS.event.LineupEntry;

import java.util.Date;
import java.util.List;

/**
 * Represents a Venue Administrator user.
 * Has permissions to create and manage events, artists, and lineups.
 */
public class VenueAdmin extends User {

   

    /**
     * Constructor for a BRAND NEW admin user (called by userDatabase.createUser)
     */
    public VenueAdmin(int userId, String name, String email, String plainPassword) {
        // Calls the public User constructor, which hashes the password
        super(userId, name, email, plainPassword, "ADMIN"); 
    }
    
    /**
     * REQUIRED: Constructor for LOADING an existing admin (called by User.fromCsvRow)
     */
    protected VenueAdmin(int userId, String name, String email, String passwordHash, LoadFrom tag) {
        // Calls the protected User constructor, which assigns the pre-existing hash
        super(userId, name, email, passwordHash, "ADMIN", tag);
    }

    
    // =========================================================================
    //  ADMIN METHODS (from Main.java adminMenu)
    // =========================================================================

    /**
     * Admin method to view all events, regardless of status.
     * @param allEvents The main list of all events.
     * @return The complete list of events.
     */
    public List<Event> viewAllEvents(List<Event> allEvents) {
        System.out.println(getName() + " is viewing all events...");
        return allEvents;
    }

    /**
     * Creates a new event and adds it to the main list.
     * @param allEvents The main list of events (to add to).
     * @param newEventId A unique ID provided by the Main class.
     * @param name Name of the event.
     * @param startDate The start date/time.
     * @param venue Name of the venue.
     * @param description Event description.
     * @param capacity Total capacity.
     * @return The newly created Event object.
     */
    public Event createEvent(List<Event> allEvents, int newEventId, String name, Date startDate, String venue, String description, int capacity) {
        System.out.println(getName() + " is creating a new event: " + name);
        
        Event newEvent = new Event(
            newEventId,
            name,
            startDate,
            venue,
            description,
            capacity,
            EventStatus.DRAFT // New events always start as DRAFT
        );
        
        allEvents.add(newEvent);
        return newEvent;
    }

    /**
     * Updates an event's status to PUBLISHED or CANCELED.
     * @param event The event object to update.
     * @param newStatus The desired new status (PUBLISHED or CANCELED).
     */
    public void updateEventStatus(Event event, EventStatus newStatus) {
        if (newStatus == EventStatus.PUBLISHED) {
            event.publish();
            System.out.println("Event '" + event.getName() + "' has been published by " + getName());
        } else if (newStatus == EventStatus.CANCELED) {
            event.cancel();
            System.out.println("Event '" + event.getName() + "' has been canceled by " + getName());
        }
    }

    /**
     * Creates a new artist and adds them to the main artist list.
     * @param allArtists The main list of artists (to add to).
     * @param newArtistId A unique ID provided by the Main class.
     * @param stageName The artist's stage name.
     * @param genre The artist's genre.
     * @return The newly created Artist object.
     */
    public Artist createArtist(List<Artist> allArtists, int newArtistId, String stageName, String genre) {
        System.out.println(getName() + " is adding a new artist: " + stageName);
        
        Artist newArtist = new Artist(newArtistId, stageName, genre);
        allArtists.add(newArtist);
        return newArtist;
    }

    /**
     * Adds an artist to an event's lineup.
     * @param event The event to modify.
     * @param artist The artist to add.
     * @param allLineupEntries The main list of all lineup entries (to add to).
     * @param position The artist's position in the lineup (e.g., 1 for headliner).
     * @param notes Any notes about this performance.
     * @return The newly created LineupEntry object.
     */
    public LineupEntry addLineupEntry(Event event, Artist artist, List<LineupEntry> allLineupEntries, int position, String notes) {
        System.out.println(getName() + " is adding " + artist.getStageName() + " to " + event.getName());
        
        LineupEntry newEntry = new LineupEntry(
            event.getEventId(),
            position,
            notes,
            artist
        );
        
        // Add to the main list (for saving to lineup.csv)
        allLineupEntries.add(newEntry);
        
        // Also add to the event's internal list (for easy lookup)
        event.addLineupEntry(newEntry);
        
        return newEntry;
    }
}