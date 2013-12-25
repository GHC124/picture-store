package picturestore.common.social;

public class SocialException extends Exception {

	private static final long serialVersionUID = -2007285097571064971L;

	public SocialException(String message) {
		super(message);
	}

	public SocialException(String message, Throwable clause) {
		super(message, clause);
	}

}
