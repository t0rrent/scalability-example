package scalability.hk2.factory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import org.glassfish.hk2.api.Factory;

import scalability.service.SessionManagerService;

public class MyBatisMapperFactory<T>implements Factory<T> {
	
	private final Class<T> mapperType;
	
	private final SessionManagerService sessionManagerService;
	
	public MyBatisMapperFactory(final Class<T> mapperType, final SessionManagerService sessionManagerService) {
		this.mapperType = mapperType;
		this.sessionManagerService = sessionManagerService;
	}

	@Override
	public T provide() {
		final ClassLoader classLoader = mapperType.getClassLoader();
		final Class<?>[] interfaces = new Class[] { mapperType };
		final InvocationHandler invocationHandler = (proxy, method, parameters) -> {
			return sessionManagerService.performActionWithSession((session) -> {
				return method.invoke(session.getMapper(mapperType), parameters);
			});
		};
		return mapperType.cast(Proxy.newProxyInstance(classLoader, interfaces, invocationHandler));
	}

	@Override
	public void dispose(final T instance) {
	}

}
