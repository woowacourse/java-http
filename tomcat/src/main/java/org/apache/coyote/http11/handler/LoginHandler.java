package org.apache.coyote.http11.handler;

import static org.apache.coyote.http11.headers.HttpHeaderType.*;
import static org.apache.coyote.http11.response.HttpStatusCode.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import org.apache.coyote.http11.exception.UnauthorizedException;
import org.apache.coyote.http11.headers.HttpHeaders;
import org.apache.coyote.http11.headers.MimeType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.QueryParam;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class LoginHandler implements HttpHandler {

	private static final String END_POINT = "/login";
	private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);
	private static final String ACCOUNT_PARAM_KEY = "account";
	private static final String PASSWORD_PARAM_KEY = "password";
	private static final String LOGIN_SUCCESS_LOCATION = "http://localhost:8080/index.html";

	@Override
	public boolean isSupported(final HttpRequest request) {
		return request.getEndPoint().equals(END_POINT);
	}

	@Override
	public HttpResponse handleTo(final HttpRequest request) throws IOException {
		final QueryParam queryParam = request.getQueryParam();
		if (!queryParam.isBlank()) {
			return loginProcess(queryParam);
		}
		return servingStaticResource();
	}

	private HttpResponse servingStaticResource() throws IOException {
		final URL url = getClass().getClassLoader()
			.getResource("static/login.html");
		final String body = new String(Files.readAllBytes(new File(url.getFile()).toPath()));
		return new HttpResponse(
			OK_200,
			body,
			resolveHeader(body)
		);
	}

	private HttpResponse loginProcess(final QueryParam queryParam) {
		final String account = queryParam.get(ACCOUNT_PARAM_KEY);
		return InMemoryUserRepository.findByAccount(account)
			.map(user -> checkPasswordProcess(queryParam.get(PASSWORD_PARAM_KEY), user))
			.orElseThrow(UnauthorizedException::new);
	}

	private HttpResponse checkPasswordProcess(final String password, final User user) {
		if (user.checkPassword(password)) {
			log.info(user.toString());
			return loginSuccessResponse();
		}
		throw new UnauthorizedException();
	}

	private HttpResponse loginSuccessResponse() {
		final String body = "";
		final HttpHeaders headers = resolveHeader(body);
		headers.put(LOCATION.getValue(), LOGIN_SUCCESS_LOCATION);
		return new HttpResponse(
			HttpStatusCode.TEMPORARILY_MOVED_302,
			body,
			headers
		);
	}

	private HttpHeaders resolveHeader(final String body) {
		final HttpHeaders headers = new HttpHeaders();
		headers.put(CONTENT_TYPE.getValue(), MimeType.HTML.getValue());
		headers.put(CONTENT_LENGTH.getValue(), String.valueOf(body.getBytes().length));
		return headers;
	}
}
