package org.apache;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpRequestBody;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.StatusLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class RegisterController extends AbstractController {

	private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

	private final static String URI_PATTERN = "/register";

	@Override
	public boolean canHandle(HttpRequest request) {
		return URI_PATTERN.equals(request.getUri());
	}

	@Override
	protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
		URL resource = Http11Processor.class.getClassLoader().getResource("static/register.html");
		File file = new File(resource.getPath());
		final Path path = file.toPath();
		response.setResponseBody(Files.readAllBytes(path));
		response.setContentType(request.getUri());
		response.setContentLength();
		response.setStatusLine(StatusLine.from(HttpStatus.OK));
		super.doGet(request, response);
	}

	@Override
	protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
		var redirectUri = "401.html";
		HttpRequestBody body = request.getRequestBody();
		boolean isSucceed = register(body.get("account"), body.get("password"), body.get("email"));
		if (isSucceed) {
			redirectUri = "index.html";
		}

		URL resource = Http11Processor.class.getClassLoader().getResource("static/" + redirectUri);
		File file = new File(resource.getPath());
		final Path path = file.toPath();
		response.setContentType(request.getUri());
		response.setResponseBody(Files.readAllBytes(path));
		response.setContentLength();
		response.setLocation("/" + redirectUri);

		super.doPost(request, response);
	}

	private boolean register(String account, String password, String email) {
		try {
			Optional<User> user = InMemoryUserRepository.findByAccount(account);
			if (user.isPresent()) {
				throw new IllegalArgumentException("already exist account: " + account);
			}

			User newUser = new User(
				(InMemoryUserRepository.getLastId() + 1),
				account,
				password,
				email);
			InMemoryUserRepository.save(newUser);
			log.info(newUser.toString());

			return true;
		} catch (RuntimeException exception) {
			return false;
		}
	}
}
