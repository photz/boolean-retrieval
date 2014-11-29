import org.junit.*;

import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;

public class DocumentCollectionTest {
    Index index;
    DocumentCollection docs;

    public DocumentCollectionTest() throws Exception {
	//index = new Index("big-index-4434231231321");
	index = new Index();

	docs = new DocumentCollection("test_08n0147.xml", index);

    }

    @Test
    public void doNothing() {}

    //@Test
    public void andSearchTest() throws Exception {
	String query[] = {
	    "genetic",
	    "analysis",
	    "mutations",
	};

	Set<String> queryset = new HashSet<String>(Arrays.asList(query));

	Assert.assertEquals("boolean search should yield one result",
	7, index.andSearch(queryset).size());
    }


    //@Test
    public void findPhraseTest1() throws Exception {



	Assert.assertEquals(3, index.findPhrase("Genetic analysis of").size());
    }

    @Test
    public void findPhraseTest2() throws Exception {
	Assert.assertEquals(1, index.findPhrase("amine radical").size());
    }

    @Test
    public void findPhraseTest3() throws Exception {
	Assert.assertEquals(1, index.findPhrase("water turnover rises").size());
    }

    @Test
    public void findPhraseTest4() throws Exception {
	Assert.assertEquals(1, index.findPhrase("its activity when linked with").size());
    }

    @Test
    public void findPhraseTest5() throws Exception {
	Assert.assertEquals(1, index.findPhrase("its activity when linked with").size());
    }

    @Test
    public void findPhraseTest6() throws Exception {
	Assert.assertEquals(1, index.findPhrase("continuing education and general internal medicine.").size());
    }

    //@Test
    public void saveIndexTest() throws Exception {
	index.save("big-index-4434231231321");
    }
}
