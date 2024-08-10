package scalability.service;

import jakarta.inject.Inject;
import scalability.mapper.CrudMapper;
import scalability.model.CrudEntity;
import scalability.model.CrudTable;

public class CrudMapperService implements CrudService {
	
	private final CrudMapper crudMapper;
	
	@Inject
	public CrudMapperService(final CrudMapper crudMapper) {
		this.crudMapper = crudMapper;
	}

	@Override
	public CrudTable getTable() {
		final CrudTable crudTable = new CrudTable();
		crudTable.setTable(crudMapper.getAll());
		return crudTable;
	}

	@Override
	public CrudEntity update(final long id, final CrudEntity updatedEntity) {
		updatedEntity.setId(id);
		crudMapper.update(updatedEntity);
		return updatedEntity;
	}

	@Override
	public CrudEntity create(final CrudEntity newEntity) {
		crudMapper.create(newEntity);
		return newEntity;
	}

	@Override
	public CrudEntity delete(final long id) {
		final CrudEntity toDelete = crudMapper.getById();
		crudMapper.delete(id);
		return toDelete;
	}

}
