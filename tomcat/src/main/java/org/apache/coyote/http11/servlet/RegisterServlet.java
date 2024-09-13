package org.apache.coyote.http11.servlet;

import java.io.IOException;
import java.util.Map;

import org.apache.coyote.http11.common.Body;
import org.apache.coyote.http11.common.Properties;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusCode;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class RegisterServlet extends AbstractServlet {
	@Override
	protected void doPost(HttpRequest request, HttpResponse response) {
		Body body = request.getBody();
		Properties properties = body.parseProperty();

		String account = properties.get("account");
		String email = properties.get("email");
		String password = properties.get("password");

		if(InMemoryUserRepository.findByAccount(account).isEmpty()) {
			User user = new User(account, password, email);
			InMemoryUserRepository.save(user);
		}
		
		response.setRequestLine("HTTP/1.1", HttpStatusCode.REDIRECT);
		response.setHeaders(Map.of("Location", "http://localhost:8080/index.html"));
	}

	@Override
	protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
		response.setRequestLine("HTTP/1.1", HttpStatusCode.OK);
		response.setBody("static" + request.getPath().value() + ".html");
	}
}
