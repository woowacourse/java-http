package org.apache.coyote.http11.handler;

import static org.apache.coyote.http11.headers.HttpHeaderType.*;
import static org.apache.coyote.http11.response.HttpStatusCode.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Set;

import org.apache.coyote.http11.headers.HttpHeaders;
import org.apache.coyote.http11.headers.MimeType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.db.InMemoryUserRepository;

public class LoginHandler implements HttpHandler {

	private static final String END_POINT = "/login";
	private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);
	private static final String ACCOUNT_PARAM_KEY = "account";

	@Override
	public boolean isSupported(final HttpRequest request) {
		return request.getEndPoint().equals(END_POINT);
	}

	@Override
	public HttpResponse handleTo(final HttpRequest request) throws IOException {
		final Set<String> accounts = request.getQueryParam().get(ACCOUNT_PARAM_KEY);
		for (final String account : accounts) {
			InMemoryUserRepository.findByAccount(account)
				.ifPresent(user -> log.info(user.toString()));
		}
		final String body = resolveBody();
		return new HttpResponse(
			OK_200,
			body,
			resolveHeader(body)
		);
	}

	private String resolveBody() throws IOException {
		final URL url = getClass().getClassLoader()
			.getResource("static/login.html");
		return new String(Files.readAllBytes(new File(url.getFile()).toPath()));
	}

	private HttpHeaders resolveHeader(final String body) {
		final HttpHeaders headers = new HttpHeaders();
		headers.put(CONTENT_TYPE.getValue(), MimeType.HTML.getValue());
		headers.put(CONTENT_LENGTH.getValue(), String.valueOf(body.getBytes().length));
		return headers;
	}
}
