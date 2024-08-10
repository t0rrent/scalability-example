package scalability.service;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import jakarta.inject.Inject;
import scalability.type.ThrowingFunction;

public class MyBatisSessionManagerService implements SessionManagerService {
	
	private final Configuration configuration;
	
	private final ThreadLocal<SqlSession> session;

	private final Supplier<SqlSessionFactory> sqlSessionFactory;
	
	@Inject
	public MyBatisSessionManagerService(final Configuration configuration) {
		this.configuration = configuration;
		this.session = new ThreadLocal<>();
		this.sqlSessionFactory = createSqlSessionFactory();
	}

	@Override
	public <R> R performActionWithSession(final ThrowingFunction<SqlSession, R, Throwable> action) throws Throwable {
		final SqlSession sqlSession = getLocalSession();
		R returnValue = null;
		try {
			R result = action.apply(sqlSession);
			sqlSession.commit();
			returnValue = result;
		} catch (final Exception exception) {
			sqlSession.rollback();
			throw exception;
		} finally {
			sqlSession.close();
			this.session.remove();
		}
		return returnValue;
	}
	
	private SqlSession getLocalSession() {
		SqlSession localSession = this.session.get();
		if (localSession == null) {
			localSession = sqlSessionFactory.get().openSession();
			this.session.set(localSession);
		}
		return localSession;
	}

	private Supplier<SqlSessionFactory> createSqlSessionFactory() {
		final AtomicReference<SqlSessionFactory> session = new AtomicReference<>();
		return () -> {
			if (session.get() == null) {
				session.set(new SqlSessionFactoryBuilder().build(configuration));
			}
			return session.get();
		};
	}

}
