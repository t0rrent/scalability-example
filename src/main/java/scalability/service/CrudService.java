package scalability.service;

import scalability.model.CrudEntity;
import scalability.model.CrudTable;

public interface CrudService {

	CrudTable getTable();

	CrudEntity update(long id, CrudEntity updatedEntity);

	CrudEntity create(CrudEntity newEntity);

	CrudEntity delete(long id);

}
