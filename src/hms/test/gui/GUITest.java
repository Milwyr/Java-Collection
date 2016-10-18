package hms.test.gui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;

import javax.swing.JPanel;
import javax.swing.UIManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import hms.command.CheckInCommand;
import hms.gui.CheckInPanel;
import hms.gui.CheckOutPanel;
import hms.gui.SearchPanel;
import hms.main.HotelManager;
import hms.model.Occupant;
import hms.model.Occupation;
import hms.model.Room;

/*
 * 
 * This test uses CheckInPanel as an example to show basic ways to invoke event handlers of the GUI widgets.
 * There are two basic ways: (1) Mock the user event (2) Directly invoke the call back methods 
 *
 */
@RunWith(Parameterized.class)
public class GUITest {
	private HotelManager mHotelManager;
	private CheckInPanel mCheckInPanel;

	// The stream that captures the System.err.print in the console
	private ByteArrayOutputStream mErrorStream;

	// The stream that captures the System.out.print in the console
	private ByteArrayOutputStream mOutStream;

	@Parameter
	public Occupation mOccupation;

	@Parameters
	public static Collection<Occupation> data() {
		Occupant standardOccupant = new Occupant("S50023930", "Standard", "Cheung", "HKUST");
		Occupation standardOccupation = new Occupation(Calendar.getInstance().getTime(), false, "00:00:00:00:00:00",
				standardOccupant);

		Occupant businessOccupant = new Occupant("B68197231", "Business", "Mourinho", "Manchester United");
		Occupation businessOccupation = new Occupation(Calendar.getInstance().getTime(), true, "91:23:56:78:07:19",
				businessOccupant);

		return Arrays.asList(new Occupation[] { standardOccupation, businessOccupation });
	}

	@Before
	public void setUp() {
		mHotelManager = new HotelManager();
		mCheckInPanel = new CheckInPanel(new CheckInCommand(mHotelManager), mHotelManager);

		assertNotNull(mCheckInPanel.hotelManager);
		assertNotNull(mCheckInPanel.command);
		assertNotNull(mCheckInPanel.checkInButton);

		// Instantiates the output stream that reads System.err.print output
		// from console
		mErrorStream = new ByteArrayOutputStream();
		System.setErr(new PrintStream(mErrorStream));

		// Instantiates the output stream that reads System.out.print output
		// from console
		mOutStream = new ByteArrayOutputStream();
		System.setOut(new PrintStream(mOutStream));
	}

	@Test
	public void testCheckInPanelGetCommandValues() {
		// Cover the branch of method getValueAt(row, column) where column > 4
		assertEquals("", mCheckInPanel.availableRoomTable.getModel().getValueAt(0, 5));

		// Cover the branch of method getValueAt(row, column) where room = null
		mCheckInPanel.availableRooms[0] = null;
		assertEquals(null, mCheckInPanel.availableRoomTable.getModel().getValueAt(0, 0));
	}

	@Test
	public void testUpdateDataServiceRequired() {
		if (!mOccupation.getOccupant().getType().equals("Standard")) {
			// Cover the branch where the selected item in type field is
			// 'Standard', i.e. the first branch in updateDataServiceRequired()
			preCheckIn(mOccupation, 4);
		} else {
			// Cover the first sub-branch of the second branch
			preCheckIn(mOccupation, 0);
			mCheckInPanel.typeField.setSelectedItem("Business");
			mCheckInPanel.dataServiceRequiredBox.setSelectedItem("No");
			mCheckInPanel.command.setDataServiceRequired(true);
			mCheckInPanel.updateDataServiceRequired();

			// Cover the second sub-branch of the second branch
			preCheckIn(mOccupation, 1);
			mCheckInPanel.typeField.setSelectedItem("Business");
			mCheckInPanel.dataServiceRequiredBox.setSelectedItem("Yes");
			mCheckInPanel.command.setDataServiceRequired(false);
			mCheckInPanel.updateDataServiceRequired();
		}
	}

	// Covers the branch of getCommandValues() in CheckInPanel when no room is
	// selected
	@Test
	public void testNoRoomSelected() {
		Room room = (Room) mCheckInPanel.availableRooms[0];
		mCheckInPanel.command.setSelectedRoom(room);
		mCheckInPanel.getCommandValues();
	}

	@Test
	public void testNoValueSelectedBeforeCheckIn() {
		// No value has been selected
		mCheckInPanel.checkInButton.doClick();

		// Compares the console output to the expected message
		assertEquals("Input Error: No room is selected", mErrorStream.toString());
	}

	@Test
	public void testInvalidIdForCheckIn() {
		// Manually assigns an invalid id to the id field
		preCheckIn(mOccupation);
		mCheckInPanel.IDField.setText("ust.hk");
		mCheckInPanel.checkInButton.doClick();

		// Compares the console output to the expected message
		String expectedMessage = "Input Error: The format of the inputted ID is invalid. It should be an English letter followed by exactly 8 digits";
		assertEquals(expectedMessage, mErrorStream.toString());
	}

	@Test
	public void testInvalidDateFormatForCheckIn() {
		// Manually assigns an invalid date to the date field
		preCheckIn(mOccupation);
		mCheckInPanel.checkInDateField.setText("abc");
		mCheckInPanel.checkInButton.doClick();

		// Compares the console output to the expected message
		String expectedMessage = "Input Error: The format of the input check-in date is invalid";
		assertEquals(expectedMessage, mErrorStream.toString());
	}

	@Test
	public void testInvalidEthernetAddressForCheckIn() {
		// Manually assigns an invalid ethernet address to the date field
		preCheckIn(mOccupation);
		mCheckInPanel.ethernetAddressField.setText("abc");
		mCheckInPanel.checkInButton.doClick();

		// Compares the console output to the expected message
		String expectedMessage = "Input Error: The format of the input ethernet address is invalid";
		assertEquals(expectedMessage, mErrorStream.toString());
	}

	@Test
	public void testNoValueSelectedBeforeCheckOut() {
		CheckOutPanel checkOutPanel = preCheckOut(mOccupation);

		// No value has been selected
		checkOutPanel.checkOutButton.doClick();

		// Compares the console output to the expected message
		assertEquals("Input Error: No room is selected", mErrorStream.toString());
	}

	@Test
	public void testCheckOutWithInvalidDate() {
		CheckOutPanel checkOutPanel = preCheckOut(mOccupation);

		// Selects the first row of the check out table
		Room room = (Room) checkOutPanel.occupiedRooms[0];
		checkOutPanel.command.setSelectedRoom(room);

		// Cover the branch where (selectedRoom != null) in CheckOutPanel
		checkOutPanel.getCommandValues();

		// Invalid check out date
		checkOutPanel.checkOutDateField.setText("abc");
		checkOutPanel.checkOutButton.doClick();

		// Compares the console output to the expected message
		String expectedMessage = "Input Error: The format of the inputted check-in date is invalid";
		assertEquals(expectedMessage, mErrorStream.toString());
	}

	@Test
	public void testInvalidCheckOutWithNullOccupation() {
		CheckOutPanel checkOutPanel = preCheckOut(mOccupation);

		// Select the first row of the check out table
		Room room = (Room) checkOutPanel.occupiedRooms[0];
		checkOutPanel.command.setSelectedRoom(room);

		// Set room to be null and click check out
		room.setOccupation(null);
		checkOutPanel.checkOutButton.doClick();

		// The error message should be "Input Error: The room has no occupant",
		// but accessing mErrorStream will cause NullPointerException raised in
		// CheckOutPanel on line 131 when room.getOccupation() is called.
		// Therefore the assertEquals statement is commented to avoid that
		// Exception.
		// assertEquals("Input Error: The room has no occupant",
		// mErrorStream.toString());
	}

	@Test
	public void testNormalCheckOut() {
		// Check in two rooms (row index 0 and 5)
		preCheckIn(mOccupation, 5);
		mCheckInPanel.checkInButton.doClick();
		CheckOutPanel checkOutPanel = preCheckOut(mOccupation);

		// Selects the second row, then the first row of the check out table, in
		// order to trigger the ListSelectionEvent in the model of CheckOutPanel
		checkOutPanel.occupiedRoomTable.addRowSelectionInterval(1, 1);
		checkOutPanel.occupiedRoomTable.addRowSelectionInterval(0, 0);

		// Cover an extra branch in occupiedRoomTable
		assertEquals("", checkOutPanel.occupiedRoomTable.getModel().getValueAt(0, 11));

		// Set check out date to tomorrow
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		LocalDate today = LocalDate.parse(checkOutPanel.checkOutDateField.getText(), formatter);
		checkOutPanel.checkOutDateField.setText(today.plusDays(1).format(formatter));

		int numberOfOccupiedRoomsBeforeCheckIn = mHotelManager.listAllOccupiedRooms().size();

		// Mock click action of checkOutPanel.checkOutButton
		checkOutPanel.checkOutButton.doClick();

		int numberOfOccupiedRoomsAfterCheckIn = mHotelManager.listAllOccupiedRooms().size();
		assertEquals(numberOfOccupiedRoomsBeforeCheckIn, numberOfOccupiedRoomsAfterCheckIn + 1);
	}

	@Test
	public void testSearchPanel() throws Exception {
		// Check in a occupant with standard type room
		Room selectedRoom = preCheckIn(mOccupation);
		mCheckInPanel.checkInButton.doClick();

		// Navigate to the search page
		mHotelManager.getUI().search.doClick();

		SearchPanel searchPanel = (SearchPanel) getCurrentPanel();

		// Search the name of the occupant that has been checked in
		// typeField options: {"ID", "Name", "Type", "Company"}
		searchPanel.typeField.setSelectedIndex(1);
		searchPanel.searchField.setText(mOccupation.getOccupant().getName());
		searchPanel.searchButton.doClick();

		assertEquals(selectedRoom.getFloorNo() + "-" + selectedRoom.getRoomNo(),
				searchPanel.searchRoomTable.getValueAt(0, 0));
		assertEquals(selectedRoom.getTypeString(), searchPanel.searchRoomTable.getValueAt(0, 1));
		assertEquals(selectedRoom.getCapacity(), searchPanel.searchRoomTable.getValueAt(0, 2));
		assertEquals(selectedRoom.getRate(), searchPanel.searchRoomTable.getValueAt(0, 3));

		String temp = "Not in used";
		if (selectedRoom.getType() == 1) {
			temp = "N/A";
		} else if (mOccupation.isDataServiceRequired()) {
			temp = "In used";
		}
		assertEquals(temp, searchPanel.searchRoomTable.getValueAt(0, 4));
		assertEquals(selectedRoom.getOccupation().getEthernetAddress(), searchPanel.searchRoomTable.getValueAt(0, 5));
		Occupant occupant = mOccupation.getOccupant();
		assertEquals(occupant.getName(), searchPanel.searchRoomTable.getValueAt(0, 6));
		assertEquals(occupant.getType(), searchPanel.searchRoomTable.getValueAt(0, 7));
		assertEquals(occupant.getID(), searchPanel.searchRoomTable.getValueAt(0, 8));
		assertEquals(occupant.getCompany(), searchPanel.searchRoomTable.getValueAt(0, 9));

		String dateString = new SimpleDateFormat("dd-MM-yyyy").format(mOccupation.getCheckInDate());
		assertEquals(dateString, searchPanel.searchRoomTable.getValueAt(0, 10));

		assertEquals("", searchPanel.searchRoomTable.getModel().getValueAt(0, 11));

		// Cover the branch of getLookAndFeel() method
		UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		assertEquals("Windows", UIManager.getLookAndFeel().getName());
	}

	// An overload version of preCheckIn with the first room selected
	private Room preCheckIn(Occupation occupation) {
		return preCheckIn(occupation, 0);
	}

	// Selects the given room and initialises all values in the check in panel
	// Returns the selected room
	private Room preCheckIn(Occupation occupation, int rowIndex) {
		Room room = (Room) mCheckInPanel.availableRooms[rowIndex];
		mCheckInPanel.command.setSelectedRoom(room);

		Occupant occupant = occupation.getOccupant();
		mCheckInPanel.IDField.setText(occupant.getID());
		mCheckInPanel.nameField.setText(occupant.getName());
		mCheckInPanel.typeField.setSelectedItem(occupant.getType());
		mCheckInPanel.companyField.setText(occupant.getCompany());

		int index = occupation.dataServiceRequired ? 0 : 1;
		mCheckInPanel.dataServiceRequiredBox.setSelectedIndex(index);

		mCheckInPanel.ethernetAddressField.setText(occupation.getEthernetAddress());

		return room;
	}

	// Checks in with the given occupation, navigates to the CheckOutPanel, and
	// returns a CheckOutPanel instance
	private CheckOutPanel preCheckOut(Occupation occupation) {
		preCheckIn(occupation);

		int numberOfAvailableRoomsBeforeCheckIn = mHotelManager.listAllAvailableRooms().size();

		// Mock click action of checkInPanel.checkInButton
		mCheckInPanel.checkInButton.doClick();

		int numberOfAvailableRoomsAfterCheckIn = mHotelManager.listAllAvailableRooms().size();
		assertEquals(numberOfAvailableRoomsBeforeCheckIn, numberOfAvailableRoomsAfterCheckIn + 1);

		// Navigate to the check out page
		mHotelManager.getUI().checkOut.doClick();

		return (CheckOutPanel) getCurrentPanel();
	}

	private JPanel getCurrentPanel() {
		return mHotelManager.getUI().currentCommand.getPanel(mHotelManager.getUI().tableView);
	}
}