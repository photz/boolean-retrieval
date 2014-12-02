benchmark_path = benchmark.txt
jar_path = search.jar
javac_flags = -source 1.6 -target 1.6 -g
classpath = .:junit4.jar:hamcrest-core-1.3.jar

jar:	index documentcollection postingslist search
	jar cfe $(jar_path) Search Search.class Index.class PostingsList.class DocumentCollection.class


search: postingslist documentcollection index
	javac $(javac_flags) -cp $(classpath) Search.java

postingslist:
	javac $(javac_flags) -cp $(classpath) PostingsList.java

documentcollection: postingslist index
	javac $(javac_flags)  -cp $(classpath) DocumentCollection.java

index: postingslist
	javac $(javac_flags)  -cp $(classpath) Index.java


# tests

postingslisttest: postingslist
	javac $(javac_flags)  -cp $(classpath) PostingsListTest.java

indextest: index
	javac $(javac_flags)  -cp $(classpath) IndexTest.java

documentcollectiontest: documentcollection
	javac $(javac_flags)  -cp $(classpath) DocumentCollectionTest.java

tests: postingslisttest indextest documentcollectiontest
	java  -cp $(classpath) org.junit.runner.JUnitCore IndexTest PostingsListTest DocumentCollectionTest

benchmark: jar
	python benchmark.py > $(benchmark_path)

buildindex: jar
	java -jar search.jar -index test_08n0147.xml

pack: jar benchmark buildindex
	tar -czf gruppe5_aufgabe2.tar.gz $(benchmark_path) $(jar_path) Index.java PostingsList.java DocumentCollection.java Search.java /usr/share/java/stax.jar


pdf:
	pdflatex --shell-escape presentation.tex
