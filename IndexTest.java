import org.junit.*;

import org.hamcrest.*;

import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


public class IndexTest {
    @Test
    public void simpleAndSearchTest() throws Exception {
	// instantiate an empty index
	Index i = new Index();

	i.addDocument(1, "Hier j'ai rencontre une belle femme");

	i.addDocument(2, "Je suis tombe amoureux d'elle");

	i.addDocument(3, "Mais elle ne m'aime pas");

	i.addDocument(4, "Elle ne m'a pas vu");

	i.addDocument(5, "Et moi je ne l'a pas vue non plus");

	String query[] = {"amoureux", "suis"};

	Set<String> queryset = new HashSet<String>(Arrays.asList(query));

	Collection<Integer> res = i.andSearch(queryset);

	Assert.assertEquals(1, res.size());

	Assert.assertThat(res, CoreMatchers.hasItem(CoreMatchers.allOf(CoreMatchers.equalTo(2))));
    }

    @Test
    public void simpleAndSearchTest2() throws Exception {
	Index i = new Index();

	i.addDocument(1, "A fairly boring document");
	i.addDocument(2, "Another quite uninteresting document");

	// this time we construct a query that should not
	// yield any matches
	String query[] = {"words", "that", "do", "not", "occur"};

	Set<String> queryset = new HashSet<String>(Arrays.asList(query));

	Collection<Integer> res = i.andSearch(queryset);

	Assert.assertEquals(0, res.size());
    }


    /**
     * Tests whether an Index object that no documents
     * have been added to functions normally
     */
    @Test
    public void simpleAndSearchTest3() throws Exception{
	Index i = new Index();

	String query[] = {"arbitrary", "query"};

	Set<String> queryset = new HashSet<String>(Arrays.asList(query));

	Collection<Integer> res = i.andSearch(queryset);

	Assert.assertEquals("There shouldn't be any documents matching this query",
			    0,
			    res.size());
    }
    
    /**
     * Tests whether a query that should yield matching
     * documents really does so.  In so doing it also tests
     * whether the tokenizer works as expected.
     */
    @Test
    public void simpleAndSearchTest4() throws Exception {
	Index index = new Index();

	index.addDocument(1,
			  "Saint Augustine! well hast thou said,");
	index.addDocument(2,
			  "That of our vices we can frame");
	index.addDocument(3,
			  "A ladder, if we will but tread");
	index.addDocument(4,
			  "Beneath our feet each deed of shame!");

	String query[] = {"augustine!", "said,", "well", "hast"};

	Set<String> queryset = new HashSet<String>(Arrays.asList(query));

	Collection<Integer> res = index.andSearch(queryset);

	Assert.assertEquals("There should be a document",
			    1, res.size());
    }

    /**
     * Performs a somewhat realistic boolean search using
     * samples from the corpus.
     */
    @Test
    public void simpleAndSearchTest5() throws Exception {
	Index index = new Index();

	index.addDocument(1, "Genetic analysis of mutations affecting ribonuclease II in Escherichia coli. XXX Exonuclease activity in an Escherichia coli K12 mutant S296 is less than 1% of that in the wild type strain (Nikolaev et al., 1976). Another mutant N464 has thermolabile ribonuclease II (Castles and Singer, 1968; Kuwano et al., 1969). Genetic analysis of these mutants by Hfr conjugation and P1 transduction indicates that the structural gene (rnb) for ribonuclease II is located near the pyrF gene (28 min on the E. coli genetic map of Bachmann, Low and Taylor (1976)), and the most probable gene order is tyrT-trp-pyrF-rnb.");
	index.addDocument(2, "[Hyperostosis on the alveolar process margin] XXX Defects of the bone margin requiring ostectomy and osteoplasty include hyperostotic processes, formations which, while recalling palatine and mandibular tori, have their own nosological slot. Hyperostosis is characterized by thickening of the cervical margin and is linked by a narrow isthmus to the underlying bone plane; it occurs with greatest frequency in the vestibular region. The personal case, in a man of 52, presented two sausage-shaped protuberances located apically at the alveolar margin in the two left arches. Their removal presented no problem of surgical technique as the hyperostosis had no close links with the underlying bone planes. Histological examination of the fragments showed that the hyperostotic tissue consisted of fascicular bone with an intima vascular component. Two years after the operation, the patient presents no signs of relapse and would appear to be completely cured.");

	
	String query[] = {
	    "analysis",
	    "Genetic",
	    "of",
	    "mutations",
	    "affecting",
	};

	Set<String> queryset = new HashSet<String>(Arrays.asList(query));

	Assert.assertEquals("boolean search should yield one result",
			    1, index.andSearch(queryset).size());
	
    }

    /**
     * Tests whether documents and  queries are correctly tokenized.
     */
    @Test
    public void tokenizationTest1() throws Exception {
		Index index = new Index();

	index.addDocument(1,
			  "a highly interesting document");
	index.addDocument(2,
			  "a$$single$$token ((another!@#token]]");
	index.addDocument(3,
			  "another tremendously interesting doc");
	index.addDocument(4,
			  "yet another brilliant document");

	String query[] = {
	    "a$$single$$TOKEN",
	    "((another!@#token]]",
	};

	Set<String> queryset = new HashSet<String>(Arrays.asList(query));

	Collection<Integer> res = index.andSearch(queryset);

	Assert.assertEquals("There should be a document",
			    1, res.size());

	Assert.assertThat(res, CoreMatchers.hasItem(CoreMatchers.allOf(CoreMatchers.equalTo(2))));

    }

    /**
     * Tests whether a non-empty index can be stored on disk
     * and loaded.
     */
    @Test
    public void loadingStoringTest1() throws Exception {
	// create a new index from scratch
	Index index = new Index();

	// fill it with a few toy documents
	index.addDocument(1, "The python ate the ape");
	index.addDocument(2, "The ate ape the apple");
	index.addDocument(3, "The apple was eaten by the ape");
	index.addDocument(4, "Did the python eat the ape?");

	// try to store the index on disk
	String filename1 = "test-store-index-1";
	String filename2 = "test-store-index-2";
	index.save(filename1, filename2);

	// destroy the index object
	index = null;

	// try to read the object from disk
	index = new Index(filename1, filename2);

	// make sure the index has been correctly restored
	// by performing a boolean search
	String query[] = {
	    "ape",
	    "apple",
	    "eaten",
	};

	Set<String> queryset = new HashSet<String>(Arrays.asList(query));

	Collection<Integer> res = index.andSearch(queryset);

	Assert.assertEquals("The index has not been correctly restored",
			    1, res.size());

	Assert.assertThat(res, CoreMatchers.hasItem(CoreMatchers.allOf(CoreMatchers.equalTo(3))));
    }


    /**
     * Tests whether a phrase whose constituents
     * do not occur in any of the indexed documents
     * correctly yields no matches.
     */
    @Test
    public void phraseSearchTest1() throws Exception {

	Index index = new Index();

	index.addDocument(1, "This is the first document");
	index.addDocument(2, "closely followd by the 2nd document");
	index.addDocument(3, "and a third one to follow");

	String query = "this one doesn't occur in any docs";

	Collection<Integer> matches = index.findPhrase(query);

	Assert.assertEquals(0, matches.size());
    }

    /**
     * Tests whether a phrase that occurs in a document is found.
     */
    @Test
    public void phraseSearchTest2()
	throws Exception {

	Index index = new Index();

	index.addDocument(1, "This is the first document");
	index.addDocument(2, "closely followd by the 2nd document");
	index.addDocument(3, "and a third one to follow");
	index.addDocument(4, "and, amazingly, a fourth one, too!");

	// a phrase that occurs in the document with document ID 3
	String query = "a third one to follow";

	Collection<Integer> matches = index.findPhrase(query);

	Assert.assertEquals(1, matches.size());

	Collection<Integer> res = index.findPhrase(query);

	Assert.assertThat(res, CoreMatchers.hasItem(CoreMatchers.allOf(CoreMatchers.equalTo(3))));
    }

    /**
     * A slightly more complex test than phraseSearchTest2
     */
    @Test
    public void phraseSearchTest3() 
	throws Exception {

	Index index = new Index();

	index.addDocument(1, "This is the first document");
	index.addDocument(2, "closely followd by the 2nd document");
	index.addDocument(3, "and a third one to follow");
	index.addDocument(4, "and, amazingly, a fourth one, too!");
	index.addDocument(5, "!@# [[[a]]] %%SINGLE%% phrase...");

	// a phrase that occurs in the document with document ID 3
	String query = "[[[a]]] %%SINGLE%% phrase...";

	Collection<Integer> matches = index.findPhrase(query);

	Assert.assertEquals(1, matches.size());

	Collection<Integer> res = index.findPhrase(query);

	Assert.assertThat("Wrong document, should be doc 5",
			  res, 
			  CoreMatchers.hasItem(CoreMatchers.allOf(CoreMatchers.equalTo(5))));
    }

    @Test
    public void phraseSearchTest4() throws Exception {

		Index index = new Index();

	index.addDocument(1, "Genetic analysis of mutations affecting ribonuclease II in Escherichia coli. XXX Exonuclease activity in an Escherichia coli K12 mutant S296 is less than 1% of that in the wild type strain (Nikolaev et al., 1976). Another mutant N464 has thermolabile ribonuclease II (Castles and Singer, 1968; Kuwano et al., 1969). Genetic analysis of these mutants by Hfr conjugation and P1 transduction indicates that the structural gene (rnb) for ribonuclease II is located near the pyrF gene (28 min on the E. coli genetic map of Bachmann, Low and Taylor (1976)), and the most probable gene order is tyrT-trp-pyrF-rnb.");
	index.addDocument(2, "[Hyperostosis on the alveolar process margin] XXX Defects of the bone margin requiring ostectomy and osteoplasty include hyperostotic processes, formations which, while recalling palatine and mandibular tori, have their own nosological slot. Hyperostosis is characterized by thickening of the cervical margin and is linked by a narrow isthmus to the underlying bone plane; it occurs with greatest frequency in the vestibular region. The personal case, in a man of 52, presented two sausage-shaped protuberances located apically at the alveolar margin in the two left arches. Their removal presented no problem of surgical technique as the hyperostosis had no close links with the underlying bone planes. Histological examination of the fragments showed that the hyperostotic tissue consisted of fascicular bone with an intima vascular component. Two years after the operation, the patient presents no signs of relapse and would appear to be completely cured.");

	Assert.assertEquals("phrase search should yield a result",
			    1,
			    index.findPhrase("Two years after").size());
    }


    @Test
    public void phraseSearchTest5() 
	throws Exception {

	Index index = new Index();

	index.addDocument(1, "for testing purposes");
	index.addDocument(128, "Prosthetic rehabilitation of unilateral distal edentulism (Kennedy class 2) and its combination with intercalated absence on the other side (class 2, modification 1) is discussed. Success obtained in a series of 29 cases followed for 9 yr using a removable prosthesis is described. This solved the difficult problems involved by employing metal wires to distribute the forces axially on the abutment teeth and over the unilateral edentulous ridge, thus creating a mixed dental and mucosal support.");
	// index.addDocument(2, "some noise");
	// index.addDocument(3, "some more noise");
	// index.addDocument(4, "success obtained");
	// index.addDocument(5, "(kennedy class 2");

	Assert.assertEquals(1, index.findPhrase("(kennedy class 2)").size());
    }
    

    /**
     * Tests whether an empty phrase is handled correctly, i.e.
     * yields no matches and doesn't cause an exception.
     */
    @Test
    public void phraseSearchTestEmptyPhrase() 
	throws Exception {

	Index index = new Index();

	index.addDocument(1, "This is the first document");
	index.addDocument(2, "closely followd by the 2nd document");
	index.addDocument(3, "and a third one to follow");
	index.addDocument(4, "and, amazingly, a fourth one, too!");
	index.addDocument(5, "!@# [[[a]]] %%SINGLE%% phrase...");

	// a phrase that occurs in the document with document ID 3
	String query = "";

	Collection<Integer> matches = index.findPhrase(query);

	Assert.assertEquals(0, matches.size());

	Collection<Integer> res = index.findPhrase(query);

    }

    
    @Test
    public void loadStoreTest1()
	throws Exception {

	// create a new Index object from scratch
	Index index = new Index();

	// add a few documents
	index.addDocument(1, "term1 term2 term3");
	index.addDocument(2, "term2 term3 term4");
	index.addDocument(3, "term3 term4 term5");
	index.addDocument(4, "term2 term3 term4 term5");
	index.addDocument(5, "term2 term3 term4 term6");

	// write the index to disk
	String filename1 = "tmp-1";
	String filename2 = "tmp-2";
	index.save(filename1, filename2);

	// try to read it from disk
	Index index2 = new Index(filename1, filename2);
	
	//
	// make sure it works
	//

	String query[] = {"term2", "term3", "term4", "term5"};

	Set<String> queryset = new HashSet<String>(Arrays.asList(query));

	Assert.assertEquals(1, index2.andSearch(queryset).size());


    }


    @Test
    public void loadStoreTest2() throws Exception {
	// create an empty index
	Index index = new Index();

	// add a few documents
	index.addDocument(1, "term1 term2 term3");
	index.addDocument(2, "term1 term2 term3");
	index.addDocument(3, "term2 term2 term2 term3");
	index.addDocument(4, "term1 term2 term3 term4 term5");

	// write the current index to disk
	String filename1 = "tmp-index-1";
	String filename2 = "tmp-index-2";
	index.save(filename1, filename2);

	// read the index from disk
	Index index2 = new Index(filename1, filename2);

	
	String query[] = {"term3", "term1", "term2"};

	Set<String> queryset = new HashSet<String>(Arrays.asList(query));

	Assert.assertEquals(3, index2.andSearch(queryset).size());

    }

    @Test
    public void loadStoreTest3() throws Exception {
	// create an empty index
	Index index = new Index();

	// add a few documents
	index.addDocument(1, "term1 term2 term3");
	index.addDocument(2, "term1 term2 term3");
	index.addDocument(3, "term2 term2 term2 term3");
	index.addDocument(4, "term1 term2 term3 term4 term5");

	// write the current index to disk
	String filename1 = "tmp-index-1";
	String filename2 = "tmp-index-2";
	index.save(filename1, filename2);

	// read the index from disk
	Index index2 = new Index(filename1, filename2);

	
	String query[] = {"term3", "unknownTerm1", "unknownTerm2"};

	Set<String> queryset = new HashSet<String>(Arrays.asList(query));

	Assert.assertEquals(0, index2.andSearch(queryset).size());

    }


    @Test
    public void loadStoreTest4() throws Exception {
	// create an empty index
	Index index = new Index();

	// add a few documents
	index.addDocument(1, "term1 term2 term3");
	index.addDocument(2, "term1 term2 term3");
	index.addDocument(3, "term2 term2 term2 term3");
	index.addDocument(4, "term1 term2 term3 term4 term5");

	// write the current index to disk
	String filename1 = "tmp-index-1";
	String filename2 = "tmp-index-2";
	index.save(filename1, filename2);

	// read the index from disk
	Index index2 = new Index(filename1, filename2);
	
	String query = "term3 term4";

	Assert.assertEquals(1, index2.findPhrase(query).size());
    }

    @Test
    public void loadStoreTest5() throws Exception {
	// create an empty index
	Index index = new Index();

	// add a few documents
	index.addDocument(1, "term1 term2 term3");
	index.addDocument(2, "term1 term2 term3");
	index.addDocument(3, "term2 term2 term2 term3");
	index.addDocument(4, "term1 term2 term3 term4 term5");

	// write the current index to disk
	String filename1 = "tmp-index-1";
	String filename2 = "tmp-index-2";
	index.save(filename1, filename2);

	// read the index from disk
	Index index2 = new Index(filename1, filename2);
	
	String query = "term3 term4";

	Assert.assertEquals(1, index2.findPhrase(query).size());
    }




    @Test
    public void loadStoreTest6() throws Exception {
	// create an empty index
	Index index = new Index();

	// add a few documents
	index.addDocument(1, "term1 term2 term3 term4 term5");
	index.addDocument(2, "term1 term2 term3 term4 term6");
	index.addDocument(3, "term1 term2 term3 term4 term7");
	index.addDocument(4, "term1 term2 term3 term4 term8");
	index.addDocument(5, "term1 term2 term3 term4 term9");
	index.addDocument(6, "term1 term2 term3 term4 term10");
	index.addDocument(7, "term1 term2 term3 term4 term11");
	index.addDocument(8, "term1 term2 term3 term4 term12");

	// write the current index to disk
	String filename1 = "tmp-index-1";
	String filename2 = "tmp-index-2";
	index.save(filename1, filename2);

	// read the index from disk
	Index index2 = new Index(filename1, filename2);
	
	String query = "term1 term2 term3 term4 term10";

	Assert.assertEquals(1, index2.findPhrase(query).size());
    }




    // @Test
    // public void loadPostingsListTest1() throws Exception {

    // 	Index index = new Index();

    // 	index.addDocument(1, "term1 term2");
    // 	index.addDocument(2, "term2 term3");
    // 	index.addDocument(3, "term3 term4 term5");
    // 	index.addDocument(4, "term4 term5 term6");
    // 	index.addDocument(5, "term4 term4 term4 term5 term6 term7");

    // 	String filename = "tmp-postingslists";

    // 	index.postingsListsToFile(filename);

    // 	PostingsList list_term3 = index.loadPostingsList(filename,
    // 							 "term3");

    // 	Assert.assertEquals(2, list_term3.totalOccurrences());

    // 	PostingsList list_term4 = index.loadPostingsList(filename,
    // 							 "term4");

    // 	Assert.assertEquals(5, list_term4.totalOccurrences());

    // }

    // @Test
    // public void loadPositionsTableTest() throws Exception {
    // 	Index index = new Index();

    // 	String filename = "tmp-postable";

    // 	// add a few documents
	

    // 	// write the positions table to the disk
    // 	index.positionsTableToFile(filename);

    // 	// read the positions table into main memory
    // 	index.loadPositionsTable(filename);

	
    // }

    // @Test
    // public void getTokensTest() {
    // 	String test = "You sold me and I sold you";

    // 	Index index = new Index();

    // 	List<String> tokenlist = index.getTokens(test);

    // 	Assert.assertEquals(7, tokenlist.size());
    // }
}
