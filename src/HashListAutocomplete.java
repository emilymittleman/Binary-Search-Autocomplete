import java.util.*;

public class HashListAutocomplete implements Autocompletor {

    private static final int MAX_PREFIX = 10;
    private Map<String, List<Term>> myMap;
    private int mySize;

    /**
     * Given arrays of words and weights, initialize myMap to HashMap of (key,value) pairs
     * with the key being a prefix and the value is an array of Terms sorted by weight.
     *
     * @param terms - A list of words to form terms from
     * @param weights - A corresponding list of weights, such that terms[i] has
     *                weight[i].
     * @return a HashListAutocomplete whose myTerms object has myTerms[i] =
     *         a Term with word terms[i] and weight weights[i].
     * @throws NullPointerException if either argument passed in is null
     */
    public HashListAutocomplete(String[] terms, double[] weights) {
        if (terms == null || weights == null) {
            throw new NullPointerException("One or more arguments null");
        }

        initialize(terms,weights);
    }

    /**
     * Required by the Autocompletor interface. Called by the constructor to
     * initialize myMap to be a HashMap of (key,value) pairs with the key being
     * a prefix and the value is a sorted ArrayList of Terms sorted by weight.
     *
     * @param terms
     *            - A list of words to form terms from
     * @param weights
     *            - A corresponding list of weights, such that terms[i] has
     *            weight[i].
     */
    @Override
    public void initialize(String[] terms, double[] weights) {
        myMap = new HashMap<>();
        for(int i=0; i<terms.length; i++) {
            String term = terms[i];

            int maxPrefixes = Math.min(MAX_PREFIX, term.length());

            for(int j=0; j<=maxPrefixes; j++) {
                String prefix = term.substring(0, j);
                myMap.putIfAbsent(prefix, new ArrayList<Term>());
                myMap.get(prefix).add(new Term(term, weights[i]));
            }
        }
        // sort the ArrayLists for each key
        for(String key : myMap.keySet()) {
            Collections.sort(myMap.get(key), Comparator.comparing(Term::getWeight).reversed());
        }
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
     */
    @Override
    public List<Term> topMatches(String prefix, int k) {
        prefix = prefix.substring(0, Math.min(prefix.length(), MAX_PREFIX));
        if(myMap.containsKey(prefix)) {
            List<Term> all = myMap.get(prefix);
            return all.subList(0, Math.min(k, all.size()));
        }
        return new ArrayList<>();
    }

    /**
     * Required by the Autocompletor interface. Calculates how
     * many bytes are in myMap - this includes all the keys, which
     * are Strings, and all the values, which are Terms, so each Term
     * contains a String and a double.
     *
     * @return mySize - the number of bytes in myMap
     */
    @Override
    public int sizeInBytes() {
        if(mySize == 0) {
            for(String key : myMap.keySet()) {
                mySize += BYTES_PER_CHAR * key.length();
                for(Term t : myMap.get(key)) {
                    mySize += BYTES_PER_DOUBLE +
                            BYTES_PER_CHAR*t.getWord().length();
                }
            }
        }
        return mySize;
    }
}
