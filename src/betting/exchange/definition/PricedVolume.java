package betting.exchange.definition;

/**
 * Represents a volume (amount of currency available) at a given price.
 * 
 * @author Milton
 *
 */
public interface PricedVolume {

	/**
	 * Adds the given value to the amount available at this price. This would
	 * happen, for example, when a submitted bet cannot be matched, and so a
	 * BACK bet's size would become available to be matched as part of the pool
	 * on the LAY side (and vice versa).
	 * 
	 * @param increment
	 *            The amount to increase the volume by.
	 * @return Volume after the increment
	 */
	int add(int increment);

	/**
	 * Subtracts the given amount from the pool available at this price (as
	 * appropriate). This might happen when a bet is submitted, and there is
	 * volume available on the "other side" (BACK versus LAY) against which it
	 * can be matched. Because it <b><i>is</i></b> matched, that volume is no
	 * longer available.
	 * 
	 * @param decrement
	 *            The amount to decrease the volume by.
	 * @return Volume after the decrement
	 */
	int subtract(int decrement);

	/**
	 * @return The amount available to be matched at this price.
	 */
	int getVolume();
}