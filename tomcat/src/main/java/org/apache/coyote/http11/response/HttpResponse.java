package org.apache.coyote.http11.response;

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

import org.apache.coyote.http11.common.ContentType;
import org.apache.coyote.http11.common.HttpCookie;
import org.apache.coyote.http11.common.Session;
import org.apache.coyote.http11.common.SessionManager;

import nextstep.jwp.model.User;
import nextstep.jwp.util.IOUtils;

public class HttpResponse {

	private static final String CRLF = "\r\n";

	private HttpStatusLine httpStatusLine;
	private HttpHeaders headers;
	private String responseBody;

	public HttpResponse() {
		this.headers = new HttpHeaders();
	}

	public String generateHttpResponse() {
		return String.join(CRLF, httpStatusLine.generateStatusLine(),
			headers.generateHeadersResponse(),
			responseBody);
	}

	public void setResponseBody(String fileName) throws IOException, URISyntaxException {
		responseBody = IOUtils.readResourceFile(fileName);
	}

	public void setResponseBodyContent(String content) {
		responseBody = content;
	}

	public void setOkHttpStatusLine() {
		this.httpStatusLine = HttpStatusLine.of(HTTP_1_1, OK);
	}

	public void setFoundHttpStatusLine() {
		this.httpStatusLine = HttpStatusLine.of(HTTP_1_1, FOUND);
	}

	public void setRedirectUrl(String redirectUrl) {
		headers.add(HttpHeader.of(LOCATION, redirectUrl));
	}

	public void setCookie(String sessionId) {
		headers.add(HttpHeader.of(SET_COOKIE, HttpCookie.generateCookieValue(sessionId)));
	}

	public void setOKHeader(ContentType fileType) {
		final HttpHeader contentType = HttpHeader.of(CONTENT_TYPE, fileType.getValue(), UTF.getValue());
		final HttpHeader contentLength = HttpHeader.of(CONTENT_LENGTH, String.valueOf(responseBody.getBytes().length));
		headers.add(contentType, contentLength);
	}

	public void setOkResponse(String fileName) throws
		IOException,
		URISyntaxException {
		setResponseBody(fileName);
		setOkHttpStatusLine();
		setOKHeader(ContentType.from(fileName));
	}

	public void setSessionAndCookieWithOkResponse(User user, String url) throws IOException, URISyntaxException {
		final Session session = setSession(user);
		setFoundHttpStatusLine();
		setResponseBody(url);
		setRedirectUrl(url);
		setOKHeader(ContentType.from(url));
		setCookie(session.getId());
	}

	public void setFoundResponse(String fileName) throws IOException, URISyntaxException {
		setResponseBody(fileName);
		setFoundHttpStatusLine();
		setOKHeader(ContentType.from(fileName));
	}

	private Session setSession(User succeedLoginUser) {
		final String uuid = UUID.randomUUID().toString();
		final Session session = new Session(uuid);
		session.setAttribute(succeedLoginUser.getAccount(), succeedLoginUser);
		SessionManager.add(session);
		return session;
	}

}
