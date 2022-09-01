package org.apache.coyote.http11;

import java.io.IOException;
import java.net.Socket;
import java.util.Optional;

import org.apache.coyote.Processor;
import org.apache.coyote.http11.exception.FileNotFoundException;
import org.apache.coyote.http11.http.ContentType;
import org.apache.coyote.http11.http.Header;
import org.apache.coyote.http11.http.HttpMethod;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.http11.http.StatusCode;
import org.apache.coyote.http11.util.StaticResourceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;

public class Http11Processor implements Runnable, Processor {

	private static final String UNAUTHORIZED_HTML = "401.html";
	private static final String NOT_FOUND_HTML = "404.html";
	private static final String LOGIN_HTML = "login.html";
	private static final String REGISTER_HTML = "register.html";
	private static final String INDEX_HTML = "index.html";

	private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

	private final Socket connection;

	public Http11Processor(final Socket connection) {
		this.connection = connection;
	}

	@Override
	public void run() {
		process(connection);
	}

	@Override
	public void process(final Socket connection) {
		try (final var inputStream = connection.getInputStream();
			 final var outputStream = connection.getOutputStream()) {

			HttpRequest request = new HttpRequest(inputStream);
			log.info("REQUEST \r\n{}", request);

			final var response = handleHttpRequest(request);
			log.info("RESPONSE \r\n{}", response);

			outputStream.write(response.getBytes());
			outputStream.flush();
		} catch (IOException | UncheckedServletException e) {
			log.error(e.getMessage(), e);
		}
	}

	private HttpResponse handleHttpRequest(HttpRequest httpRequest) throws IOException {
		if (httpRequest.isStaticResourceRequest()) {
			return handleStaticRequest(httpRequest);
		}
		return handleApiRequest(httpRequest);
	}

	private HttpResponse handleStaticRequest(HttpRequest httpRequest) throws IOException {
		try {
			return HttpResponse.OK()
				.responseBody(StaticResourceUtil.getContent(httpRequest.getUrl()))
				.setHeader(Header.CONTENT_TYPE, httpRequest.getContentType().getFormat())
				.build();
		} catch (FileNotFoundException e) {
			return responseErrorPage(NOT_FOUND_HTML, StatusCode.NOT_FOUND.value());
		}
	}

	private HttpResponse handleApiRequest(HttpRequest httpRequest) throws IOException {
		String requestUrl = httpRequest.getUrl();
		if (requestUrl.equals("/")) {
			return HttpResponse.OK()
				.responseBody("Hello world!")
				.setHeader(Header.CONTENT_TYPE, ContentType.HTML.getFormat())
				.build();
		}
		if (HttpMethod.GET.equals(httpRequest.getMethod()) && requestUrl.startsWith("/login")) {
			return HttpResponse.OK()
				.responseBody(StaticResourceUtil.getContent(LOGIN_HTML))
				.setHeader(Header.CONTENT_TYPE, ContentType.HTML.getFormat())
				.build();
		}
		if (HttpMethod.POST.equals(httpRequest.getMethod()) && requestUrl.startsWith("/login")) {
			return checkValidLogin(
				httpRequest.getQueryString("account"),
				httpRequest.getQueryString("password")
			);
		}
		if (HttpMethod.GET.equals(httpRequest.getMethod()) && requestUrl.startsWith("/register")) {
			return HttpResponse.OK()
				.responseBody(StaticResourceUtil.getContent(REGISTER_HTML))
				.setHeader(Header.CONTENT_TYPE, ContentType.HTML.getFormat())
				.build();

		}
		return responseErrorPage(NOT_FOUND_HTML, StatusCode.NOT_FOUND.value());
	}

	private HttpResponse checkValidLogin(String account, String password) throws IOException {
		Optional<User> findUser = InMemoryUserRepository.findByAccount(account);
		if (findUser.isPresent() && findUser.get().checkPassword(password)) {
			return HttpResponse.FOUND()
				.setHeader(Header.LOCATION, "/" + INDEX_HTML)
				.build();
		}
		return responseErrorPage(UNAUTHORIZED_HTML, StatusCode.UNAUTHORIZED.value());
	}

	private HttpResponse responseErrorPage(String htmlPath, int statusCode) throws IOException {
		String responseBody = StaticResourceUtil.getContent(htmlPath);
		return HttpResponse.builder()
			.statusCode(StatusCode.from(statusCode))
			.responseBody(responseBody)
			.setHeader(Header.CONTENT_TYPE, ContentType.HTML.getFormat())
			.build();
	}
}
