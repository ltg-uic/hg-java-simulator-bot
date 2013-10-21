/**
 * 
 */
package ltg.hg;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import ltg.commons.ltg_event_handler.LTGEvent;
import ltg.commons.ltg_event_handler.SingleChatLTGEventHandler;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author tebemis
 *
 */
public class HungerGamesSimulator {

	private Tag[] rfid_ids_array = {	
			new Tag("arg"), new Tag("baf"), new Tag("boz"), new Tag("dob"), new Tag("fot"), 
			new Tag("gub"), new Tag("hap"), new Tag("jur"), new Tag("kep"), new Tag("mid"),
			new Tag("nar"), new Tag("pab"), new Tag("pha"), new Tag("ren"), new Tag("som"),
			new Tag("sug"), new Tag("tuz"), new Tag("wat"), new Tag("wir"), new Tag("yim")
	};
	private List<Tag> tags = Arrays.asList(rfid_ids_array); 
	private String[] patches = {"patch-a", "patch-b", "patch-c", "patch-d", "patch-e", "patch-f"};

	private SingleChatLTGEventHandler eh = null;


	public HungerGamesSimulator(String usernameAndPass, String chatAndDBId) { 

		// --------------
		// Initialize bot
		// --------------
		eh = new SingleChatLTGEventHandler(usernameAndPass+"@ltg.evl.uic.edu", usernameAndPass, chatAndDBId+"@conference.ltg.evl.uic.edu");
		

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
				updateLocations(t);
				resetAndIncrementStaleCounters(t);
				Thread.sleep(1000 + (long) Math.random()*1000 );
			} catch (InterruptedException e) {
				eh.shutdown();
			}
		}
	}


	private void assignInitialDestination() {
		for (Tag t: tags)
			t.setDestination(patches[new Random().nextInt(patches.length)]);
	}
	
	
	private int selectLuckyWinner() {
		Collections.sort(tags, Collections.reverseOrder());
		int random_window = tags.size()/4;
		return new Random().nextInt(random_window);
	}
	
	
	private void dispatchEvent(int i) {
		ObjectNode payload = JsonNodeFactory.instance.objectNode();
		payload.put("id", tags.get(i).getId());
		payload.put("departure", tags.get(i).getOrigin());
		payload.put("arrival", tags.get(i).getDestination());
		LTGEvent e = new LTGEvent("rfid_update", null, null, payload);
		eh.generateEvent(e);
	}

	
	private void updateLocations(int idx) {
		String old_destination_now_origin = tags.get(idx).getDestination();
		String new_destination = null;
		do {
			new_destination = patches[new Random().nextInt(patches.length)];
		} while (new_destination == old_destination_now_origin);
		tags.get(idx).setDestination(new_destination);
		tags.get(idx).setOrigin(old_destination_now_origin);
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
		if ( args.length != 2 || 
				args[0]==null || args[0].isEmpty() || 
				args[1]==null || args[1].isEmpty() ) {
			System.out.println("Need to specify the username/password (eg. hg-bots#simulator) and "
					+ "the chatroom ID (eg. hg-test). Terminating...");
			System.exit(0);
		}
		HungerGamesSimulator hg = new HungerGamesSimulator(args[0], args[1]);
		hg.generateEvents();
	}

}
