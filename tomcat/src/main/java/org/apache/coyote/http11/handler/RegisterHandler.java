package org.apache.coyote.http11.handler;

import static org.apache.coyote.http11.headers.HttpHeaderType.*;
import static org.apache.coyote.http11.headers.MimeType.*;
import static org.apache.coyote.http11.response.HttpStatusCode.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Map;

import org.apache.coyote.http11.exception.EmptyBodyException;
import org.apache.coyote.http11.headers.HttpHeaders;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.util.QueryParser;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class RegisterHandler implements HttpHandler {

	private static final Map<HttpMethod, HttpHandle> HANDLE_MAP = Map.of(
		HttpMethod.GET, request -> servingStaticResource(),
		HttpMethod.POST, RegisterHandler::registerProcess
	);
	private static final String END_POINT = "/register";
	private static final String STATIC_RESOURCE_FILE_PATH = "static/register.html";
	private static final String QUERY_ACCOUNT_KEY = "account";
	private static final String QUERY_PASSWORD_KEY = "password";
	private static final String QUERY_EMAIL_KEY = "email";
	private static final String REGISTER_SUCCESS_LOCATION = "http://localhost:8080/index.html";

	@Override
	public boolean isSupported(final HttpRequest request) {
		return request.equalPath(END_POINT)
			&& HANDLE_MAP.containsKey(request.getHttpMethod());
	}

	@Override
	public HttpResponse handleTo(final HttpRequest request) throws IOException {
		final HttpMethod httpMethod = request.getHttpMethod();
		return HANDLE_MAP.get(httpMethod)
			.handle(request);
	}

	private static HttpResponse registerProcess(final HttpRequest request) {
		return request.getBody()
			.map(RegisterHandler::register)
			.orElseThrow(EmptyBodyException::new);
	}

	private static HttpResponse register(final String body) {
		final Map<String, String> parseQuery = QueryParser.parse(body);

		final User user = new User(
			parseQuery.get(QUERY_ACCOUNT_KEY),
			parseQuery.get(QUERY_PASSWORD_KEY),
			parseQuery.get(QUERY_EMAIL_KEY)
		);

		InMemoryUserRepository.save(user);

		final String responseBody = "";
		final HttpHeaders headers = HttpHeaders.of(responseBody, HTML);
		headers.put(LOCATION.getValue(), REGISTER_SUCCESS_LOCATION);
		return new HttpResponse(
			TEMPORARILY_MOVED_302,
			responseBody,
			headers
		);
	}

	private static HttpResponse servingStaticResource() throws RuntimeException {
		try {
			final URL url = RegisterHandler.class.getClassLoader()
				.getResource(STATIC_RESOURCE_FILE_PATH);
			final String body = new String(Files.readAllBytes(new File(url.getFile()).toPath()));
			return new HttpResponse(
				OK_200,
				body,
				HttpHeaders.of(body, HTML)
			);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}
}
