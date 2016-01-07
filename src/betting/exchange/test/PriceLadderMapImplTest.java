package betting.exchange.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import betting.exchange.concrete.PriceLadderMapImpl;

public class PriceLadderMapImplTest {

	private PriceLadderMapImpl ladderMap;

	@Before
	public void setUp() {
		ladderMap = new PriceLadderMapImpl();
		ladderMap.pool(3, 2);
		ladderMap.pool(4, 5);
		ladderMap.pool(5, 3);
		ladderMap.pool(6, 1);
	}

	@Test
	public void testSpend() {
		assertEquals(0, ladderMap.spend(9, 6));
		assertEquals(4, ladderMap.spend(5, 10));
		assertEquals(6, ladderMap.spend(3, 6));
		assertEquals(1, ladderMap.spend(2, 10));
		assertTrue(ladderMap.isEmpty());
	}

	@Test
	public void testCancel() {
		assertEquals(0, ladderMap.cancel(9, 3));
		assertEquals(3, ladderMap.cancel(4, 3));
		assertEquals(2, ladderMap.cancel(4, 3));
		assertEquals(0, ladderMap.cancel(4, 3));
		assertEquals(2, ladderMap.get(3).getVolume());
		assertEquals(3, ladderMap.get(5).getVolume());
		assertEquals(1, ladderMap.get(6).getVolume());
	}

}