package scalability.mapper;

import java.util.List;

import scalability.model.CrudEntity;

public interface CrudMapper {

	List<CrudEntity> getAll();

	void update(CrudEntity updatedEntity);

	void create(CrudEntity newEntity);

	CrudEntity getById();

	void delete(long id);

}
