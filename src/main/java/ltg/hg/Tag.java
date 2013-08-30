package ltg.hg;

public class Tag {
	
	private String id = null;
	private String currentLocation = null;
	public int stale = 0;
	
	
	public Tag(String id) {
		this.id = id;
	}


	public String getId() {
		return id;
	}
	
	public String getCurrentLocation() {
		return currentLocation;
	}


	public void setCurrentLocation(String currentLocation) {
		this.currentLocation = currentLocation;
	}
	
	
	@Override
	public String toString() {
		return this.id + " @ " + currentLocation;
	}

}
