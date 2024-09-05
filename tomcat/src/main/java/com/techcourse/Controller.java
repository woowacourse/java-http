package com.techcourse;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;
import java.util.Optional;

import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class Controller {

	private static final Logger log = LoggerFactory.getLogger(Controller.class);

	public String processGetRequest(String uri) throws IOException {
		if (uri.equals("/")) {
			return "Hello world!";
		}

		if (uri.equals("/register")) {
			URL resource = getClass().getClassLoader().getResource("static" + uri + ".html");
			return new String(Files.readAllBytes(new File(resource.getFile()).toPath()), StandardCharsets.UTF_8);
		}

		URL resource = getClass().getClassLoader().getResource("static" + uri);
		return new String(Files.readAllBytes(new File(resource.getFile()).toPath()), StandardCharsets.UTF_8);
	}

	public String processPostRequest(String uri, Map<String, String> params) {
		if (uri.startsWith("/login")) {

			Optional<User> byAccount = InMemoryUserRepository.findByAccount(params.get("account"));

			if (byAccount.isPresent()) {
				log.info(byAccount.get().getAccount());
				return "/index.html";
			}
			return "/401.html";
		}

		if (uri.startsWith("/register")) {
			User user = new User(
				(InMemoryUserRepository.getLastId() + 1),
				params.get("account"),
				params.get("password"),
				params.get("email"));
			InMemoryUserRepository.save(user);

			log.info(user.toString());
			return "/index.html";
		}

		throw new IllegalArgumentException("undefined POST URI");
	}
}
