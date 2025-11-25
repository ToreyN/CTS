package CTS.seating;

public class Section {
	private int sectionID;
	private String name;
	private String description;
	
	private void setID(int ID) {
		sectionID = ID;
	}
	private void setName(String name) {
		this.name = name;
	}
	private void setDesc(String desc) {
		description = desc;
	}
	
	private int getID() {
		return sectionID;
	}
	
	private String getName() {
		return name;
	}
	
	private String getDesc() {
		return description;
	}
	
}
