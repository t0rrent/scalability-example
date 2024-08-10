package scalability.hk2.binder;

import scalability.mapper.CrudMapper;
import scalability.mapper.VersionMapper;
import scalability.model.CrudEntity;

public class ScalabilityExampleMyBatisBinder extends AbstractMyBatisBinder {

	@Override
	protected void configure() {
		bindMapper(CrudMapper.class);
		bindMapper(VersionMapper.class);
		addSimpleAlias(CrudEntity.class);
	}

}
