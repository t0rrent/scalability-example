package scalability.model.factory;

import org.glassfish.hk2.api.Factory;

import scalability.model.SimpleAliasClass;

public class SimpleAliasClassWrapperFactory<T> implements Factory<SimpleAliasClass<T>> {
	
	private final SimpleAliasClass<T> simpleAliasClass;
	
	public SimpleAliasClassWrapperFactory(final Class<T> simpleAliasClass) {
		this.simpleAliasClass = new SimpleAliasClass<>(simpleAliasClass);
	}

	@Override
	public SimpleAliasClass<T> provide() {
		return simpleAliasClass;
	}

	@Override
	public void dispose(final SimpleAliasClass<T> simpleAliasClass) {
	}

}
