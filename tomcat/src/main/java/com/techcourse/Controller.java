package com.techcourse;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class Controller {

	private static final Logger log = LoggerFactory.getLogger(Controller.class);

	public String getHomePage() {
		return "Hello world!";
	}

	public String getRegisterPage() throws IOException {
		URL resource = getClass().getClassLoader().getResource("static/register.html");
		return new String(Files.readAllBytes(new File(resource.getFile()).toPath()), StandardCharsets.UTF_8);
	}

	public String getUriPage(String uri) throws IOException {
		URL resource = getClass().getClassLoader().getResource("static" + uri);
		return new String(Files.readAllBytes(new File(resource.getFile()).toPath()), StandardCharsets.UTF_8);
	}

	public UUID login(String account, String password) {
		Optional<User> user = InMemoryUserRepository.findByAccount(account);
		if (user.isPresent()) {
			log.info(user.get().getAccount());
			return UUID.randomUUID();
		}
		throw new IllegalArgumentException("login fail");
	}

	public boolean register(String account, String password, String email) {
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
