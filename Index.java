import java.util.Collection;

import java.util.Map;
import java.util.SortedMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import java.util.Arrays;

import java.nio.channels.FileChannel;

import java.io.*;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.ArrayList;


public class Index {
    private Map<String,PostingsList> postingsLists;

    private int lastQueryTotalCount = 0;

    private Map<String, Integer> positions;

    public String postingsListsFile = "tmp-plists";

    public String positionsTableFile = "tmp-positions";


    /**
     * Attempts to load an existing index from a file.
     */
    public Index(String positions_table_file,
		 String postings_lists_file) throws Exception {
	
	postingsLists = new HashMap<String, PostingsList>();

	postingsListsFile = postings_lists_file;
	positionsTableFile = positions_table_file;

	loadPositionsTable();

	
    }


    /**
     * Creates a new Index object from scratch without.
     */
    public Index() {
	postingsLists = new HashMap<String, PostingsList>();
	
	positions = new HashMap<String, Integer>();
    }

    public int getLastQueryTotalCount() {
	return lastQueryTotalCount;
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

	    //assert true == postingsLists.containsKey(token);

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
    public Set<Integer>andSearch(Collection<String> terms)
	throws Exception {

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

		// maybe the postings list for this term needs
		// to be read from disk first
		if (positions.containsKey(term)) {
		    // load into memory

		    loadPostingsList(term);
		}
		else {
		    // this term does not exist
		    return new HashSet<Integer>();
		}
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

	lastQueryTotalCount = merged.getDocuments().size();

	return merged.getDocuments();
    }

    /**
     * Attempts to find an occurrence of the given
     * phrase in the document collection.
     */
    public Collection<Integer> findPhrase(String phrase) 
	throws Exception {

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

		if (positions.containsKey(term)) {
		    // load into memory

		    loadPostingsList(term);
		}
		else {
		    return new HashSet<Integer>();
		}
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

	lastQueryTotalCount = merged.totalOccurrences();

	return merged.getDocuments();
    }

    /**
     * Store the index permanently on disk.
     */
    // public void saveold(String filename) throws Exception {
    // 	ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(filename)));

    // 	oos.writeObject(postingsLists);
    // }

    public void save(String positions_table_file,
		     String postings_lists_file) throws Exception {

	postingsListsFile = postings_lists_file;
	positionsTableFile = positions_table_file;
	
	postingsListsToFile();

	positionsTableToFile();

    }

    public void loadPositionsTable() 
	throws Exception {
	
	assert null != positionsTableFile;

	File file = new File(positionsTableFile);

	FileInputStream fis = new FileInputStream(file);

	ObjectInputStream ois = new ObjectInputStream(fis);

	positions = (HashMap<String, Integer>) ois.readObject();

	ois.close();

	fis.close();
    }

    /**
     * Writes the positions table to the hard disk.
     */
    public void positionsTableToFile()
	throws IOException {

	File file = new File(positionsTableFile);

	FileOutputStream fos = new FileOutputStream(file);

	ObjectOutputStream oos = new ObjectOutputStream(fos);

	oos.writeObject((Object) positions);

	oos.close();

	fos.close();
    }


    /**
     * Writes the postings lists to the hard disk.
     * @arg filename the postings lists will be written \
     * to this file
     */
    public void postingsListsToFile() throws IOException {
	assert postingsLists != null;

	positions = new HashMap<String, Integer>();

	File file = new File(postingsListsFile);

	FileOutputStream fos = new FileOutputStream(file);

	FileChannel ch = fos.getChannel();

	for (Map.Entry<String, PostingsList> entry : postingsLists.entrySet()) {

	    // get the current position in the file
	    long pos = ch.position();

	    // get the postings list as a byte array
	    byte bytearr[] = objectToByteArray(entry.getValue());

	    fos.write(bytearr);

	    // save the position of this postings list in the file
	    positions.put(entry.getKey(), (int) pos);

	    

	}
    }

    /**
     * Reads the postings list for the term _term_ from the \
     * hard disk.
     */
    public PostingsList loadPostingsList(String term)
	throws Exception {

	assert term != null;
	assert postingsListsFile != null;
	assert positions != null;
	assert postingsLists != null;

	if (positions == null) {
	    throw new Exception("The positions table is not available");
	}

	if (!positions.containsKey(term)) {
	    throw new Exception("The positions table has no entry for a term " + term);
	}
	
	FileInputStream fis = new FileInputStream(new File(postingsListsFile));

	fis.skip((long) positions.get(term));

	ObjectInputStream oos = new ObjectInputStream(fis);

	PostingsList pl = (PostingsList) oos.readObject();

	postingsLists.put(term, pl);

	oos.close();

	fis.close();

	return pl;
    }

    private static byte[] objectToByteArray(Object obj) {
	assert obj != null;

	ByteArrayOutputStream bos = new ByteArrayOutputStream();

	ObjectOutput out = null;

	try {
	    out = new ObjectOutputStream(bos);
	    out.writeObject(obj);
	    byte[] bytearr = bos.toByteArray();
	    return bytearr;
	}
	catch (IOException ex) {
	    return null;
	}
	finally {
	    try {
		if (out != null) {
		    out.close();
		}
	    }
	    catch (IOException ex) {
		// ignore close exception
	    }
	    try {
		bos.close();
	    }
	    catch (IOException ex) {
		// ignore close exception
	    }
	}

    }

    private static List<String> getTokens(String text) {
	assert text != null;

	text = text.toLowerCase();

	String tokens_arr [] = text.split(" ");

	List<String> tokens_coll = new ArrayList<String>(Arrays.asList(tokens_arr));

	return tokens_coll;
    }
}

