package org.apache.coyote.http11.handler;

import static org.apache.coyote.http11.headers.HttpHeaderType.*;
import static org.apache.coyote.http11.response.HttpStatusCode.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Map;
import java.util.UUID;

import org.apache.coyote.http11.exception.EmptyBodyException;
import org.apache.coyote.http11.exception.UnauthorizedException;
import org.apache.coyote.http11.headers.HttpHeaders;
import org.apache.coyote.http11.headers.MimeType;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusCode;
import org.apache.coyote.http11.util.QueryParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class LoginHandler implements HttpHandler {

	private static final Map<HttpMethod, HttpHandle> HANDLE_MAP = Map.of(
		HttpMethod.GET, request -> servingStaticResource(),
		HttpMethod.POST, LoginHandler::loginProcess
	);
	private static final String END_POINT = "/login";
	private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);
	private static final String ACCOUNT_PARAM_KEY = "account";
	private static final String PASSWORD_PARAM_KEY = "password";
	private static final String LOGIN_SUCCESS_LOCATION = "http://localhost:8080/index.html";
	private static final String STATIC_RESOURCE_FILE_PATH = "static/login.html";

	@Override
	public boolean isSupported(final HttpRequest request) {
		return request.getEndPoint().equals(END_POINT)
			&& HANDLE_MAP.containsKey(request.getHttpMethod());
	}

	@Override
	public HttpResponse handleTo(final HttpRequest request) {
		final HttpMethod httpMethod = request.getHttpMethod();
		return HANDLE_MAP.get(httpMethod)
			.handle(request);
	}

	private static HttpResponse servingStaticResource() {
		try {
			final URL url = LoginHandler.class.getClassLoader()
				.getResource(STATIC_RESOURCE_FILE_PATH);
			final String body = new String(Files.readAllBytes(new File(url.getFile()).toPath()));
			return new HttpResponse(
				OK_200,
				body,
				resolveHeader(body)
			);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static HttpResponse loginProcess(final HttpRequest request) {
		final Map<String, String> parsedQuery = request.getBody()
			.map(QueryParser::parse)
			.orElseThrow(EmptyBodyException::new);
		final String account = parsedQuery.get(ACCOUNT_PARAM_KEY);
		return InMemoryUserRepository.findByAccount(account)
			.map(user -> checkPasswordProcess(parsedQuery.get(PASSWORD_PARAM_KEY), user))
			.orElseThrow(UnauthorizedException::new);
	}

	private static HttpResponse checkPasswordProcess(final String password, final User user) {
		if (user.checkPassword(password)) {
			log.info(user.toString());
			return loginSuccessResponse();
		}
		throw new UnauthorizedException();
	}

	private static HttpResponse loginSuccessResponse() {
		final String body = "";
		final HttpHeaders headers = resolveHeader(body);
		headers.put(LOCATION.getValue(), LOGIN_SUCCESS_LOCATION);
		headers.put(SET_COOKIE.getValue(), UUID.randomUUID().toString());
		return new HttpResponse(
			HttpStatusCode.TEMPORARILY_MOVED_302,
			body,
			headers
		);
	}

	private static HttpHeaders resolveHeader(final String body) {
		final HttpHeaders headers = new HttpHeaders();
		headers.put(CONTENT_TYPE.getValue(), MimeType.HTML.getValue());
		headers.put(CONTENT_LENGTH.getValue(), String.valueOf(body.getBytes().length));
		return headers;
	}
}
