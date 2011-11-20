package nlp.NewsZilla.DataProcessing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;

public class Main {

	public static void main(String[] args) {
		try {
			File input = new File("data/tdt.text.p.title");

			BufferedReader inputtext = new BufferedReader(new FileReader(input));

			StringBuilder text = new StringBuilder();
			StringBuilder doc = new StringBuilder();
			boolean include = true;
			String temp = inputtext.readLine();
			while (temp != null) {
				doc.append(temp);
				doc.append("\n");
				if (temp.contains("Anchor:") || temp.contains("Correspondent")) {
					include = false;
				}
				if (temp.contains("<\\DOC>")) {
					if (include) {
						text.append(doc.toString());
					}
					doc = new StringBuilder();
					include = true;
				}
				temp = inputtext.readLine();
			}
			String bigstring = text.toString();

			PrintWriter pw = new PrintWriter("articles.processed");
			pw.print(bigstring);
			pw.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
