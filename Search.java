

public class Search {

    public static void main(String[] args) {

	String indexfile = "index-333";

	// build the index
	if (args.length == 2
	    && "-index".equals(args[0])) {

	    String corpusfile = args[1];

	    System.out.println("corps: " + corpusfile);

	    Index index = new Index();

	    DocumentCollection docs = 
		new DocumentCollection(corpusfile, index);

	    index.save(indexfile);
	}
	// search for a phrase
	else if (args.length == 1) {
	    
	    String phrase = args[0];

	    Index index = new Index(indexfile);

	}
	// boolean search
	else if (args.length > 1) {
	    
	    

	}


    }

}
