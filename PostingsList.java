import java.util.Collection;
import java.util.SortedMap;


import java.util.Set;
import java.util.Iterator;
import java.util.Map;

import java.io.Serializable;

import java.util.TreeMap;
import java.util.ArrayList;

public class PostingsList implements Serializable {
    
    private SortedMap<Integer, Collection<Integer>> postings;

    public PostingsList() {
	postings = new TreeMap<Integer, Collection<Integer>>();
    }


    private PostingsList(SortedMap<Integer, Collection<Integer>> _postings) {
	postings = _postings;
    }


    /**
     * Adds a posting to the potings list.
     * For any document, a term can occur multiple times.
     * It is assumed that documents are scanned sequentially
     * from the beginning, so for 
     */
    public void addPosting(Integer doc_id, Integer pos) {
	if (!postings.containsKey(doc_id)) {
	    // create a new posting for this document

	    postings.put(doc_id,
			 new ArrayList<Integer>());
	}

	postings.get(doc_id).add(pos);
    }



    /**
     * Performs a 'positional merge' of two postings lists.
     * If the first postings list belongs to a term t1,
     * and the second one belongs to a term t2,
     * then the new postings list will contain only those
     * documents in which t1 and t2 occur next to each other.
     */
    public PostingsList posIntersect(PostingsList list2) {

	PostingsList list3 = new PostingsList();

	if (postings.size() == 0
	    || list2.postings.size() == 0) {
	    
	    return list3;
	}

	// TODO eliminate the repetition in intersect()
	// and posIntersect()

	Iterator<Map.Entry<Integer, Collection<Integer>>> list2_iterator = list2.postings.entrySet().iterator();


	Map.Entry<Integer, Collection<Integer>> list2_crnt = list2_iterator.next();

	for (Map.Entry<Integer, Collection<Integer>> posting : 
		 postings.entrySet()) { 

	    // keep advancing the second pointer until
	    // we hit a document id that's equal to or greater
	    // than the current document id in the outer loop
	    while (list2_iterator.hasNext()
		   && list2_crnt.getKey().intValue() < posting.getKey().intValue()) {

		list2_crnt = list2_iterator.next();
	    }


	    if (list2_crnt.getKey().intValue() == posting.getKey().intValue()) {

		// if there are no positions, something's broken
		assert list2_crnt.getValue().size() > 0;
		assert posting.getValue().size() > 0;
		

		// an iterator to iterate over the positions
		// in the second postings list
		Iterator<Integer> list2_iter = list2_crnt.getValue().iterator();

		Integer list2_pos_crnt = list2_iter.next();

		// iterate over the positions at which the term
		// occurs
		for (Integer pos_list1 : posting.getValue()) {
		    
		    // we're trying to find a configuration
		    // where pos_list1 + 1 == list2_pos_crnt

		    while (list2_iter.hasNext()
			   && list2_pos_crnt.intValue() <= 1 + pos_list1.intValue()) {
			
			list2_pos_crnt = list2_iter.next();
		    }

		    if (list2_pos_crnt.intValue() == 1 + pos_list1.intValue()) {

			// we found a phrase, now add its
			// position to the result
			// note that we add the position
			// of the rightmost element,
			// so further concatenations are possible

			list3.addPosting(posting.getKey(),
					 list2_pos_crnt);

			//break;
		    }
		}
	    }
	}

	return list3;
    }





    public PostingsList intersect(PostingsList list2) {
	// TODO make use of subMap() or tailMap()

	PostingsList list3 = new PostingsList();

	// if one of the postings lists involved is empty,
	// the result is necessarily empty, too
	if (postings.size() == 0
	    || list2.postings.size() == 0) {

		return list3;
	    }

	Iterator<Map.Entry<Integer, Collection<Integer>>> list2_iterator = list2.postings.entrySet().iterator();


	Map.Entry<Integer, Collection<Integer>> list2_crnt = list2_iterator.next();

	for (Map.Entry<Integer, Collection<Integer>> posting : 
		 postings.entrySet()) { 

	    // keep advancing the second pointer until
	    // we hit a document id that's equal to or greater
	    // than the current document id in the outer loop
	    while (list2_iterator.hasNext()
		   && list2_crnt.getKey().intValue() < posting.getKey().intValue()) {

		list2_crnt = list2_iterator.next();
	    }

	    if (list2_crnt.getKey().intValue() == posting.getKey().intValue()) {
		// add to result
		list3.addPosting(posting.getKey(), 1);
	    }
	}
	
	return list3;
    }

    /**
     * Returns a Collection of Documents that occur
     * in this PostingsList.
     */
    public Set<Integer> getDocuments() {
	return postings.keySet();
    }

    /**
     * When doing a phrase search, a phrase may occur multiple
     * times in the same document.  This method returns
     * the total number of occurrences.  This number is commonly
     * referred to as the frequency of the given term/phrase.
     */
    public int totalOccurrences() {
	int total_count = 0;

	for (Collection<Integer> positions : postings.values()) {
	    total_count += positions.size();
	}

	return total_count;
    }
    
}
