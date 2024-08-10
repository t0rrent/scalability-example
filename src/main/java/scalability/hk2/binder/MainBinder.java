package scalability.hk2.binder;

import java.util.Collection;
import java.util.function.Supplier;

import org.glassfish.hk2.utilities.Binder;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

public class MainBinder extends AbstractBinder {
	
	private final Collection<Supplier<Binder>> binders;

	public MainBinder(final Collection<Supplier<Binder>> binders) {
		this.binders = binders;
	}

	@Override
	protected void configure() {		
		this.binders.stream()
				.map(Supplier::get)
				.forEach(this::install);
	}

}
