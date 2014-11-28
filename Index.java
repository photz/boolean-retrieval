import java.util.Collection;

import java.util.Map;
import java.util.SortedMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import java.util.Arrays;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.ArrayList;


public class Index {
    private Map<String,PostingsList> postingsLists;

    /**
     * Attempts to load an existing index from a file.
     */
    public Index(String filename) {
	
    }


    /**
     * Creates a new Index object from scratch without.
     */
    public Index() {
	postingsLists = new HashMap<String, PostingsList>();
    }

    /**
     * Adds a document to the index.
     */
    public void addDocument(int doc_id, String content) {
	assert content != null;

	List<String> tokens = getTokens(content);

	assert null != tokens;

	// while iterating over the tokens inside the document
	// we keep an integer that represents the position
	// of the current token in the document
	int token_pos = 0;

	assert null != postingsLists;

	for (String token : tokens) {
	    
	    assert null != token;

	    assert true == postingsLists.containsKey(token);

	    // if we encounter this term for the first time
	    // create a new potings list for it
	    if (!postingsLists.containsKey(token)) {
		postingsLists.put(token,
	    			  new PostingsList());
	    }

	    postingsLists.get(token).addPosting(doc_id,
						token_pos);

	    token_pos++;
	}
    }

    /**
     * 
     */
    public Set<Integer>andSearch(Collection<String> terms) {

	assert null != postingsLists;
	assert null != terms;

	// if the query contains no terms, return an empty result
	if (terms.size() == 0) {
	    return new HashSet<Integer>();
	}

	// likewise, if the query contains a String that's
	// not in the vocabulary, we return an empty result set
	for (String term : terms) {
	    
	    // queries are supposed to be case-insensitive
	    // so everything's converted to lower case
	    term = term.toLowerCase();

	    if (!postingsLists.containsKey(term)) {
		return new HashSet<Integer>();
	    }
	}

	Iterator<String> it = terms.iterator();

	PostingsList merged = postingsLists.get(it.next().toLowerCase());

	// iterate over the terms inside the query
	while (it.hasNext()) {
	    PostingsList next_list = postingsLists.get(it.next().toLowerCase());

	    assert next_list != null;

	    merged = merged.intersect(next_list);
	}

	return merged.getDocuments();
    }

    /**
     * Attempts to find an occurrence of the given
     * phrase in the document collection.
     */
    public Collection<Integer> findPhrase(String phrase) {
	assert phrase != null;
	assert null != postingsLists;


	if (phrase.isEmpty()) {
	    return new HashSet<Integer>();
	}

	List<String> terms = getTokens(phrase);

	assert terms.size() > 0;

	// likewise, if the query contains a String that's
	// not in the vocabulary, we return an empty result set
	for (String term : terms) {

	    if (!postingsLists.containsKey(term)) {
		return new HashSet<Integer>();
	    }
	}

	Iterator<String> it = terms.iterator();

	PostingsList merged = postingsLists.get(it.next());

	// iterate over the terms inside the query
	while (it.hasNext()) {
	    PostingsList next_list = postingsLists.get(it.next());

	    assert next_list != null;

	    merged = merged.posIntersect(next_list);
	}

	return merged.getDocuments();
    }

    /**
     * Store the index permanently on disk.
     */
    public void save(String filename) {
	
    }


    private static List<String> getTokens(String text) {
	assert text != null;

	text = text.toLowerCase();

	String tokens_arr [] = text.split(" ");

	List<String> tokens_coll = new ArrayList<String>(Arrays.asList(tokens_arr));

	return tokens_coll;
    }
}

