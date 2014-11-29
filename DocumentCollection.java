import javax.xml.stream.*;

public class DocumentCollection {

    /**
     * The number of documents in this document collection
     */
    private int ndocs;

    public DocumentCollection(String filename,
			      Index index) throws Exception{
	
	XMLInputFactory factory = XMLInputFactory.newInstance();
	XMLStreamReader reader = 
	    factory.createXMLStreamReader(
            ClassLoader.getSystemResourceAsStream(filename));

	ndocs = 0;
	
	int pmid = -1;
	String article_title = "";
	String abstract_text = "";
	String tag_contents = "";
	boolean get_text = true;

	// as long as we've not yet hit the end of the document
	// keep pulling events
	while (reader.hasNext()) {
	    
	    int event = reader.next();

	    switch (event) {

	    case XMLStreamConstants.START_ELEMENT:
		// an opening XML tag has been found
		{
		    String tagname = reader.getLocalName();
		
		    if ("MedlineCitation".equals(tagname)) {
			pmid = -1;
			article_title = "";
			abstract_text = "";
		    } else if (tagname.equals("PMID")
			       || tagname.equals("ArticleTitle")
			       || tagname.equals("AbstractText")) {
		    
			get_text = true;
		    }
		}
		break;

	    case XMLStreamConstants.CHARACTERS:
		// a chunk of text has been encountered
		if (get_text) {
		    tag_contents = reader.getText().trim();
		    get_text = false;
		}
		break;


	    case XMLStreamConstants.END_ELEMENT:
		// a closing XML tag has been found
		{
		    String tagname = reader.getLocalName();

		    if ("PMID".equals(tagname)) {
			pmid = Integer.parseInt(tag_contents);
		    }
		    else if ("ArticleTitle".equals(tagname)) {
			article_title = tag_contents;
		    }
		    else if ("AbstractText".equals(tagname)) {
			abstract_text = tag_contents;
		    }
		    else if ("MedlineCitation".equals(tagname)) {
			assert pmid != -1;
			assert !article_title.isEmpty();

			ndocs++;

			// we insert an arbitrary uncommon
			// delimiter between the title and
			// the abstract so there can't be a 
			// phrase search that extends from
			// the title to the abstract
			index.addDocument(pmid,
					  article_title + " XXX " + abstract_text);
		    }
		}
		break;
	    }
	}
    }

    public int numberOfDocs() {
	return ndocs;
    }	
}
