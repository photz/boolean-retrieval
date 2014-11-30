benchmark_path = benchmark.txt
jar_path = search.jar
classpath = .:junit4.jar:hamcrest-core-1.3.jar

jar:	index documentcollection postingslist search
	jar cfe $(jar_path) Search Search.class Index.class PostingsList.class DocumentCollection.class


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

benchmark: jar
	python benchmark.py > $(benchmark_path)

buildindex: jar
	java -jar search.jar -index test_08n0147.xml

pack: jar benchmark buildindex
	tar -czf gruppe5_aufgabe2.tar.gz $(benchmark_path) $(jar_path) Index.java PostingsList.java DocumentCollection.java Search.java


