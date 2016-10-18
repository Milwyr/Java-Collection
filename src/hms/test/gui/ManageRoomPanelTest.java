package hms.test.gui;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Before;
import org.junit.Test;

import hms.command.ManageRoomCommand;
import hms.gui.ManageRoomPanel;
import hms.main.HotelManager;
import hms.model.Room;

public class ManageRoomPanelTest {
	private HotelManager mHotelManager;
	private ManageRoomPanel mManageRoomPanel;

	// The stream that captures the System.err.print in the console
	private ByteArrayOutputStream mErrorStream;

	// The stream that captures the System.out.print in the console
	private ByteArrayOutputStream mOutStream;

	@Before
	public void setUp() {
		mHotelManager = new HotelManager();
		mManageRoomPanel = new ManageRoomPanel(new ManageRoomCommand(), mHotelManager);

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
	public void testGetCommandValues() {
		// Navigate to the manage room page
		mHotelManager.getUI().manageRooms.doClick();

		// Cover the branch of method getValueAt(row, column) where column > 4
		assertEquals("", mManageRoomPanel.editableRoomsTable.getModel().getValueAt(0, 5));

		// Cover the branch of method getValueAt(row, column) where room = null
		mManageRoomPanel.editableRooms[0] = null;
		assertEquals(null, mManageRoomPanel.editableRoomsTable.getModel().getValueAt(0, 0));
	}
	
	@Test
	public void testNoRoomSelected() {
		// Navigate to the manage room page
		mHotelManager.getUI().manageRooms.doClick();

		mManageRoomPanel.editBtn.doClick();

		// Compare the console output to the expected message
		assertEquals("Warning: no room selected!", mOutStream.toString());
	}

	@Test
	public void testCancelButtonClicked() {
		preEditSelectedRoom();
		mManageRoomPanel.cancelBtn.doClick();
		assertTrue(mManageRoomPanel.editableRoomsTable.isEnabled());
	}

	@Test
	public void testUpdateWithInvalidRate() {
		preEditSelectedRoom();

		// Assign an invalid value to the rate field
		mManageRoomPanel.rate.setText("abc");
		mManageRoomPanel.updateBtn.doClick();
		assertEquals("Error: Rate not correctly set!", mErrorStream.toString());
	}

	@Test
	public void testUpdateWithInvalidDiscount() {
		preEditSelectedRoom();

		// Assigns an invalid value to the discount field
		mManageRoomPanel.discount.setText("abc");
		mManageRoomPanel.updateBtn.doClick();

		// The message is concatenated because the program does not return after
		// printing the first error
		String expectedMessage = "Error: Discount not correctly set!Error: Invalid room rate or discount!";
		assertEquals(expectedMessage, mErrorStream.toString());
	}

	@Test
	public void testUpdateWithInvalidRateOrDiscount() {
		preEditSelectedRoom();

		// Rate < 0, discount > 100
		mManageRoomPanel.rate.setText("-1");
		mManageRoomPanel.discount.setText("200");
		mManageRoomPanel.updateBtn.doClick();
		assertEquals("Error: Invalid room rate or discount!", mErrorStream.toString());
	}

	@Test
	public void testNormalUpdate() {
		preEditSelectedRoom(0);

		mManageRoomPanel.rate.setText("689.0");
		mManageRoomPanel.discount.setText("15.0");
		mManageRoomPanel.updateBtn.doClick();

		// Selects the rate column from the room which was just updated
		Room room = (Room) mManageRoomPanel.editableRooms[0];

		// The delta value is huge as the double is rounded to integer
		assertEquals(689.0 * 0.85, room.getRate(), 0.9);

		// Cover the branch (roomInfoPanel != null) where for update and edit
		// buttons are clicked
		preEditSelectedRoom(0);
		mManageRoomPanel.roomInfoPanel = null;
		mManageRoomPanel.rate.setText("689.0");
		mManageRoomPanel.discount.setText("15.0");
		mManageRoomPanel.updateBtn.doClick();

		// Cover the branch where the cancel button is clicked
		preEditSelectedRoom(0);
		mManageRoomPanel.roomInfoPanel = null;
		mManageRoomPanel.cancelBtn.doClick();
	}

	// An overload version which selects the first row
	private void preEditSelectedRoom() {
		preEditSelectedRoom(0);
	}

	// Navigates to the manage room page, selects the room in the given row and
	// clicks the edit button
	private void preEditSelectedRoom(int rowIndex) {
		// Navigate to the manage room page
		mHotelManager.getUI().manageRooms.doClick();

		// Select the first row in the table
		mManageRoomPanel.editableRoomsTable.setRowSelectionInterval(0, 0);
		mManageRoomPanel.editBtn.doClick();
	}
}