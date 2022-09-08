package org.apache.coyote.http11.response;

import static org.apache.coyote.http11.common.ContentType.HTML;
import static org.apache.coyote.http11.common.ContentType.UTF;
import static org.apache.coyote.http11.common.HttpHeaderType.CONTENT_LENGTH;
import static org.apache.coyote.http11.common.HttpHeaderType.CONTENT_TYPE;
import static org.apache.coyote.http11.common.HttpHeaderType.LOCATION;
import static org.apache.coyote.http11.common.HttpHeaderType.SET_COOKIE;
import static org.apache.coyote.http11.common.StatusCode.FOUND;
import static org.apache.coyote.http11.common.StatusCode.OK;
import static org.apache.coyote.http11.common.Version.HTTP_1_1;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.UUID;

import org.apache.coyote.http11.common.HttpCookie;
import org.apache.coyote.http11.common.Session;
import org.apache.coyote.http11.common.SessionManager;

import nextstep.jwp.model.User;
import nextstep.jwp.util.IOUtils;

public class HttpResponseFactory {

	private HttpResponseFactory() {
	}

	public static HttpResponse getOKHttpResponse(String fileName) throws IOException, URISyntaxException {
		final String body = IOUtils.readResourceFile(fileName);
		final HttpHeader contentType = HttpHeader.of(CONTENT_TYPE, HTML.getValue(), UTF.getValue());
		final HttpHeader contentLength = HttpHeader.of(CONTENT_LENGTH, String.valueOf(body.getBytes().length));
		return HttpResponse.of(HttpStatusLine.of(HTTP_1_1, OK),
			HttpHeaders.from(contentType, contentLength),
			body);
	}

	public static HttpResponse getOKHttpResponse(String fileName, String type) throws
		IOException,
		URISyntaxException {
		final String body = IOUtils.readResourceFile(fileName);
		final HttpHeader contentType = HttpHeader.of(CONTENT_TYPE, type, UTF.getValue());
		final HttpHeader contentLength = HttpHeader.of(CONTENT_LENGTH, String.valueOf(body.getBytes().length));
		return HttpResponse.of(HttpStatusLine.of(HTTP_1_1, OK),
			HttpHeaders.from(contentType, contentLength),
			body);
	}

	public static HttpResponse getFoundHttpResponse(String redirectUrl) throws
		IOException,
		URISyntaxException {
		final String body = IOUtils.readResourceFile(redirectUrl);
		final HttpHeader location = HttpHeader.of(LOCATION, redirectUrl);
		final HttpHeader contentType = HttpHeader.of(CONTENT_TYPE, HTML.getValue(), UTF.getValue());
		final HttpHeader contentLength = HttpHeader.of(CONTENT_LENGTH, String.valueOf(body.getBytes().length));
		return HttpResponse.of(HttpStatusLine.of(HTTP_1_1, FOUND),
			HttpHeaders.from(location, contentType, contentLength),
			body);
	}

	// public static HttpResponse getLoginHttpResponse(String redirectUrl) throws
	// 	IOException,
	// 	URISyntaxException {
	// 	final String body = IOUtils.readResourceFile(redirectUrl);
	// 	final HttpHeader location = HttpHeader.of(LOCATION, redirectUrl);
	// 	final HttpHeader contentType = HttpHeader.of(CONTENT_TYPE, HTML.getValue(), UTF.getValue());
	// 	final HttpHeader contentLength = HttpHeader.of(CONTENT_LENGTH, String.valueOf(body.getBytes().length));
	// 	final HttpHeader setCookie = HttpHeader.of(SET_COOKIE, HttpCookie.generateCookieValue());
	// 	return HttpResponse.of(HttpStatusLine.of(HTTP_1_1, FOUND),
	// 		HttpHeaders.from(location, contentType, contentLength, setCookie),
	// 		body);
	// }

	public static HttpResponse getLoginHttpResponse(String redirectUrl, User user) throws
		IOException,
		URISyntaxException {
		final String uuid = UUID.randomUUID().toString();
		final Session session = new Session(uuid);
		session.setAttribute(user.getAccount(), user);
		SessionManager.add(session);

		final String body = IOUtils.readResourceFile(redirectUrl);
		final HttpHeader location = HttpHeader.of(LOCATION, redirectUrl);
		final HttpHeader contentType = HttpHeader.of(CONTENT_TYPE, HTML.getValue(), UTF.getValue());
		final HttpHeader contentLength = HttpHeader.of(CONTENT_LENGTH, String.valueOf(body.getBytes().length));
		final HttpHeader setCookie = HttpHeader.of(SET_COOKIE, HttpCookie.generateCookieValue(session.getId()));
		return HttpResponse.of(HttpStatusLine.of(HTTP_1_1, FOUND),
			HttpHeaders.from(location, contentType, contentLength, setCookie),
			body);
	}

}
