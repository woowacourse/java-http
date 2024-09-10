package org.apache;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class RegisterRequestHandler implements RequestHandler {

	private static final Logger log = LoggerFactory.getLogger(RegisterRequestHandler.class);

	private final static String URI_PATTERN = "/register";

	@Override
	public boolean canHandle(HttpRequest request) {
		return URI_PATTERN.equals(request.getUri());
	}

	@Override
	public HttpResponse handle(HttpRequest httpRequest) throws IOException {
		if (httpRequest.getMethod().equals("GET")) {
			URL resource = Http11Processor.class.getClassLoader().getResource("static/register.html");
			File file = new File(resource.getPath());
			final Path path = file.toPath();
			var responseBody = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
			return HttpResponse.ok(httpRequest.getUri(), responseBody);
		}
		var redirectUri = "/401.html";
		Map<String, String> params = httpRequest.getPayload();
		boolean isSucceed = register(params.get("account"), params.get("password"), params.get("email"));
		if (isSucceed) {
			redirectUri = "/index.html";
		}
		return HttpResponse.redirect(httpRequest.getUri(), redirectUri);
	}

	private boolean register(String account, String password, String email) {
		try {
			User user = new User(
				(InMemoryUserRepository.getLastId() + 1),
				account,
				password,
				email);
			InMemoryUserRepository.save(user);
			log.info(user.toString());
			return true;
		} catch (RuntimeException exception) {
			return false;
		}
	}
}
