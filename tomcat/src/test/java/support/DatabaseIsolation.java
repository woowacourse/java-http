package support;

import java.lang.reflect.Field;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class DatabaseIsolation {

	@SuppressWarnings("unchecked")
	@AfterEach
	void clear() throws NoSuchFieldException, IllegalAccessException {
		Field databaseField = InMemoryUserRepository.class.getDeclaredField("database");
		databaseField.setAccessible(true);
		Map<String, User> database = (Map<String, User>)databaseField.get(null);
		database.clear();
	}
}
