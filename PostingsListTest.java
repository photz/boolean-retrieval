

//import static org.unit.Assert.assertEquals;
import org.junit.*;

import org.hamcrest.*;

import java.util.Set;


public class PostingsListTest {
    @Test
    public void firstTest() {
	PostingsList pl = new PostingsList();

	pl.addPosting(13, 42);

	pl.addPosting(42, 13);

	Assert.assertEquals(pl.getDocuments().size(), 2);
    }

    @Test
    public void mergeTest() {
	PostingsList l1 = new PostingsList();
	PostingsList l2 = new PostingsList();

	l1.addPosting(13, 42);
	l1.addPosting(42, 13);
	l1.addPosting(666, 666);

	l2.addPosting(1, 1);
	l2.addPosting(2, 2);
	l2.addPosting(3, 3);
	l2.addPosting(13, 123);
	l2.addPosting(123, 13);

	PostingsList intersected = l1.intersect(l2);

 	Assert.assertEquals(intersected.getDocuments().size(), 1);

	Assert.assertTrue(intersected.getDocuments().contains(13));

    }

    @Test
    public void mergeTestEmptyB() {
	PostingsList l1 = new PostingsList();
	PostingsList l2 = new PostingsList();
	
	l1.addPosting(1, 1);
	l1.addPosting(1, 2);
	l1.addPosting(1, 3);
	l1.addPosting(1, 4);
	l1.addPosting(2, 1);

	PostingsList intersected = l1.intersect(l2);

	Assert.assertEquals(intersected.getDocuments().size(), 0);	
	

    }

    @Test
    public void mergeTestEmptyA() {
	PostingsList l1 = new PostingsList();
	PostingsList l2 = new PostingsList();

	l2.addPosting(1, 1);
	l2.addPosting(2, 2);
	l2.addPosting(3, 3);
	l2.addPosting(4, 4);
	l2.addPosting(100, 100);

	PostingsList intersected = l1.intersect(l2);

	Assert.assertEquals(intersected.getDocuments().size(), 0);
	
    }

    /**
     * Tests whether a positional merge of two non-empty,
     * non-overlapping postings lists really does result
     * in an empty postings list.
     */
    @Test
    public void posMergeTest() {
	PostingsList l1 = new PostingsList();
	PostingsList l2 = new PostingsList();

	// l1 is assumed to be on the left hand side
	// while l2 is on the right hand side

	l1.addPosting(10, 1);
	l1.addPosting(20, 100);
	l1.addPosting(30, 10);
	l1.addPosting(40, 50);

	l2.addPosting(10, 3);
	l2.addPosting(15, 15);
	l2.addPosting(30, 9);
	l2.addPosting(45, 45);
	l2.addPosting(50, 50);

	PostingsList intersected = l1.posIntersect(l2);

	Assert.assertEquals(intersected.getDocuments().size(), 0);
    }

    /**
     * Tests whether a positional merge of two non-empty,
     * partially overlapping postings lists works as expected.
     */
    @Test
    public void posMergeTest2() {
	PostingsList l1 = new PostingsList();
	PostingsList l2 = new PostingsList();

	// l1 is assumed to be on the left hand side
	// while l2 is on the right hand side

	l1.addPosting(3, 1);
	l1.addPosting(6, 100);
	l1.addPosting(9, 10);
	l1.addPosting(12, 50);

	l2.addPosting(1, 3);
	l2.addPosting(3, 15);
	l2.addPosting(6, 101);
	l2.addPosting(9, 11);
	l2.addPosting(15, 14);

	PostingsList intersected = l1.posIntersect(l2);

	Assert.assertEquals(2, intersected.getDocuments().size());

	Assert.assertTrue(intersected.getDocuments().contains(6));

	Assert.assertTrue(intersected.getDocuments().contains(9));
    }


    /**
     * Tests whether the method posIntersect() retains multiple
     * ocurrences of the same phrase in the same document.
     */
    @Test
    public void posMergeTest3() {
	PostingsList l1 = new PostingsList();
	PostingsList l2 = new PostingsList();
	
	// phrase 1
	l1.addPosting(42, 13);
	l2.addPosting(42, 14);

	//phrase 2
	l1.addPosting(42, 77);
	l2.addPosting(42, 78);

	// phrase 3
	l1.addPosting(42, 99);
	l2.addPosting(42, 100);

	// junk
	l1.addPosting(42, 102);
	l2.addPosting(42, 104);


	PostingsList intersected = l1.posIntersect(l2);

	Assert.assertEquals(3, intersected.totalOccurrences());
    }

    /**
     * Tests whether a positional merge succeeds when
     * documents IDs are high and passed to the
     * added in no particular order.
     */
    @Test
    public void posMergeTest4() {
	PostingsList l1 = new PostingsList();
	PostingsList l2 = new PostingsList();

	// phrase 1
	l1.addPosting(128, 130);
	l2.addPosting(128, 131);

	PostingsList intersected = l1.posIntersect(l2);

	Assert.assertEquals(1, intersected.getDocuments().size());
    }

    
    /**
     * Tests whether the method posIntersect() retains multiple
     * ocurrences of the same phrase in the same document.
     */
    @Test
    public void posMergeTest5() {
	PostingsList l1 = new PostingsList();
	PostingsList l2 = new PostingsList();
	
	// phrase 1
	l1.addPosting(400, 111);
	l2.addPosting(400, 112);

	//phrase 2
	l1.addPosting(400, 300);
	l2.addPosting(400, 301);

	// phrase 3
	l1.addPosting(400, 303);
	l2.addPosting(400, 304);

	// phrase 4
	l1.addPosting(200, 1);
	l2.addPosting(200, 2);

	//phrase 5
	l1.addPosting(200, 3);
	l2.addPosting(200, 4);

	// phrase 6
	l1.addPosting(200, 79);
	l2.addPosting(200, 80);

	PostingsList intersected = l1.posIntersect(l2);

	Assert.assertEquals(6, intersected.totalOccurrences());
    }


    /**
     * Tests whether it's possible to merge arbitrarily
     * long chains of postings lists.
     */
    @Test
    public void posMergeChainTest() {
	PostingsList l1 = new PostingsList();
	PostingsList l2 = new PostingsList();
	PostingsList l3 = new PostingsList();


	// add postings such that merging l1, l2
	// and l3 (in this order) does not give
	// an empty postings list
	l1.addPosting(42, 5);
	l2.addPosting(42, 6);
	l3.addPosting(42, 7);

	// sprinkle a few random postings on top
	l1.addPosting(3, 9);
	l1.addPosting(9, 17);
	
	l2.addPosting(123, 123);

	l3.addPosting(111, 222);

	
	PostingsList intersected = l1.posIntersect(l2).posIntersect(l3);

	Assert.assertEquals(1, intersected.getDocuments().size());

	Assert.assertTrue(intersected.getDocuments().contains(42));
    }

    @Test
    public void posMergeChainTest2() {
	PostingsList l1 = new PostingsList();
	PostingsList l2 = new PostingsList();
	PostingsList l3 = new PostingsList();
	PostingsList l4 = new PostingsList();
	PostingsList l5 = new PostingsList();
	PostingsList l6 = new PostingsList();
	PostingsList l7 = new PostingsList();
	PostingsList l8 = new PostingsList();
	PostingsList l9 = new PostingsList();
	PostingsList l10 = new PostingsList();
	PostingsList l11 = new PostingsList();
	PostingsList l12 = new PostingsList();

	l1.addPosting(13, 7);
	l2.addPosting(13, 8);
	l3.addPosting(13, 9);
	l4.addPosting(13, 10);
	l5.addPosting(13, 11);
	l6.addPosting(13, 12);
	l7.addPosting(13, 13);
	l8.addPosting(13, 14);
	l9.addPosting(13, 15);
	l10.addPosting(13, 16);
	l11.addPosting(13, 17);
	l12.addPosting(13, 18);

	// create a few additional postings
	// to add some realism
	l1.addPosting(13, 100);
	l2.addPosting(5000, 100);
	l3.addPosting(30000, 200);
	
	PostingsList intersected = l1.posIntersect(l2)
	    .posIntersect(l3)
	    .posIntersect(l4)
	    .posIntersect(l5)
	    .posIntersect(l6)
	    .posIntersect(l7)
	    .posIntersect(l8)
	    .posIntersect(l9)
	    .posIntersect(l10)
	    .posIntersect(l11)
	    .posIntersect(l12);

	Assert.assertEquals(1, intersected.getDocuments().size());

	Assert.assertTrue(intersected.getDocuments().contains(13));
    }


}
