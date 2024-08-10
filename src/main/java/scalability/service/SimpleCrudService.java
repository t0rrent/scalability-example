package scalability.service;

import java.util.ArrayList;
import java.util.List;

import jakarta.ws.rs.BadRequestException;
import scalability.model.CrudEntity;
import scalability.model.CrudTable;

public class SimpleCrudService implements CrudService {
	
	private final CrudTable table;

	public SimpleCrudService() {
		this.table = new CrudTable();
		table.setTable(getBootstrappedData());
	}

	@Override
	public CrudTable getTable() {
		return table;
	}

	private List<CrudEntity> getBootstrappedData() {
		final List<CrudEntity> data = new ArrayList<>();
		final CrudEntity e1 = new CrudEntity();
		e1.setId(1);
		e1.setEntityName("entity 1");

		final CrudEntity e2 = new CrudEntity();
		e2.setId(2);
		e2.setEntityName("entity 2");
		e2.setEntityDescription("some description");

		data.add(e1);
		data.add(e2);
		return data;
	}

	@Override
	public CrudEntity update(final long id, final CrudEntity updatedEntity) {
		final CrudEntity storedEntity = table.getTable()
				.stream()
				.filter((entity) -> entity.getId() == id)
				.findAny()
				.orElseThrow(BadRequestException::new);
		storedEntity.setEntityName(updatedEntity.getEntityName());
		storedEntity.setEntityDescription(updatedEntity.getEntityDescription());
		return storedEntity;
	}

	@Override
	public CrudEntity create(final CrudEntity newEntity) {
		final long newId = table.getTable()
				.stream()
				.map(CrudEntity::getId)
				.mapToLong(Long::valueOf)
				.max()
				.orElse(0) + 1;
		newEntity.setId(newId);
		table.getTable().add(newEntity);
		return newEntity;
	}

	@Override
	public CrudEntity delete(final long id) {
		final CrudEntity entityToDelete = getById(id);
		table.getTable().remove(entityToDelete);
		return entityToDelete;
	}

	private CrudEntity getById(final long id) {
		return table.getTable()
				.stream()
				.filter((entity) -> entity.getId() == id)
				.findAny()
				.orElseThrow(BadRequestException::new);
	}

}
