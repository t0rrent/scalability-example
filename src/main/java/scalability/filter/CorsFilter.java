package scalability.filter;

import java.io.IOException;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CorsFilter implements ContainerResponseFilter {

	public static String WEB_CLIENT_HOST = "http://127.0.0.1:80";

	@Override
	public void filter(final ContainerRequestContext request, final ContainerResponseContext response) throws IOException {
		response.getHeaders().add("Access-Control-Allow-Origin", WEB_CLIENT_HOST);
		response.getHeaders().add("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
		response.getHeaders().add("Access-Control-Allow-Origin", "Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");
	}

}
