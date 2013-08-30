/**
 * 
 */
package ltg.ps.helioroom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import ltg.commons.LTGEvent;
import ltg.commons.LTGEventHandler;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author tebemis
 *
 */
public class HelioRoomSimulatorAgent {

	public static String[] colors = {"red", "blue", "brown", "pink", "gray", "green", "yellow", "orange"};
	public static String[] planets = {"mercury", "venus", "earth", "mars", "jupiter", "saturn", "uranus", "neptune"};
	public static String[] events = {"insert", "update", "remove"};

	private LTGEventHandler eh = null;
	private List<LTGEvent> generated_events = new ArrayList<LTGEvent>();


	public HelioRoomSimulatorAgent(String usernameAndPass, String chatAndDBId, String[] r_events, int sleep, int stablePoint) { 

		// -------------------
		// Init network and DB
		// -------------------
		eh = new LTGEventHandler(usernameAndPass+"@54.243.60.48", usernameAndPass, chatAndDBId+"@conference.54.243.60.48");

		// -----------------
		//Register listeners
		// -----------------

		// Once we are done registering the listeners we can actually start 
		// to listen for events and handle them 
		eh.runAsynchronously();


		// Now we can simulate the main thread  and make ourselves busy
		while(true) {
			try {
				generateRandomEvent(r_events, stablePoint);
				Thread.sleep(sleep);
			} catch (InterruptedException e) {
				eh.close();
			}
		}
	}


	private void generateRandomEvent(String[] r_events, int stablePoint) {
		if (r_events.length==events.length)
			simulateTraffic();
		else if (Arrays.asList(r_events).contains("insert") && Arrays.asList(r_events).contains("remove")) {
			generateInsertsAndRemove(stablePoint);
		} else if (Arrays.asList(r_events).contains("insert") && Arrays.asList(r_events).contains("update")) {
			generateInsertAndUpdate(stablePoint);
		} else if (Arrays.asList(r_events).contains("update") && Arrays.asList(r_events).contains("remove")){
			System.out.println("This mode is not supported, exiting..,");
			System.exit(0);
		} else if (r_events.length==1 && r_events[0].equals("insert")) {
			generateUpdateEvent();
		} else if (r_events.length==1 && r_events[0].equals("update")) {
			System.out.println("This mode is not supported, exiting..,");
			System.exit(0);
		} else if (r_events.length==1 && r_events[0].equals("remove")) {
			System.out.println("This mode is not supported, exiting..,");
			System.exit(0);
		}
	}


	private void generateInsertAndUpdate(int stablePoint) {
		if (generated_events.size() < stablePoint)
			generateCreateEvent();
		else {
			if (Math.random() < 0.5) 
				generateCreateEvent();
			else
				generateUpdateEvent();
		}
	}


	private void generateInsertsAndRemove(int stablePoint) {
		if (generated_events.size() < stablePoint)
			generateCreateEvent();
		else {
			if (Math.random() < 0.5) 
				generateCreateEvent();
			else
				generateRemoveEvent();
		}
	}


	private void simulateTraffic() {
		double r = Math.random();
		if (r<0.5)
			generateUpdateEvent();
		else if (r <0.75) 
			generateCreateEvent();
		else 
			generateRemoveEvent();
	}


	private void generateCreateEvent() {
		String origin = generateOrigin();
		String event;
		String color;
		String anchor;
		if (Math.random()<0.5) {
			event = "new_observation";
			color = colors[(int) (Math.random()*colors.length)];
			anchor = colors[(int) (Math.random()*colors.length)];
		} else {
			event = "new_theory";
			color = colors[(int) (Math.random()*colors.length)];
			anchor = planets[(int) (Math.random()*planets.length)];
		}
		String reason = generateString((int) (Math.random()*100));
		ObjectNode payload =  JsonNodeFactory.instance.objectNode();
		payload.put("anchor", anchor);
		payload.put("color", color);
		payload.put("reason", reason);
		eh.generateEvent(new LTGEvent(event, origin, null, payload));
		generated_events.add(new LTGEvent(event, origin, null, payload));
	}


	private void generateRemoveEvent() {
		Random r = new Random();
		LTGEvent re = generated_events.remove(r.nextInt(generated_events.size()));
		String event;
		if (re.getType().equals("new_observation")) {
			event = "remove_observation";
		} else {
			event = "remove_theory";
		}
		eh.generateEvent(new LTGEvent(event, re.getOrigin(), null, re.getPayload()));	
	}


	private void generateUpdateEvent() {
		Random r = new Random();
		LTGEvent re = generated_events.remove(r.nextInt(generated_events.size()));
		String event;
		if (re.getType().equals("new_observation")) {
			event = "update_observation";
		} else {
			event = "update_theory";
		}
		eh.generateEvent(new LTGEvent(event, re.getOrigin(), null, re.getPayload()));
	}


	private String generateString(int length) {
		String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		Random rng = new Random(System.nanoTime());
		char[] text = new char[length];
		for (int i = 0; i < length; i++){
			text[i] = characters.charAt(rng.nextInt(characters.length()));
		}
		return new String(text);
	}


	private String generateOrigin() {
		String[] usernames = {"user-1", "user-2", "user-3", "user-4", "user-5", "user-6", "user-7", "user-8", 
				"user-9", "user-10", "user-11", "user-12", "user-13", "user-14", "user-15", "user-16", 
				"user-17", "user-18", "user-19", "user-20", "user-21", "user-22", "user-23", "user-24"};
		return usernames[(int) (Math.random()*usernames.length)];
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String[] events = {"insert", "remove"};
		//The last parameter is the stable point. Meaning how many notes (observations and theories)
		// must be generated before the first remove event is generatd
		new HelioRoomSimulatorAgent("hr-simulator", "helio-sp-13", events, 1000, 5*2);
	}

}
