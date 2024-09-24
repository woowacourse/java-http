package com.techcourse.servlet;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpRequestBody;
import org.apache.coyote.http11.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

import jakarta.servlet.AbstractController;

public class LoginController extends AbstractController {

	private static final Logger log = LoggerFactory.getLogger(LoginController.class);

	private final static String URI_PATTERN = "/login";

	private final SessionManager sessionManager;

	public LoginController(SessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}

	@Override
	public boolean canHandle(HttpRequest request) {
		return URI_PATTERN.equals(request.getUri());
	}

	@Override
	protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
		Optional<String> sessionId = request.getSessionIdFromCookie();
		if (sessionId.isEmpty() || sessionManager.findSession(sessionId.get()) == null) {
			URL resource = getClass().getClassLoader().getResource("static/" + "login.html");
			File file = new File(resource.getPath());
			final Path path = file.toPath();
			response.setContentType("text/html");
			response.ok(Files.readAllBytes(path));
			return;
		}
		response.redirect("index.html");
	}

	@Override
	protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
		try {
			HttpRequestBody body = request.getRequestBody();
			User user = login(body.get("account"), body.get("password"));
			UUID uuid = UUID.randomUUID();
			Session session = new Session(uuid.toString());
			session.setAttribute("user", user);
			sessionManager.add(session);

			response.redirect("index.html");
			response.setCookie("JSESSIONID", uuid.toString());
			return;
		} catch (RuntimeException exception) {
			response.redirect("401.html");
		}
	}

	private User login(String account, String password) {
		Optional<User> user = InMemoryUserRepository.findByAccount(account);
		if (user.isPresent()) {
			validateUser(user.get(), password);
			log.info(user.get().getAccount());
			return user.get();
		}
		throw new IllegalArgumentException("login fail");
	}

	private void validateUser(User user, String password) {
		if (!user.checkPassword(password)) {
			throw new IllegalArgumentException("invalid password");
		}
	}
}
