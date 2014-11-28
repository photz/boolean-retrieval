import org.junit.*;

import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;

public class DocumentCollectionTest {
    Index index;
    DocumentCollection docs;

    public DocumentCollectionTest() throws Exception {
	index = new Index();

	docs = new DocumentCollection("test_08n0147.xml", index);

    }


    @Test
    public void andSearchTest() throws Exception {
	String query[] = {
	    "genetic",
	    "analysis",
	    "mutations",
	};

	Set<String> queryset = new HashSet<String>(Arrays.asList(query));

	Assert.assertEquals("boolean search should yield one result",
	6, index.andSearch(queryset).size());
    }


    @Test
    public void findPhraseTest() throws Exception {



	Assert.assertEquals(1, index.findPhrase("Genetic analysis of").size());
    }
}
