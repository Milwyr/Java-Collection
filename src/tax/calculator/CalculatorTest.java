package tax.calculator;

import static org.junit.Assert.*;

import org.junit.Test;

public class CalculatorTest {

	@Test
	public void test() {
		// The maximum delta between expected and actual for which both numbers
		// are still considered equal
		double delta = 0.001;

		Calculator calculator = new Calculator();
		assertEquals(0, calculator.totalTax(90000), delta);
		assertEquals(1100, calculator.totalTax(180000), delta);
		assertEquals(8000, calculator.totalTax(360000), delta);
		assertEquals(43750, calculator.totalTax(1000000), delta);
		assertEquals(513750, calculator.totalTax(5000000), delta);
	}

}
