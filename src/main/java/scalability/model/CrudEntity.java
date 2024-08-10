package scalability.model;

public class CrudEntity {
	
	private long id;
	
	private String entityName;
	
	private String entityDescription;

	public long getId() {
		return id;
	}

	public void setId(final long id) {
		this.id = id;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(final String entityName) {
		this.entityName = entityName;
	}

	public String getEntityDescription() {
		return entityDescription;
	}

	public void setEntityDescription(final String entityDescription) {
		this.entityDescription = entityDescription;
	}

}
