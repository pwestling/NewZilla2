package nlp.NewsZilla.Subject;

public class Test {

	public static void main(String[] args) {
		SubjectFinder sf = new SubjectFinder();

		System.out
				.println(sf
						.getSubject("(JJ <title>palestinians) (TO to) (VB receive) "
								+ "(VB aid) (TO to) (NN police) (NN gaza) (JJ jericho<title>)"));

		System.out
				.println(sf
						.getSubject("(null <title>oj) (NNP simpson) (VBD bought) (NN knife) (NN murder) (VBG hearing) (null told<title>)"));
	}

}
