/**
 * 
 */
package ltg.hg;

import java.util.Random;

import ltg.commons.ltg_handler.LTGEvent;
import ltg.commons.ltg_handler.LTGEventHandler;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author tebemis
 *
 */
public class HungerGamesSimulator {

	private Tag[] rfid_ids = {new Tag("1623110"), new Tag("3"), new Tag("2")};
	private String[] patches = {"hg-patch-1", "hg-patch-2", "hg-patch-3", "hg-patch-4"};

	private LTGEventHandler eh = null;


	public HungerGamesSimulator(String usernameAndPass, String chatAndDBId) { 

		// --------------
		// Initialize bot
		// --------------
		eh = new LTGEventHandler(usernameAndPass+"@ltg.evl.uic.edu", usernameAndPass, chatAndDBId+"@conference.ltg.evl.uic.edu");
		//HttpClient

		// ------------------
		// Register listeners
		// ------------------

		// No listeners needed for this bot

		// -----------------------
		// StartRegister listeners
		// -----------------------
		eh.runAsynchronously();
	}


	/**
	 * Starts generating all the events 
	 */
	public void generateEvents() {
		// Assign a random starting location to all tags
		assignInitialLocation();
		// When flush = rfid_ids.lentgh then we flushed the initial location
		int flush = 0;
		// Event generation loop
		while(!Thread.interrupted()) {
			try {
				if (flush<rfid_ids.length)
					flushInitialLocation(flush++);
				else
					somebodyComesAndGoes();
				Thread.sleep(250);
			} catch (InterruptedException e) {
				eh.close();
			}
		}
	}


	private void assignInitialLocation() {
		for (int i=0; i<rfid_ids.length; i++) 
			rfid_ids[i].setCurrentLocation(patches[new Random().nextInt(patches.length)]);
	}
	
	
	private void flushInitialLocation(int i) {
		ObjectNode payload =  JsonNodeFactory.instance.objectNode();
		ArrayNode arrivals = JsonNodeFactory.instance.arrayNode();
		ArrayNode departures = JsonNodeFactory.instance.arrayNode();
		arrivals.add(rfid_ids[i].getId());
		payload.put("arrivals", arrivals);
		payload.put("departures", departures);
		eh.generateEvent(new LTGEvent("rfid_update", null, rfid_ids[i].getCurrentLocation(), payload));
	}


	private void somebodyComesAndGoes() {
		int ti = selectLuckyWinner();
		// Departure from current location
		ObjectNode payload1 =  JsonNodeFactory.instance.objectNode();
		ArrayNode arrivals1 = JsonNodeFactory.instance.arrayNode();
		ArrayNode departures1 = JsonNodeFactory.instance.arrayNode();
		departures1.add(rfid_ids[ti].getId());
		payload1.put("arrivals", arrivals1);
		payload1.put("departures", departures1);
		eh.generateEvent(new LTGEvent("rfid_update", null, rfid_ids[ti].getCurrentLocation(), payload1));
		// Set new location and reset stale counter
		setNewLocationAndResetStaleCounter(ti);
		// Arrival at new location
		ObjectNode payload2 =  JsonNodeFactory.instance.objectNode();
		ArrayNode arrivals2 = JsonNodeFactory.instance.arrayNode();
		ArrayNode departures2 = JsonNodeFactory.instance.arrayNode();
		arrivals2.add(rfid_ids[ti].getId());
		payload1.put("arrivals", arrivals2);
		payload1.put("departures", departures2);
		eh.generateEvent(new LTGEvent("rfid_update", null, rfid_ids[ti].getCurrentLocation(), payload2));
	}



	private void setNewLocationAndResetStaleCounter(int tagIndex) {
		rfid_ids[tagIndex].setCurrentLocation(selectNewLocation(tagIndex));
		// Increment stale counter for all 
		for(int i=0; i<rfid_ids.length; i++)
			rfid_ids[i].stale++;
		rfid_ids[tagIndex].stale=0;
	}


	private String selectNewLocation(int tagIndex) {
		String newLocation = null;
		do {
			newLocation = patches[new Random().nextInt(patches.length)];
		} while (newLocation==rfid_ids[tagIndex].getCurrentLocation());
		return newLocation;
	}


	private int selectLuckyWinner() {
		return new Random().nextInt(rfid_ids.length);
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		HungerGamesSimulator hg = new HungerGamesSimulator("hg-simulator-bot", "hg-test");
		hg.generateEvents();
	}

}
