package CTS.event;

public class Artist {
    private int artistId;
    private String stageName;
    private String genre;

    public Artist(int artistId, String stageName, String genre) {
        this.artistId = artistId;
        this.stageName = stageName;
        this.genre = genre;
    }

    public int getArtistId() {
        return artistId;
    }

    public String getStageName() {
        return stageName;
    }

    public void setStageName(String stageName) {
        this.stageName = stageName;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    @Override
    public String toString() {
        return "Artist{" +
                "artistId=" + artistId +
                ", stageName='" + stageName + '\'' +
                ", genre='" + genre + '\'' +
                '}';
    }

}
