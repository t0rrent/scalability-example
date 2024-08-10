package scalability.service;

import org.apache.ibatis.session.SqlSession;

import scalability.type.ThrowingFunction;

public interface SessionManagerService {

	<R> R performActionWithSession(ThrowingFunction<SqlSession, R, Throwable> action) throws Throwable;

}
