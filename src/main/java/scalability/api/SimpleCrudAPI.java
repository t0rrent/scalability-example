package scalability.api;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import scalability.model.CrudEntity;
import scalability.model.CrudTable;
import scalability.service.CrudService;

@Path(SimpleCrudAPI.PATH)
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
public class SimpleCrudAPI {
	
	protected static final String PATH = "simple-crud";
	
	private final CrudService crudService;
		
	@Inject
	public SimpleCrudAPI(final CrudService crudService) {
		this.crudService = crudService;
	}

	@GET
	public CrudTable getTable() {
		return crudService.getTable();
	}

	@PUT
	public CrudEntity updateEntity(@QueryParam("id") final long id, final CrudEntity entity) {
		return crudService.update(id, entity);
	}

	@POST
	public CrudEntity createEntity(final CrudEntity entity) {
		return crudService.create(entity);
	}

	@DELETE
	public CrudEntity deleteEntity(@QueryParam("id") final long id) {
		return crudService.delete(id);
	}
	
}
