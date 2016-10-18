package hms.test.main;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import hms.main.HotelManager;
import hms.model.Occupant;
import hms.model.Occupation;
import hms.model.Room;

@RunWith(Parameterized.class)
public class HotelManagerTest {
	private HotelManager mHotelManager;
	private Room mStandardRoom;
	private Room mExecutiveRoom;
	private Room mPresidentialRoom;

	@Parameter
	public Bundle mTestPair;

	@Parameters
	public static Collection<Bundle> data() {
		Date now = Calendar.getInstance().getTime();

		return Arrays
				.asList(new Bundle[] {

						// Null input
						new Bundle(new Occupation(null, false, null, new Occupant(null, null, null, null)),
								Room.STANDARD, "No input can be null"),
						new Bundle(
								new Occupation(now, false, "00:00:00:00:00:00",
										new Occupant(null, "Standard", "Cheng", "HKUST")),
								Room.STANDARD, "No input can be null"),
						new Bundle(
								new Occupation(now, false, "00:00:00:00:00:00",
										new Occupant("S50023930", null, "Cheng", "HKUST")),
								Room.STANDARD, "No input can be null"),
						new Bundle(
								new Occupation(now, false, "00:00:00:00:00:00",
										new Occupant("S50023930", "Standard", null, "HKUST")),
								Room.STANDARD, "No input can be null"),
						new Bundle(
								new Occupation(now, false, "00:00:00:00:00:00",
										new Occupant("S50023930", "Standard", "Cheng", null)),
								Room.STANDARD, "No input can be null"),
						new Bundle(
								new Occupation(null, false, "00:00:00:00:00:00",
										new Occupant("S50023930", "Standard", "Cheng", "HKUST")),
								Room.STANDARD, "No input can be null"),
						new Bundle(
								new Occupation(now, false, null,
										new Occupant("S50023930", "Standard", "Cheng", "HKUST")),
								Room.STANDARD, "No input can be null"),
						new Bundle(
								new Occupation(now, false, "00:00:00:00:00:00",
										new Occupant("S50023930", "Standard", "Cheng", "HKUST")),
								Short.MIN_VALUE, "No input can be null"),

						// Empty ID, name or company
						new Bundle(
								new Occupation(Calendar.getInstance().getTime(), false, "00:00:00:00:00:00",
										new Occupant("", "Standard", "Cheng", "HKUST")),
								Room.STANDARD, "ID, name, and company cannot be empty"),
						new Bundle(
								new Occupation(Calendar.getInstance().getTime(), false, "00:00:00:00:00:00",
										new Occupant("S50023930", "Standard", "", "HKUST")),
								Room.STANDARD, "ID, name, and company cannot be empty"),
						new Bundle(
								new Occupation(Calendar.getInstance().getTime(), false, "00:00:00:00:00:00",
										new Occupant("S50023930", "Standard", "Cheng", "")),
								Room.STANDARD, "ID, name, and company cannot be empty"),

						// Invalid type
						new Bundle(
								new Occupation(Calendar.getInstance().getTime(), false, "00:00:00:00:00:00",
										new Occupant("S50023930", "Dodgy", "Cheng", "HKUST")),
								Room.STANDARD, "Invalid type"),

						// No data service for standard rooms
						new Bundle(
								new Occupation(Calendar.getInstance().getTime(), true, "00:00:00:00:00:00",
										new Occupant("S50023930", "Business", "Cheng", "HKUST")),
								Room.STANDARD, "No data service for standard rooms"),

						// No data service for standard occupants
						new Bundle(
								new Occupation(Calendar.getInstance().getTime(), true, "00:00:00:00:00:00",
										new Occupant("S50023930", "Standard", "Cheng", "HKUST")),
								Room.EXECUTIVE, "No data service for standard occupants"),

						// Invalid ID
						new Bundle(
								new Occupation(Calendar.getInstance().getTime(), false, "00:00:00:00:00:00",
										new Occupant("()", "Standard", "Cheng", "HKUST")),
								Room.PRESIDENTIAL, "The format of the inputted ID is invalid"),

						// Invalid Ethernet address
						new Bundle(
								new Occupation(Calendar.getInstance().getTime(), true, "Invalid",
										new Occupant("S50023930", "Business", "Cheng", "HKUST")),
								Room.EXECUTIVE, "The format of the inputted ethernetAddress is invalid"),

						// Valid check in where data service is not required
						new Bundle(
								new Occupation(Calendar.getInstance().getTime(), false, "00:00:00:00:00:00",
										new Occupant("S50023930", "Business", "Cheng", "HKUST")),
								Room.STANDARD, "Success"),
						// Valid check in where data service is required
						new Bundle(
								new Occupation(Calendar.getInstance().getTime(), true, "00:00:00:00:00:00",
										new Occupant("S50023930", "Business", "Cheng", "HKUST")),
								Room.EXECUTIVE, "Success") });
	}

	@Before
	public void setUp() {
		mHotelManager = new HotelManager();
		mStandardRoom = new Room(2, 1, 100, Room.STANDARD, 800d);
		mExecutiveRoom = new Room(2, 1, 100, Room.EXECUTIVE, 800d);
		mPresidentialRoom = new Room(2, 1, 100, Room.EXECUTIVE, 800d);
	}

	@After
	public void tearDown() {
		mHotelManager = null;
		mStandardRoom = null;
		mExecutiveRoom = null;
		mPresidentialRoom = null;
	}

	@Test
	public void testHotelManagerCheckIn() {
		Occupation occupation = mTestPair.getOccupation();
		Occupant occupant = occupation.getOccupant();
		Room checkedInRoom = getRoom(mTestPair.getRoomType());

		String checkInResult = mHotelManager.checkIn(occupant.getID(), occupant.getName(), occupant.getType(),
				occupant.getCompany(), occupation.getCheckInDate(), occupation.dataServiceRequired,
				occupation.ethernetAddress, checkedInRoom);
		assertEquals(mTestPair.getExpectedResult(), checkInResult);

		if (checkInResult.equals("Success")) {
			// Check out date = null, room = null
			assertEquals("No input can be null", mHotelManager.checkOut(null, null));

			// Valid check out date, room = null
			assertEquals("No input can be null",
					mHotelManager.checkOut(convertToDate(LocalDate.now().plusDays(1)), null));

			// Invalid checkout date, valid room
			Date yesterday = convertToDate(LocalDate.now().minusDays(1));
			assertEquals("Check-out date must be after check-in date",
					mHotelManager.checkOut(yesterday, checkedInRoom));

			// Valid checkout date, occupation = null
			checkedInRoom.setOccupation(null);
			assertEquals("The room has no occupant",
					mHotelManager.checkOut(convertToDate(LocalDate.now().plusDays(1)), checkedInRoom));
		}
	}

	private Room getRoom(short roomType) {
		switch (mTestPair.getRoomType()) {
		case Room.STANDARD:
			return mStandardRoom;
		case Room.EXECUTIVE:
			return mExecutiveRoom;
		case Room.PRESIDENTIAL:
			return mPresidentialRoom;
		default:
			return null;
		}
	}

	private Date convertToDate(LocalDate localDate) {
		return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
	}
}

/**
 * Consists of an occupation, a room object and the expected message returned
 * from checkIn() in HotelManager. This object is created just for this test
 * class.
 */
class Bundle {
	private Occupation mOccupation;
	private short mRoomType;
	private String mExpectedResult;

	Bundle(Occupation occupation, short roomType, String expectedResult) {
		mOccupation = occupation;
		mRoomType = roomType;
		mExpectedResult = expectedResult;
	}

	Occupation getOccupation() {
		return mOccupation;
	}

	short getRoomType() {
		return mRoomType;
	}

	String getExpectedResult() {
		return mExpectedResult;
	}

	void setOccupation(Occupation occupation) {
		mOccupation = occupation;
	}
}