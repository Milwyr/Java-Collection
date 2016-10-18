package hms.test.main;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import hms.main.Hotel;
import hms.main.HotelManager;
import hms.model.Room;

public class HotelTest {

	private HotelManager mHotelManager;

	@Before
	public void setUp() {
		mHotelManager = new HotelManager();
	}

	@After
	public void tearDown() {
		mHotelManager = null;
	}

	@Test
	public void testHotel() {
		Hotel hotel = new Hotel("Marriott", 15, 10);
		assertEquals("Marriott", hotel.getName());

		// Initialises a new HotelManager object
		Hotel.main(null);

		// Tests getHotelName() method
		assertEquals("201Hotel", mHotelManager.getHotelName());
	}

	// This test covers the findOccupant() method in the HotelManager class, and
	// it is put here because this test does not need parameterised test.
	@Test
	public void testFindOccupant() {
		Room room = (Room) mHotelManager.listAllAvailableRooms().get(0);

		mHotelManager.checkIn("S50023930", "Cheung", "Standard", "HKUST", Calendar.getInstance().getTime(), false,
				"00:00:00:00:00:00", room);

		// Values that match the checked in occupant
		Room resultRoom = (Room) mHotelManager.findOccupant("ID", "S50023930").get(0);
		assertTrue(roomEquals(room, resultRoom));
		resultRoom = (Room) mHotelManager.findOccupant("Type", "Standard").get(0);
		assertTrue(roomEquals(room, resultRoom));
		resultRoom = (Room) mHotelManager.findOccupant("Company", "HKUST").get(0);
		assertTrue(roomEquals(room, resultRoom));
		resultRoom = (Room) mHotelManager.findOccupant("Name", "Cheung").get(0);
		assertTrue(roomEquals(room, resultRoom));

		// Values that do not match the checked in occupant
		ArrayList list = mHotelManager.findOccupant("ID", "123");
		assertEquals(0, list.size());
		list = mHotelManager.findOccupant("Type", "123");
		assertEquals(0, list.size());
		list = mHotelManager.findOccupant("Company", "123");
		assertEquals(0, list.size());
		list = mHotelManager.findOccupant("Name", "123");
		assertEquals(0, list.size());
	}

	@Test
	public void testGetRoom() {
		Room expected = new Room(1, 1, 8, Room.PRESIDENTIAL, 3000);

		// The getRoom(floor, roomNo) method starts from zero, but 5100Hotel.xml
		// starts from 1
		assertTrue(roomEquals(expected, mHotelManager.getRoom(0, 0)));

		// Increase branch coverage of the roomEquals method
		assertFalse(roomEquals(expected, mHotelManager.getRoom(1, 1)));
	}

	private boolean roomEquals(Room r1, Room r2) {
		return r1.getFloorNo() == r2.getFloorNo() && r1.getRoomNo() == r2.getRoomNo()
				&& r1.getCapacity() == r2.getCapacity() && r1.getType() == r2.getType()
				&& (Math.abs(r1.getRate() - r2.getRate()) < 0.001);
	}
}