package betting.exchange.definition;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * There is some similarity between a Map and a price ladder. There are some
 * things, however, that aren't relevant, or whose implementation is just not
 * interesting in the wider context of the exercise. This base class has been
 * created to provide a place to put things we really want you to ignore so you
 * can concentrate on the problem to be solved.
 */
public abstract class AbstractMapBase<K, V> implements Map<K, V> {

	/**
	 * Can't overload "get" due to the way Java handles generic types. This is
	 * just a simple method so you need to worry less about typing/casting in
	 * the actual implementation class.
	 */
	protected abstract V getValueFor(K key);

	@SuppressWarnings("unchecked")
	@Override
	public V get(Object key) {
		// While greater validation is possible, we don't want people wasting
		// their time
		// when they could be working on the actual problem at hand.
		return getValueFor((K) key);
	}

	@Override
	public boolean containsKey(Object key) {
		// It is possible that some implementations may be able to determine
		// whether the Map contains a key
		// more efficiently than the simple approach used here.
		// This would be a distraction from the exercise at hand though, and so
		// this is perfectly adequate.
		return (null != get(key));
	}

	@Override
	public boolean isEmpty() {
		// Same as in "containsKey":
		// Any potential optimisations for the exact implementation aren't as
		// interesting as the rest of the solution.
		return (0 == size());
	}

	@Override
	public V put(K key, V value) {
		// Although the comment here makes reference to a method that is
		// "unknown" to this abstraction,
		// we only (currently) have one use case, and it is simpler to just
		// throw an exception here rather
		// than to add extra complexity to the exercise for the sake of absolute
		// correctness.
		throw new UnsupportedOperationException("Invalid to put directly - use pool instead");
	}

	@Override
	public V remove(Object key) {
		// See comment in "put" - the same consideration applies here.
		throw new UnsupportedOperationException("Invalid to remove directly - use spend or cancel instead");
	}

	@Override
	public boolean containsValue(Object v) {
		throw new UnsupportedOperationException("Uninteresting");
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		throw new UnsupportedOperationException("Uninteresting");
	}

	@Override
	public Set<K> keySet() {
		throw new UnsupportedOperationException("Uninteresting");
	}

	@Override
	public Collection<V> values() {
		throw new UnsupportedOperationException("Uninteresting");
	}
}