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

    @Override
    public void initialize(String[] terms, double[] weights) {
        myMap = new HashMap<>();
        for(int i=0; i<terms.length; i++) {
            String term = terms[i];

            //int maxPrefixes = MAX_PREFIX;
            //if(term.length() < maxPrefixes) { maxPrefixes = term.length(); }
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

    @Override
    public List<Term> topMatches(String prefix, int k) {
        prefix = prefix.substring(0, Math.min(prefix.length(), MAX_PREFIX));
        if(myMap.containsKey(prefix)) {
            List<Term> all = myMap.get(prefix);
            return all.subList(0, Math.min(k, all.size()));
        }
        return new ArrayList<>();
    }

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
