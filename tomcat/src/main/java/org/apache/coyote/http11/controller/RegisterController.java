package org.apache.coyote.http11.controller;

import java.io.IOException;
import java.util.Map;

import org.apache.coyote.http11.common.Body;
import org.apache.coyote.http11.common.Properties;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class RegisterController extends AbstractController {
	@Override
	protected void doPost(HttpRequest request, HttpResponse response) {
		Body body = request.getBody();
		Properties properties = body.parseProperty();

		String account = properties.get("account");
		String mail = properties.get("mail");
		String password = properties.get("password");

		User user = new User(account, mail, password);
		InMemoryUserRepository.save(user);

		response.setVersionOfProtocol("HTTP/1.1");
		response.setStatusCode(302);
		response.setStatusMessage("Found");
		response.setHeaders(Map.of("Location", "http://localhost:8080/index.html"));
	}

	@Override
	protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
		response.setVersionOfProtocol("HTTP/1.1");
		response.setStatusCode(200);
		response.setStatusMessage("OK");
		response.setBody("static" + request.getPath().value() + ".html");
	}
}
