package scalability.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.InternalServerErrorException;

@WebServlet
public class SimpleHttpServlet extends HttpServlet {

	private static final long serialVersionUID = 6418693879783314582L;

	@Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
		try (final InputStream inputStream = new FileInputStream(getResource(request.getRequestURI()))) {
            byte[] buffer = new byte[1024];
			int bytesRead;
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				response.getOutputStream().write(buffer, 0, bytesRead);
			}
        } catch (final RuntimeException runtimeException) {
        	throw new InternalServerErrorException(runtimeException.getMessage());
        }
	}

	private String getResource(final String resource) {
		final String staticResourceLocation = "pub" + resource;
		if (new File(staticResourceLocation).isDirectory()) {
			return staticResourceLocation + "/index.html";
		} else {
			return staticResourceLocation;
		}
	}

}
