package ltg.hg;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class SimulatorTest {
	
	@Test
	public void testSelectLuckyWinner() {
		List<Tag> tags = new ArrayList<Tag>();
		tags.add(new Tag("a"));
		tags.add(new Tag("b"));
		tags.add(new Tag("c"));
		tags.get(1).incrementStaleCounter();
		tags.get(1).incrementStaleCounter();
		tags.get(2).incrementStaleCounter();
		// Now a_stale = 0, b_stale = 2, c_stale = 1
		Collections.sort(tags, Collections.reverseOrder());
		// Sorting should produce b, c, a
		assertTrue(tags.get(0).getId().equals("b"));
		assertTrue(tags.get(1).getId().equals("c"));
		assertTrue(tags.get(2).getId().equals("a"));
	}

}
