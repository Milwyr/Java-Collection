package hms.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import hms.test.gui.GUITest;
import hms.test.gui.ManageRoomPanelTest;
import hms.test.main.HotelManagerTest;
import hms.test.main.HotelTest;

@RunWith(Suite.class)
@SuiteClasses({ CommandTest.class, GUITest.class, HotelTest.class, HotelManagerTest.class, ManageRoomPanelTest.class,
		ModelTest.class })
public class AllTests {

}
