package com.techcourse.web.handler;

import java.io.IOException;
import java.util.Optional;

import org.apache.coyote.http11.http.request.HttpMethod;
import org.apache.coyote.http11.http.request.HttpRequest;
import org.apache.coyote.http11.http.request.HttpRequestLine;
import org.apache.coyote.http11.http.request.HttpRequestQuery;
import org.apache.coyote.http11.http.response.HttpResponse;
import org.apache.coyote.http11.http.response.HttpResponseBody;
import org.apache.coyote.http11.http.response.HttpResponseHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import com.techcourse.web.util.ResourceLoader;

public class LoginHandler implements Handler {

	private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);
	private static final LoginHandler instance = new LoginHandler();

	private LoginHandler() {
	}

	public static LoginHandler getInstance() {
		return instance;
	}

	@Override
	public boolean isSupport(HttpRequestLine requestLine) {
		return requestLine.getRequestPath().startsWith("/login");
	}

	@Override
	public HttpResponse handle(HttpRequest request) throws IOException {
		HttpRequestLine requestLine = request.getRequestLine();
		HttpMethod method = requestLine.getMethod();

		if (method == HttpMethod.GET) {
			return handleGet(request);
		}

		HttpResponseBody notFoundPage = ResourceLoader.getInstance().loadResource("/404.html");
		return HttpResponse.notFound(new HttpResponseHeader(), notFoundPage);
	}

	private HttpResponse handleGet(HttpRequest request) throws IOException {
		HttpRequestLine requestLine = request.getRequestLine();
		HttpRequestQuery query = requestLine.getQuery();
		checkUser(query);

		HttpResponseBody body = ResourceLoader.getInstance().loadResource("/login.html");
		return HttpResponse.ok(new HttpResponseHeader(), body);
	}

	private void checkUser(HttpRequestQuery query) {
		String account = query.getValue("account");
		Optional<User> userOptional = InMemoryUserRepository.findByAccount(account);
		if (userOptional.isEmpty()) {
			log.info("user not found. account: {}", account);
			return;
		}

		User user = userOptional.get();
		String password = query.getValue("password");
		if (!user.checkPassword(password)) {
			log.info("password not matched. account: {}", account);
			return;
		}

		log.info("user: {}", user);
	}
}
