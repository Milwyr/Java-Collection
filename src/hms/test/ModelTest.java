package hms.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import hms.model.Occupant;
import hms.model.Occupation;
import hms.model.Room;

@RunWith(Parameterized.class)
public class ModelTest {

	@Parameter
	public Room mRoom;

	@Parameters
	public static Collection<Room> data() {
		// The occupation which does not require data service
		Occupation occupation = new Occupation(Calendar.getInstance().getTime(), false, null,
				new Occupant("S50023930", "Standard", "Cheng", "HKUST"));

		// The occupation which requires data service
		Occupation requiredOccupation = new Occupation(Calendar.getInstance().getTime(), true, null,
				new Occupant("S50023930", "Standard", "Cheng", "HKUST"));

		// Occupation == null; Type: Standard
		Room r1 = new Room(2, 1, 100, Room.STANDARD, 800d);
		r1.setOccupation(null);

		// Occupation == null; Type: Presidential
		Room r2 = new Room(2, 1, 100, Room.PRESIDENTIAL, 800d);
		r2.setOccupation(null);

		// Room type: Standard; Data service required: false
		Room r3 = new Room(2, 1, 100, Room.STANDARD, 800d);
		r3.setOccupation(occupation);

		// Room type: Executive; Data service required: false
		Room r4 = new Room(2, 1, 100, Room.EXECUTIVE, 800d);
		r4.setOccupation(occupation);

		// Room type: Standard; Data service required: true
		Room r5 = new Room(2, 1, 100, Room.PRESIDENTIAL, 800d);
		r5.setOccupation(requiredOccupation);

		// Room type: Presidential; Data service required: true
		Room r6 = new Room(2, 1, 100, Room.PRESIDENTIAL, 800d);
		r5.setOccupation(requiredOccupation);

		// Room type that is neither standard nor executive nor presidential
		Room r7 = new Room(2, 1, 100, Short.MAX_VALUE, 800d);

		return Arrays.asList(new Room[] { r1, r2, r3, r4, r5, r6, r7 });
	}

	@Test
	public void testGetTypeString() {
		switch (mRoom.getType()) {
		case Room.STANDARD:
			assertEquals("Standard", mRoom.getTypeString());
			break;
		case Room.EXECUTIVE:
			assertEquals("Executive", mRoom.getTypeString());
			break;
		case Room.PRESIDENTIAL:
			assertEquals("Presidential", mRoom.getTypeString());
			break;
		}
	}
	
	@Test
	public void testToString() {
		String expectedStringFragment = "Room No: " + mRoom.getFloorNo() + "-" + mRoom.getRoomNo() +
				", Room Type: " + mRoom.getTypeString() + ", Capacity: " + mRoom.getCapacity() +
				", Rate: " + mRoom.getRate();
		
		assertTrue(mRoom.toString().contains(expectedStringFragment));
	}

}