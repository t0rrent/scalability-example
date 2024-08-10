package scalability.hk2.binder;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

import jakarta.inject.Singleton;
import scalability.service.CrudMapperService;
import scalability.service.CrudService;
import scalability.service.VersionMapperService;
import scalability.service.VersionService;

public class ScalabilityExampleModuleBinder extends AbstractBinder {

	@Override
	protected void configure() {
		bind(VersionMapperService.class).to(VersionService.class).in(Singleton.class);
		bind(CrudMapperService.class).to(CrudService.class).in(Singleton.class);
	}

}
