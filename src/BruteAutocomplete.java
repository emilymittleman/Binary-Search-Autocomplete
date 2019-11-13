import java.util.*;

/**
 * Implements Autocompletor by scanning through the entire array of terms for
 * every topKMatches or topMatch query.
 */
public class BruteAutocomplete implements Autocompletor {

	protected Term[] myTerms;
	protected int mySize;

	/**
	 * Create immutable instance with terms constructed from parameter
	 * @param terms words such that terms[k] is part of a word pair 0 <= k < terms.length
	 * @param weights weights such that weights[k] corresponds to terms[k]
	 * @throws NullPointerException if either parameter is null
	 * @throws IllegalArgumentException if terms.length != weights.length
	 * @throws IllegalArgumentException if any elements of weights is negative
	 * @throws IllegalArgumentException if any elements of terms is duplicate
	 */
	public BruteAutocomplete(String[] terms, double[] weights) {

		if (terms == null || weights == null) {
			throw new NullPointerException("One or more arguments null");
		}

		if (terms.length != weights.length) {
			throw new IllegalArgumentException("terms and weights are not the same length");
		}
		initialize(terms,weights);
	}

	/**
	 * Required by the Autocompletor interface. Returns an array containing the
	 * k words in myTerms with the largest weight which match the given prefix,
	 * in descending weight order. If less than k words exist matching the given
	 * prefix (including if no words exist), then the array instead contains all
	 * those words. e.g. If terms is {air:3, bat:2, bell:4, boy:1}, then
	 * topKMatches("b", 2) should return {"bell", "bat"}, but topKMatches("a",
	 * 2) should return {"air"}
	 *
	 * @param prefix
	 *            - A prefix which all returned words must start with
	 * @param k
	 *            - The (maximum) number of words to be returned
	 * @return An array of the k words with the largest weights among all words
	 *         starting with prefix, in descending weight order. If less than k
	 *         such words exist, return an array containing all those words If
	 *         no such words exist, reutrn an empty array
	 * @throws NullPointerException if prefix is null
	 */
	@Override
	public List<Term> topMatches(String prefix, int k) {
		if (k < 0) {
			throw new IllegalArgumentException("Illegal value of k:"+k);
		}
		
		// maintain pq of size k
		PriorityQueue<Term> pq = new PriorityQueue<Term>(Comparator.comparing(Term::getWeight));
		for (Term t : myTerms) {
			if (!t.getWord().startsWith(prefix))
				continue;
			if (pq.size() < k) {
				pq.add(t);
			} else if (pq.peek().getWeight() < t.getWeight()) {
				pq.remove();
				pq.add(t);
			}
		}
		int numResults = Math.min(k, pq.size());
		LinkedList<Term> ret = new LinkedList<>();
		for (int i = 0; i < numResults; i++) {
			ret.addFirst(pq.remove());
		}
		return ret;
	}

	/**
	 * Required by the Autocompletor interface. Called by the constructor to
	 * initialize myTerms to be a sorted array of Terms
	 *
	 * @param terms
	 *            - A list of words to form terms from
	 * @param weights
	 *            - A corresponding list of weights, such that terms[i] has
	 *            weight[i].
	 */
	@Override
	public void initialize(String[] terms, double[] weights) {
		myTerms = new Term[terms.length];

		HashSet<String> words = new HashSet<>();

		for (int i = 0; i < terms.length; i++) {
			words.add(terms[i]);
			myTerms[i] = new Term(terms[i], weights[i]);
			if (weights[i] < 0) {
				throw new IllegalArgumentException("Negative weight "+ weights[i]);
			}
		}
		if (words.size() != terms.length) {
			throw new IllegalArgumentException("Duplicate input terms");
		}
	}

	/**
	 * Required by the Autocompletor interface. Calculates how
	 * many bytes the array myTerms takes up
	 *
	 * @return mySize - the number of bytes in myTerms
	 */
	@Override
	public int sizeInBytes() {
		if (mySize == 0) {
			
			for(Term t : myTerms) {
			    mySize += BYTES_PER_DOUBLE + 
			    		BYTES_PER_CHAR*t.getWord().length();	
			}
		}
		return mySize;
	}
}
