package org.apache;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class LoginRequestHandler implements RequestHandler {

	private static final Logger log = LoggerFactory.getLogger(LoginRequestHandler.class);

	private final static String URI_PATTERN = "/login";

	private final SessionManager sessionManager;

	public LoginRequestHandler(SessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}

	@Override
	public boolean canHandle(HttpRequest request) {
		return URI_PATTERN.equals(request.getUri());
	}

	@Override
	public HttpResponse handle(HttpRequest httpRequest) throws IOException {
		if (httpRequest.getMethod().equals("GET")) {
			String sessionId = httpRequest.getSessionIdFromCookie();
			if (sessionId == null || sessionManager.findSession(sessionId) == null) {
				URL resource = Http11Processor.class.getClassLoader().getResource("static/login.html");
				File file = new File(resource.getPath());
				final Path path = file.toPath();
				var responseBody = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
				return HttpResponse.ok(httpRequest.getUri(), responseBody);
			}
			return HttpResponse.redirect(httpRequest.getUri(), "/index.html");
		}
		var redirectUri = "/401.html";
		try {
			Map<String, String> payload = httpRequest.getPayload();
			User user = login(payload.get("account"), payload.get("password"));
			UUID uuid = UUID.randomUUID();
			Session session = new Session(uuid.toString());
			session.setAttribute("user", user);
			sessionManager.add(session);

			redirectUri = "/index.html";
			HttpResponse response = HttpResponse.redirect(httpRequest.getUri(), redirectUri);
			response.setCookie("JSESSIONID", uuid.toString());
			return response;
		} catch (RuntimeException exception) {
			return HttpResponse.redirect(httpRequest.getUri(), redirectUri);
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
