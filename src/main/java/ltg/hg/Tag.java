package ltg.hg;

public class Tag implements Comparable<Tag> {
	
	private String id = null;
	private String origin = null;
	private String destination = null;
	private int stale = 0;
	
	
	public Tag(String id) {
		this.id = id;
	}


	public String getId() {
		return id;
	}
	
	public String getOrigin() {
		return origin;
	}


	public void setOrigin(String origin) {
		this.origin = origin;
	}
	
	
	public String getDestination() {
		return destination;
	}


	public void setDestination(String destination) {
		this.destination = destination;
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
		return this.id + " @ " + origin + " -> " + destination;
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
