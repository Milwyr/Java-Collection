package hms.test;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import hms.command.CheckInCommand;
import hms.command.CheckOutCommand;
import hms.main.HotelManager;

public class CommandTest {

	private Date mNow = Calendar.getInstance().getTime();
	
	@Before
	public void setUp() {
		mNow = Calendar.getInstance().getTime();
	}
	
	@Test
	public void testInvalidCheckIn() {
		CheckInCommand checkInCommand = new CheckInCommand(new HotelManager());
		
		// Check in with an invalid ID
		checkInCommand.setID("123456789");
		checkInCommand.setName("Jamie Vardy");
		checkInCommand.setCompany("Leicester City");
		checkInCommand.setCheckInDate(mNow);
		checkInCommand.setEthernetAddress("00:00:00:00:00:00");
		checkInCommand.setDataServiceRequired(false);
		checkInCommand.setType("Standard");
		checkInCommand.checkIn();
		
		// Verify the getter methods
		assertEquals("123456789", checkInCommand.getID());
		assertEquals("Jamie Vardy", checkInCommand.getName());
		assertEquals("Leicester City", checkInCommand.getCompany());
		assertEquals(mNow, checkInCommand.getCheckInDate());
		assertEquals("00:00:00:00:00:00", checkInCommand.getEthernetAddress());
		assertEquals(false, checkInCommand.isDataServiceRequired());
		assertEquals("Standard", checkInCommand.getType());
	}
	
	@Test
	public void testCheckOut() {
		CheckOutCommand checkOutCommand = new CheckOutCommand(new HotelManager());

		// Cover the branch of unsuccessful checkout in CheckoutCommand
		assertEquals("No input can be null", checkOutCommand.checkOut());

		// Increase line coverage on the setter methods of CheckOutCommand
		checkOutCommand.setID("123456789");
		checkOutCommand.setName("Jamie Vardy");
		checkOutCommand.setCompany("Leicester City");
		checkOutCommand.setCheckInDate(mNow);
		checkOutCommand.setEthernetAddress("00:00:00:00:00:00");
		checkOutCommand.setDataServiceRequired(false);
		checkOutCommand.setType("Standard");

		// Increase line coverage on the getter methods of CheckOutCommand
		assertEquals("123456789", checkOutCommand.getID());
		assertEquals("Jamie Vardy", checkOutCommand.getName());
		assertEquals("Leicester City", checkOutCommand.getCompany());
		assertEquals(mNow, checkOutCommand.getCheckInDate());
		assertEquals("00:00:00:00:00:00", checkOutCommand.getEthernetAddress());
		assertEquals(false, checkOutCommand.isDataServiceRequired());
		assertEquals("Standard", checkOutCommand.getType());
	}

}