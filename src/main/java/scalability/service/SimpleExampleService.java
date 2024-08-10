package scalability.service;

public class SimpleExampleService implements ExampleService {

	@Override
	public String testString() {
		return "hello";
	}

}
