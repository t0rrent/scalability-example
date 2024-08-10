package scalability.model;

public class WrappedString {
	
	private String string;

	public String getString() {
		return string;
	}

	public void setString(final String string) {
		this.string = string;
	}

	public static WrappedString wrap(final String string) {
		final WrappedString wrappedString = new WrappedString();
		wrappedString.setString(string);
		return wrappedString;
	}

}
