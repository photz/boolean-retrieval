import org.junit.*;

import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;

public class DocumentCollectionTest {
    @Test
    public void firstTest() throws Exception {
	Index index = new Index();

	DocumentCollection docs = new DocumentCollection("test_08n0147.xml", index);

	//Assert.assertEquals(30000, docs.numberOfDocs());

	String query[] = {
	    "analysis",
	    "Genetic",
	    "of",
	    "mutations",
	    "affecting",
	};

	//Set<String> queryset = new HashSet<String>(Arrays.asList(query));

	//Assert.assertEquals("boolean search should yield one result",
	//1, index.andSearch(queryset).size());

	Assert.assertEquals(1, index.findPhrase("Genetic analysis of mutations affecting").size());
    }
}
