package tax.calculator;

/**
 * This record contains a threshold and the corresponding tax rate.
 * 
 * @author Milton
 *
 */
public class TaxRecord {
	private int threshold;
	private double taxRate;

	public TaxRecord(int threshold, double taxRate) {
		this.threshold = threshold;
		this.taxRate = taxRate;
	}

	public int getThreshold() {
		return this.threshold;
	}

	public double getTaxRate() {
		return this.taxRate;
	}
}