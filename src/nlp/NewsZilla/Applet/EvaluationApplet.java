package nlp.NewsZilla.Applet;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import nlp.NewsZilla.ArticleProcessing.ArticleModel;

public class EvaluationApplet extends Applet implements ActionListener {

	ArticleModel model;

	JTextField subject;
	JTextField verb;
	JTextArea article;
	JTextField comments;
	JButton submitButton;
	OneThroughFiveRadio funnyButton;
	OneThroughFiveRadio cohesionButton;
	OneThroughFiveRadio qualityButton;
	JButton evalButton;

	File evalFile;

	String basinRoot = "http://www.cs.middlebury.edu/~wwestlin/articlegen/";

	@Override
	public void init() {
		this.setLayout(new BorderLayout());

		try {
			model = ArticleModel.makeArticleModelFromSerial(new URL(basinRoot + "data/aModel" + 2 + ".serial"));

			evalFile = (new File((new File(getDocumentBase().getFile())).getParentFile(), "eval/eval.txt"));

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JPanel submitPanel = new JPanel();
		subject = new JTextField("Subject");
		verb = new JTextField("Verb");
		submitButton = new JButton("Submit");
		submitButton.addActionListener(this);
		submitPanel.add(subject);
		submitPanel.add(verb);
		submitPanel.add(submitButton);
		this.add(submitPanel, BorderLayout.NORTH);

		article = new JTextArea("Article will appear here");
		this.add(article, BorderLayout.CENTER);
		article.setLineWrap(true);
		JPanel evalPanel = new JPanel(new GridLayout(5, 1));

		funnyButton = new OneThroughFiveRadio("Humor");
		evalPanel.add(funnyButton);
		cohesionButton = new OneThroughFiveRadio("Cohesion");
		evalPanel.add(cohesionButton);
		qualityButton = new OneThroughFiveRadio("Quality");

		evalPanel.add(qualityButton);

		comments = new JTextField("Any Comments?");
		evalPanel.add(comments);
		evalButton = new JButton("Submit Evaluation");
		evalButton.addActionListener(this);
		JPanel flowPanel = new JPanel();
		flowPanel.add(evalButton);
		evalPanel.add(flowPanel);
		this.add(evalPanel, BorderLayout.SOUTH);

	}

	boolean evalSubmitted = true;

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == submitButton) {
			String art = model.makeArticle(subject.getText(), verb.getText());
			article.setText(art);
			evalSubmitted = false;
		}

		if (e.getSource() == evalButton && !evalSubmitted) {

			StringBuilder sb = new StringBuilder();
			sb.append("<EVAL>\n");
			sb.append("<ARTICLE>\n" + article.getText() + "\n</ARTICLE>\n");
			sb.append("<GRADE>\n" + funnyButton.getSelection() + "\n" + cohesionButton.getSelection() + "\n"
					+ qualityButton.getSelection() + "\n</GRADE>\n");
			sb.append("<COMMENT>\n" + comments.getText() + "\n</COMMENT>\n");
			sb.append("\n</EVAL>\n");

			try {
				URL evalScript = new URL(basinRoot + "eval.php?eval=" + URLEncoder.encode(sb.toString(), "UTF-8"));
				System.out.println(evalScript.toString());
				URLConnection conn = evalScript.openConnection();
				// conn.setRequestProperty("Accept-Charset", "UTF-8");
				InputStream inputStream = conn.getInputStream();
				while (inputStream.available() > 0) {
					System.out.print(inputStream.read());
				}
				System.out.println();

			} catch (Exception e3) {
				e3.printStackTrace();
			}

			comments.setText("");
			evalSubmitted = true;

		}

	}

	class OneThroughFiveRadio extends JPanel {

		JRadioButton[] buttons = new JRadioButton[5];
		ButtonGroup group = new ButtonGroup();

		public OneThroughFiveRadio(String label) {
			Label l = new Label(label);
			this.add(l);

			this.setLayout(new GridLayout(1, 6));
			for (int i = 0; i < 5; i++) {
				buttons[i] = new JRadioButton((i + 1) + "");
				this.add(buttons[i], i);
				group.add(buttons[i]);
			}
			buttons[2].setSelected(true);

		}

		public int getSelection() {
			for (int i = 0; i < 5; i++) {
				if (buttons[i].isSelected()) {
					return i + 1;
				}
			}
			return 0;
		}

	}

}
