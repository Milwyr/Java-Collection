package betting.exchange.concrete;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import betting.exchange.definition.AbstractMapBase;
import betting.exchange.definition.PriceLadderMap;
import betting.exchange.definition.PricedVolume;

public class PriceLadderMapImpl extends AbstractMapBase<Integer, PricedVolume> implements PriceLadderMap {

	private final Map<Integer, PricedVolume> ladder = new TreeMap<Integer, PricedVolume>();

	@Override
	protected PricedVolume getValueFor(Integer price) {
		return ladder.get(price);
	}

	@Override
	public void clear() {
		ladder.clear();
	}

	@Override
	public Set<Entry<Integer, PricedVolume>> entrySet() {
		return ladder.entrySet();
	}

	@Override
	public int size() {
		return ladder.size();
	}

	@Override
	public int pool(int price, int increment) {
		// Add increment to ladder's value
		if (ladder.containsKey(price)) {
			ladder.get(price).add(increment);
		}
		// Create an entry of type PricedVolume if the given price does not
		// exist in the ladder
		else {
			PricedVolumeImpl p = new PricedVolumeImpl();
			p.add(increment);
			ladder.put(new Integer(price), p);
		}

		return ladder.get(price).getVolume();
	}

	@Override
	public int spend(int price, int decrement) {
		// Find total amount of volumes that are larger than the price
		int availableVolume = 0;
		for (Entry<Integer, PricedVolume> l : ladder.entrySet()) {
			if (l.getKey() >= price) {
				availableVolume += l.getValue().getVolume();
			}
		}

		// Remaining value volume has to subtract
		int remainingDecrement = decrement;

		while (availableVolume > 0 && remainingDecrement > 0) {
			// Start iterating from the last entry, i.e. the entry with the
			// highest key (matched price)
			for (Integer key : ((TreeMap<Integer, PricedVolume>) ladder).descendingKeySet()) {
				// Terminate if ladder's price is smaller than the given price
				if (key < price) {
					break;
				}

				if (ladder.get(key).getVolume() > 0) {
					int subtractedValue = ladder.get(key).subtract(remainingDecrement);
					availableVolume -= subtractedValue;
					remainingDecrement -= subtractedValue;

					if (remainingDecrement < 0)
						remainingDecrement = 0;
				}
			}
		}

		// Remove the entry with zero volume
		ladder.entrySet().removeIf(entry -> entry.getValue().getVolume() == 0);

		return decrement - remainingDecrement;
	}

	@Override
	public int cancel(int price, int decrement) {
		// Return 0 if the price is not in the ladder
		if (ladder.get(price) == null) {
			return 0;
		}

		// This decrement is never larger than the volume in the ladder
		int realDecrement = Math.min(decrement, ladder.get(price).getVolume());

		if (realDecrement == 0) {
			return 0;
		}

		ladder.get(price).subtract(realDecrement);
		return realDecrement;
	}

}