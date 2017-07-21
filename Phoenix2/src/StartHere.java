import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

public class StartHere {
	public static void main(String[] args) throws IOException, ParseException {
		// 0. Specify the analyzer for tokenizing text.
		// The same analyzer should be used for indexing and searching
		StandardAnalyzer analyzer = new StandardAnalyzer();

		// 1. create the index
		Directory index = new RAMDirectory();

		IndexWriterConfig config = new IndexWriterConfig(analyzer);

		IndexWriter w = new IndexWriter(index, config);
		addDoc(w, "kariert", "dok1");
		addDoc(w, "Hemd", "dok1");
		addDoc(w, "kariert", "dok2");
		addDoc(w, "Hose", "dok2");
		addDoc(w, "kariert", "dok3");
		addDoc(w, "Hemd", "dok3");
		addDoc(w, "gelb", "dok4");
		addDoc(w, "kariert", "dok4");
		w.close();

		// 2. query
		String querystr = "kariert";
		Query q = new QueryParser("title", analyzer).parse(querystr);
		search(index, q, analyzer);

	}

	private static void search(Directory index, Query q, StandardAnalyzer analyzer) throws IOException, ParseException {

		// 3. search
		int hitsPerPage = 10;
		IndexReader reader = DirectoryReader.open(index);
		IndexSearcher searcher = new IndexSearcher(reader);
		TopDocs docs = searcher.search(q, hitsPerPage);
		ScoreDoc[] hits = docs.scoreDocs;

		// 4. display results
		System.out.println("Found " + hits.length + " hits.");
		for (int i = 0; i < hits.length; ++i) {
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
			System.out.println((i + 1) + ". " + d.get("path"));

			String querystr2 = d.get("path");
			Query q2 = new QueryParser("path", analyzer).parse(querystr2);

			search2(index, q2, analyzer);
		}

		reader.close();
	}

	private static void search2(Directory index, Query q, StandardAnalyzer analyzer)
			throws IOException, ParseException {
		// 3. search
		int hitsPerPage = 10;
		IndexReader reader = DirectoryReader.open(index);
		IndexSearcher searcher = new IndexSearcher(reader);
		TopDocs docs = searcher.search(q, hitsPerPage);
		ScoreDoc[] hits = docs.scoreDocs;

		for (int i = 0; i < hits.length; ++i) {
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
			System.out.println("\t " + d.get("title"));

		}
	}

	private static void addDoc(IndexWriter w, String title, String path) throws IOException {
		Document doc = new Document();
		doc.add(new TextField("title", title, Field.Store.YES));
		doc.add(new TextField("path", path, Field.Store.YES));
		w.addDocument(doc);
	}

}

/*
 * SELECT * FROM documentstable WHERE documents = ( SELECT documents FROM
 * documentstable WHERE name='*kariert*' )
 */