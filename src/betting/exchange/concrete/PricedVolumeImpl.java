package betting.exchange.concrete;

import betting.exchange.definition.PricedVolume;

/**
 * Simple implementation of PricedVolume.
 * 
 * @author Milton
 *
 */
public class PricedVolumeImpl implements PricedVolume {
	
	private int volume = 0;

	@Override
	public int add(int increment) {
		return (volume += increment);
	}

	@Override
	public int subtract(int decrement) {

		if (volume < decrement) {
			int v = volume;
			volume = 0;
			return v;
		}

		volume -= decrement;
		return decrement;
	}

	@Override
	public int getVolume() {
		return volume;
	}
}
