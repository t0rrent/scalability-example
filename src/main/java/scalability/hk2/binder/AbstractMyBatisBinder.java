package scalability.hk2.binder;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.hk2.utilities.binding.ServiceBindingBuilder;

import jakarta.inject.Singleton;
import scalability.model.MapperClass;
import scalability.model.SimpleAliasClass;
import scalability.model.factory.MapperClassWrapperFactory;
import scalability.model.factory.SimpleAliasClassWrapperFactory;

public abstract class AbstractMyBatisBinder extends AbstractBinder {

	protected <T> ServiceBindingBuilder<MapperClass<T>> bindMapper(final Class<T> mapperType) {
		return bindFactory(new MapperClassWrapperFactory<T>(mapperType)).to(MapperClass.class);
	}
	
	protected void addSimpleAlias(final Class<?> paramType) {
		bindFactory(new SimpleAliasClassWrapperFactory<>(paramType)).to(SimpleAliasClass.class).in(Singleton.class);
	}

}
