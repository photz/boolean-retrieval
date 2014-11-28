import org.junit.*;

import org.hamcrest.*;

import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


public class IndexTest {
    @Test
    public void simpleAndSearchTest() {
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
    public void simpleAndSearchTest2() {
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
    public void simpleAndSearchTest3() {
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
    public void simpleAndSearchTest4() {
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
    public void simpleAndSearchTest5() {
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
    public void tokenizationTest1() {
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
    public void loadingStoringTest1() {
	// create a new index from scratch
	Index index = new Index();

	// fill it with a few toy documents
	index.addDocument(1, "The python ate the ape");
	index.addDocument(2, "The ate ape the apple");
	index.addDocument(3, "The apple was eaten by the ape");
	index.addDocument(4, "Did the python eat the ape?");

	// try to store the index on disk
	String filename = "test-store-index-344342";
	index.save(filename);

	// destroy the index object
	index = null;

	// try to read the object from disk
	index = new Index(filename);

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
    public void phraseSearchTest1() {
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
    public void phraseSearchTest2() {
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
    public void phraseSearchTest3() {
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
    public void phraseSearchTest4() {
		Index index = new Index();

	index.addDocument(1, "Genetic analysis of mutations affecting ribonuclease II in Escherichia coli. XXX Exonuclease activity in an Escherichia coli K12 mutant S296 is less than 1% of that in the wild type strain (Nikolaev et al., 1976). Another mutant N464 has thermolabile ribonuclease II (Castles and Singer, 1968; Kuwano et al., 1969). Genetic analysis of these mutants by Hfr conjugation and P1 transduction indicates that the structural gene (rnb) for ribonuclease II is located near the pyrF gene (28 min on the E. coli genetic map of Bachmann, Low and Taylor (1976)), and the most probable gene order is tyrT-trp-pyrF-rnb.");
	index.addDocument(2, "[Hyperostosis on the alveolar process margin] XXX Defects of the bone margin requiring ostectomy and osteoplasty include hyperostotic processes, formations which, while recalling palatine and mandibular tori, have their own nosological slot. Hyperostosis is characterized by thickening of the cervical margin and is linked by a narrow isthmus to the underlying bone plane; it occurs with greatest frequency in the vestibular region. The personal case, in a man of 52, presented two sausage-shaped protuberances located apically at the alveolar margin in the two left arches. Their removal presented no problem of surgical technique as the hyperostosis had no close links with the underlying bone planes. Histological examination of the fragments showed that the hyperostotic tissue consisted of fascicular bone with an intima vascular component. Two years after the operation, the patient presents no signs of relapse and would appear to be completely cured.");

	Assert.assertEquals("phrase search should yield a result",
			    1,
			    index.findPhrase("Two years after").size());
    }

    

    /**
     * Tests whether an empty phrase is handled correctly, i.e.
     * yields no matches and doesn't cause an exception.
     */
    @Test
    public void phraseSearchTestEmptyPhrase() {
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


    // @Test
    // public void getTokensTest() {
    // 	String test = "You sold me and I sold you";

    // 	Index index = new Index();

    // 	List<String> tokenlist = index.getTokens(test);

    // 	Assert.assertEquals(7, tokenlist.size());
    // }
}
