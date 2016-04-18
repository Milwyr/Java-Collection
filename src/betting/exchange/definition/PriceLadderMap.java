package betting.exchange.definition;

import java.util.Map;

/**
 * Represents the amount available to be matched on one "side" (BACK or LAY) of
 * one selection within a market. This extends Map because there are some
 * similarities with that interface, and so this is a good starting point for
 * anyone unfamiliar with Betfair's exchange, but there are some differences,
 * particularly in how values get added to and removed from the collection.
 */
public interface PriceLadderMap extends Map<Integer, PricedVolume> {

	/**
	 * Adds the given volume (increment) to the amount available at the given
	 * price. This would happen, for example, when a submitted bet cannot be
	 * matched, and so a BACK bet's size would become available to be matched as
	 * part of the pool on the LAY side (and vice versa).
	 * 
	 * @param price
	 *            The price at which to add the volume.
	 * @param increment
	 *            The amount to increase the volume by.
	 * @return The volume of the object PricedVolume with the given price
	 */
	int pool(int price, int increment);

	/**
	 * Spends the given volume (decrement) from the pool available at the best
	 * price possible, down to equal or better than the given price (as
	 * appropriate/available). This might happen when a bet is submitted, and
	 * there is volume available on the "other side" (BACK versus LAY) against
	 * which it can be matched. Because it <b><i>is</i></b> matched, that volume
	 * is no longer available.
	 * 
	 * @param limitPrice
	 *            The price "below" which nothing should be spent.
	 * @param decrement
	 *            The amount to decrease the volume by.
	 * @return The available volume with the given price after the decrement
	 */
	int spend(int limitPrice, int decrement);

	/**
	 * Cancels the given volume (decrement) so it is no longer available to be
	 * matched/spent.
	 * 
	 * @param price
	 *            The price at which to decrease the volume.
	 * @param decrement
	 *            The amount to decrease the volume by.
	 * @return The amount of volume decreased at the given price
	 */
	int cancel(int price, int decrement);
}