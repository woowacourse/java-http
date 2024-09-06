package com.techcourse.web.handler;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import org.apache.coyote.http11.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import com.techcourse.web.HttpResponse;
import com.techcourse.web.HttpStatusCode;
import com.techcourse.web.annotation.Param;
import com.techcourse.web.annotation.Query;
import com.techcourse.web.annotation.Request;

public class LoginHandler extends RequestHandler {

	private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);
	private static final LoginHandler instance = new LoginHandler();

	private LoginHandler() {
	}

	public static LoginHandler getInstance() {
		return instance;
	}

	@Request(path = "/login", method = "GET")
	@Query(params = {
		@Param(key = "account", required = true),
		@Param(key = "password", required = true)
	})
	public HttpResponse loginWithQuery(HttpRequest request) throws IOException {
		Map<String, String> query = request.getRequestQuery();
		String account = query.get("account");
		String password = query.get("password");

		logUser(account, password);

		return HttpResponse.builder()
			.protocol(request.getProtocol())
			.statusCode(HttpStatusCode.OK)
			.body(HttpResponse.Body.fromPath(request.getRequestPath()))
			.build();
	}

	private void logUser(String account, String password) {
		Optional<User> userOptional = InMemoryUserRepository.findByAccount(account);
		if (userOptional.isEmpty()) {
			log.info("user not found. account: {}", account);
			return;
		}

		User user = userOptional.get();
		if (!user.checkPassword(password)) {
			log.info("password not matched. account: {}", account);
			return;
		}

		log.info("user: {}", user);
	}
}
