/**
 * 
 */
package ltg.hg;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import ltg.commons.ltg_handler.LTGEvent;
import ltg.commons.ltg_handler.LTGEventHandler;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author tebemis
 *
 */
public class HungerGamesSimulator {

	private Tag[] rfid_ids_array = {	
			new Tag("1623305"), new Tag("1623303"), new Tag("1623386"), new Tag("1623210"), new Tag("1623373"), 
			new Tag("1623115"), new Tag("1623667"), new Tag("1623110"), new Tag("1623683"), new Tag("1623257"),
			new Tag("1623126"), new Tag("1623238"), new Tag("1623624"), new Tag("1623454"), new Tag("1623972"),
			new Tag("1623302"), new Tag("1623392"), new Tag("1623663"), new Tag("1623728"), new Tag("1623641")
	};
	private List<Tag> tags = Arrays.asList(rfid_ids_array); 
	private String[] patches = {"patch-1", "patch-2", "patch-3", "patch-4", "patch-5", "patch-6"};

	private LTGEventHandler eh = null;


	public HungerGamesSimulator(String usernameAndPass, String chatAndDBId) { 

		// --------------
		// Initialize bot
		// --------------
		eh = new LTGEventHandler(usernameAndPass+"@ltg.evl.uic.edu", usernameAndPass, chatAndDBId+"@conference.ltg.evl.uic.edu");
		

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
	 * Starts generating arrivals and departures 
	 */
	public void generateEvents() {
		// Assign a random destination to all tags
		assignInitialDestination();
		// Event generation loop
		while(!Thread.interrupted()) {
			try {
				int t = selectLuckyWinner();
				dispatchEvent(t);
				setNewLocations(t);
				resetAndIncrementStaleCounters(t);
				Thread.sleep(250);
			} catch (InterruptedException e) {
				eh.close();
			}
		}
	}


	private void assignInitialDestination() {
		for (Tag t: tags)
			t.setDesiredDestination(patches[new Random().nextInt(patches.length)]);
	}
	
	
	private int selectLuckyWinner() {
		Collections.sort(tags);
		return new Random().nextInt(tags.size()/4);
	}
	
	
	private void dispatchEvent(int i) {
		ObjectNode payload = JsonNodeFactory.instance.objectNode();
		payload.put("id", tags.get(i).getId());
		payload.put("departure", tags.get(i).getCurrentLocation());
		payload.put("arrival", tags.get(i).getDesiredDestination());
		LTGEvent e = new LTGEvent("rfid_update", null, null, payload);
		//eh.generateEvent(e);
	}

	private void setNewLocations(int idx) {
		String old_desired_location_now_current = tags.get(idx).getDesiredDestination();
		String new_desired_location = null;
		do {
			new_desired_location = patches[new Random().nextInt(patches.length)];
		} while (new_desired_location==tags.get(idx).getCurrentLocation());
		tags.get(idx).setDesiredDestination(new_desired_location);
		tags.get(idx).setCurrentLocation(old_desired_location_now_current);
	}

	
	private void resetAndIncrementStaleCounters(int idx) {
		// Increment stale counter for all... 
		for (Tag t: tags)
			t.incrementStaleCounter();
		// ... but idx, which gets a reset
		tags.get(idx).resetStaleCounter();
	}

	


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		HungerGamesSimulator hg = new HungerGamesSimulator("hg-simulator-bot", "hg-test");
		hg.generateEvents();
	}

}
