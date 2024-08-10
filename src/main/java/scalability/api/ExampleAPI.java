package scalability.api;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import scalability.model.WrappedString;
import scalability.service.VersionService;

@Path(ExampleAPI.PATH)
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
public class ExampleAPI {
	
	protected static final String PATH = "example";
	
	private final VersionService versionService;
	
	@Inject
	public ExampleAPI(final VersionService versionService) {
		this.versionService = versionService;
	}	

	@GET
	@Path("backend-id")
	public WrappedString getBackendId() {
		return WrappedString.wrap(versionService.getBackendId());
	}

	@GET
	@Path("database-version")
	public WrappedString getDatabaseVersion() {
		return WrappedString.wrap(versionService.getDatabaseVersion());
	}
	
}
