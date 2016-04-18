package tax.calculator;

import java.util.ArrayList;
import java.util.List;

/**
 * This calculator computes the total amount of stamp duty land tax required to
 * pay for the given housing property.
 * 
 * @author Milton
 * @see https://www.gov.uk/stamp-duty-land-tax/residential-property-rates
 *
 */
public class Calculator {
	private List<TaxRecord> taxLookup = new ArrayList<TaxRecord>();

	public Calculator() {
		initialise();
	}

	/**
	 * This method initialises the list of taxLookup.
	 */
	private void initialise() {
		this.taxLookup.add(new TaxRecord(125000, 0));
		this.taxLookup.add(new TaxRecord(125000, 0.02));
		this.taxLookup.add(new TaxRecord(675000, 0.05));
		this.taxLookup.add(new TaxRecord(575000, 0.1));
		this.taxLookup.add(new TaxRecord(Integer.MAX_VALUE, 0.12));
	}

	/**
	 * This method returns the total stamp duty land tax needed to pay.
	 * 
	 * @param value
	 *            Value of the housing property
	 * @return The amount of money taxed on the value of the given housing
	 *         property
	 */
	public double totalTax(int value) {
		int remainingValue = value;
		double totalTax = 0.0;

		while (remainingValue > 0) {
			for (TaxRecord taxRecord : this.taxLookup) {
				int taxedValue = Integer.min(taxRecord.getThreshold(), remainingValue);
				totalTax += taxedValue * taxRecord.getTaxRate();
				remainingValue -= taxedValue;

				if (remainingValue == 0) {
					break;
				}
			}
		}

		return totalTax;
	}
}