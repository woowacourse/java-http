package com.techcourse.web.handler;

import java.io.IOException;
import java.util.Map;

import org.apache.coyote.http11.http.request.HttpMethod;
import org.apache.coyote.http11.http.request.HttpRequest;
import org.apache.coyote.http11.http.request.HttpRequestLine;
import org.apache.coyote.http11.http.response.HttpResponse;
import org.apache.coyote.http11.http.response.HttpResponseBody;
import org.apache.coyote.http11.http.response.HttpResponseHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import com.techcourse.web.util.FormUrlEncodedParser;
import com.techcourse.web.util.ResourceLoader;

public class RegisterHandler implements Handler {

	private static final Logger log = LoggerFactory.getLogger(RegisterHandler.class);
	private static final String REGISTER_PATH = "/register";
	private static final RegisterHandler instance = new RegisterHandler();

	private RegisterHandler() {
	}

	public static RegisterHandler getInstance() {
		return instance;
	}

	@Override
	public boolean isSupport(HttpRequestLine requestLine) {
		return requestLine.getRequestPath().startsWith(REGISTER_PATH);
	}

	@Override
	public HttpResponse handle(HttpRequest request) throws IOException {
		HttpRequestLine requestLine = request.getRequestLine();
		HttpMethod method = requestLine.getMethod();

		if (method == HttpMethod.GET) {
			return loadRegisterPage();
		}
		if (method == HttpMethod.POST) {
			return register(request);
		}

		return notFoundResponse();
	}

	private HttpResponse register(HttpRequest request) {
		try {
			Map<String, String> data = FormUrlEncodedParser.parse(request.getRequestBody());
			User user = new User(data.get("account"), data.get("password"), data.get("email"));
			InMemoryUserRepository.save(user);

			return redirect(new HttpResponseHeader(), "/index.html");
		} catch (IllegalArgumentException e) {
			log.error("Failed to register user. {}", e.getMessage());
			return HttpResponse.badRequest(new HttpResponseHeader());
		}
	}

	private HttpResponse loadRegisterPage() throws IOException {
		HttpResponseBody body = ResourceLoader.getInstance().loadResource("/register.html");
		return HttpResponse.ok(new HttpResponseHeader(), body);
	}
}
