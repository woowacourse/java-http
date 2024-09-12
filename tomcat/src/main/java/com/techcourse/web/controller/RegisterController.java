package com.techcourse.web.controller;

import java.io.IOException;
import java.util.Map;

import org.apache.coyote.http11.http.HttpCookie;
import org.apache.coyote.http11.http.HttpHeader;
import org.apache.coyote.http11.http.request.HttpRequest;
import org.apache.coyote.http11.http.request.HttpRequestLine;
import org.apache.coyote.http11.http.response.HttpResponse;
import org.apache.coyote.http11.http.response.HttpStatusCode;
import org.apache.coyote.http11.http.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import com.techcourse.web.Resource;
import com.techcourse.web.util.FormUrlEncodedParser;
import com.techcourse.web.util.JsessionIdGenerator;
import com.techcourse.web.util.ResourceLoader;

public class RegisterController extends AbstractController {

	private static final Logger log = LoggerFactory.getLogger(RegisterController.class);
	private static final String REGISTER_PATH = "/register";
	private static final RegisterController instance = new RegisterController();

	private RegisterController() {
	}

	public static RegisterController getInstance() {
		return instance;
	}

	@Override
	public boolean isSupport(HttpRequest request) {
		HttpRequestLine requestLine = request.getRequestLine();
		return requestLine.getRequestPath().startsWith(REGISTER_PATH);
	}

	@Override
	public void doGet(HttpRequest request, HttpResponse response) throws IOException {
		Resource resource = ResourceLoader.getInstance().loadResource("/register.html");
		response.setStatusCode(HttpStatusCode.OK);
		response.setBody(resource.getContentType(), resource.getContent());
	}

	@Override
	public void doPost(HttpRequest request, HttpResponse response) {
		try {
			Map<String, String> data = FormUrlEncodedParser.parse(request.getRequestBody());
			User user = new User(data.get("account"), data.get("password"), data.get("email"));
			InMemoryUserRepository.save(user);

			login(user, response);
		} catch (IllegalArgumentException e) {
			log.error("Failed to register user. {}", e.getMessage());
			response.setStatusCode(HttpStatusCode.BAD_REQUEST);
		}
	}

	private void login(User user, HttpResponse response) {
		String sessionId = JsessionIdGenerator.generate();
		response.addHeader(HttpHeader.SET_COOKIE.getName(), HttpCookie.JSESSIONID + "=" + sessionId);
		SessionManager.createSession(sessionId, user);

		redirect(response, "/index.html");
	}
}
