package org.apache.coyote.http11;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class UserService {

	private final Logger log;

	public UserService(Logger log) {
		this.log = log;
	}

	public void logUserInfo(String queryString) {
		List<String> strings = parseQueryString(queryString);
		final String key = strings.get(0);
		final String value = strings.get(1);

		final Optional<User> optionalUser = InMemoryUserRepository.findByAccount(key);
		if (optionalUser.isPresent()) {
			logUserInfo(value, optionalUser);
		}
	}

	private List<String> parseQueryString(String queryString) {
		String[] values = queryString.split("&");
		List<String> data = new ArrayList<>();
		for (String param : values) {
			final String[] nameAndValue = param.split("=");
			data.add(nameAndValue[1]);
		}
		return data;
	}

	private void logUserInfo(String value, Optional<User> optionalUser) {
		User user = optionalUser.get();
		if (user.checkPassword(value)) {
			log.info(user.toString());
		}
	}
}
