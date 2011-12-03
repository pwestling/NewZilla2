package nlp.NewsZilla.ArticleProcessing;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Main {

	public static void main(String[] args) throws FileNotFoundException {
		System.out.println("Start");
		ArticleModel am = new ArticleModel("data/articles.processed", 3);
		System.out.println("End");
		PrintWriter pw = new PrintWriter("articles.txt");

		String[] subs = { "Alex Clement", "Armaan", "Porter" };
		String[] verbs = { "wrote", "flew", "discussed" };

		for (int i = 0; i < subs.length; i++) {
			pw.println(am.makeArticle(subs[i], verbs[i]));
			pw.println("\n\n");
		}

		pw.close();

		// String s =
		// "She said ``decent health care'' and education were the key elements needed to bring black women into the mainstream of U.S. society. <P>"
		// +
		// "``We all know that black women are the stewards and custodians of our culture,'' she said. ``But we've got to make sure our women know how to use the system.'' <P>";
		// String[] sp =
		// s.split("((?<=\\?)|(?<=\\!)|(?<=([^A-Z]\\. ))|(?<=([^A-Z]\\.''))|(?<=;))");
		// for (String str : sp) {
		// System.out.println(str);
		// }

	}
}
