package org.apache.coyote.http11.controller;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import org.apache.coyote.http11.common.Body;
import org.apache.coyote.http11.common.Properties;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class LoginController extends AbstractController {

	@Override
	protected void doPost(HttpRequest request, HttpResponse response) {
		Body body = request.getBody();
		Properties properties = body.parseProperty();

		String account = properties.get("account");
		String password = properties.get("password");
		Optional<User> user = InMemoryUserRepository.findByAccount(account);
		if (user.isPresent() && user.get().checkPassword(password)) {
			Session session = SessionManager.createSession(user.get());

			response.setVersionOfProtocol("HTTP/1.1");
			response.setStatusCode(302);
			response.setStatusMessage("Found");
			response.setHeaders(Map.of(
				"Set-Cookie", "JSESSIONID=" + session.getId(),
				"Location", "http://localhost:8080/index.html"
			));
		}
	}

	@Override
	protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
		Session session = request.getSession();

		if (session == null || SessionManager.findUserBySession(session).isEmpty()) {
			response.setVersionOfProtocol("HTTP/1.1");
			response.setStatusCode(200);
			response.setStatusMessage("OK");
			response.setBody("static" + request.getPath().value() + ".html");

		} else {
			response.setVersionOfProtocol("HTTP/1.1");
			response.setStatusCode(302);
			response.setStatusMessage("Found");
			response.setHeaders(Map.of(
				"Location", "http://localhost:8080" + "index.html"
			));
		}
	}
}
