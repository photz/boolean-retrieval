classpath = .:junit4.jar:hamcrest-core-1.3.jar

jar:	index documentcollection postingslist search
	jar cfe search.jar Search Search.class Index.class PostingsList.class DocumentCollection.class


search: postingslist documentcollection index
	javac -cp $(classpath) Search.java

postingslist:
	javac -cp $(classpath) PostingsList.java

documentcollection: postingslist index
	javac -cp $(classpath) DocumentCollection.java

index: postingslist
	javac -cp $(classpath) Index.java


# tests

postingslisttest: postingslist
	javac -cp $(classpath) PostingsListTest.java

indextest: index
	javac -cp $(classpath) IndexTest.java

documentcollectiontest: documentcollection
	javac -cp $(classpath) DocumentCollectionTest.java

tests: postingslisttest indextest documentcollectiontest
	java -cp $(classpath) org.junit.runner.JUnitCore IndexTest PostingsListTest DocumentCollectionTest

