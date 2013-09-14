package ltg.hg;

public class Tag implements Comparable<Tag> {
	
	private String id = null;
	private String currentLocation = null;
	private String desiredDestination = null;
	private int stale = 0;
	
	
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
	
	
	public String getDesiredDestination() {
		return desiredDestination;
	}


	public void setDesiredDestination(String desiredDestination) {
		this.desiredDestination = desiredDestination;
	}
	
	public void resetStaleCounter() {
		this.stale = 0;
	}
	
	public void incrementStaleCounter() {
		this.stale ++;
	}
	
	public int getStaleCounter() {
		return stale;
	}
	
	
	@Override
	public String toString() {
		return this.id + " @ " + currentLocation + " -> " + desiredDestination;
	}


	@Override
	public int compareTo(Tag o) {
		if (this.stale == o.stale)
			return 0;
		else if (this.stale < o.stale)
			return -1;
		else
			return 1;
	}

}
