package scalability.hk2.binder;

import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

public class SimpleFactoryBinder<T, C>  extends AbstractBinder {
	
	private static final int DEFAULT_RANK = 10;

	private final Factory<T> factory;
	
	private final Class<C> contract;
	
	private final int rank;

	public SimpleFactoryBinder(final Factory<T> factory, final Class<C> contract, final int rank) {
		this.factory = factory;
		this.contract = contract;
		this.rank = rank;
	}

	public SimpleFactoryBinder(final Factory<T> factory, final Class<C> contract) {
		this(factory, contract, DEFAULT_RANK);
	}

	@Override
	protected void configure() {
		bindFactory(factory).to(contract).ranked(rank);
	}

}
