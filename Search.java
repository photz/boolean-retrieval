import java.util.Set;
import java.util.Arrays;
import java.util.Collection;


import java.util.HashSet;

public class Search {

    public static void main(String[] args) {

	String indexfile = "index-333";

	// build the index
	if (args.length == 2
	    && "-index".equals(args[0])) {

	    String corpusfile = args[1];

	    System.out.println("corps: " + corpusfile);

	    

	    Index index = new Index();

	    try {
		DocumentCollection docs = 
		    new DocumentCollection(corpusfile, index);

		index.save(indexfile);
	    }
	    catch (Exception e) {
		System.out.println("sth went wrong!");
	    }
	}
	// search for a phrase
	else if (args.length == 1) {
	    
	    String phrase = args[0];

	    try {
		Index index = new Index(indexfile);

		Collection<Integer> res = index.findPhrase(phrase);

		System.out.println("Matches: " + (String) String.valueOf(res.size()));

		for (Integer docid : res) {
		    System.out.println(String.valueOf(docid));
		}
		
	    }
	    catch (Exception e) {
		System.out.println("sth went wrong!" + e.getMessage());
	    }
	    
	}
	// boolean search
	else if (args.length > 1) {
	    
	    Set<String> termset = new HashSet<String>(Arrays.asList(args));

	    try {
		Index index = new Index(indexfile);

		Collection<Integer> res = index.andSearch(termset);

		System.out.println("Matches: " + (String) String.valueOf(res.size()));

		for (Integer docid : res) {
		    System.out.println(String.valueOf(docid));
		}
		
	    }
	    catch (Exception e) {
		System.out.println("sth went wrong!" + e.getMessage());
	    }


	}
	// print usage information
	else {
	    System.out.println("Usage: [-index <corpusfile>|<phrase>|<term1> <term2> ... <termn>]");
	}


    }

}
