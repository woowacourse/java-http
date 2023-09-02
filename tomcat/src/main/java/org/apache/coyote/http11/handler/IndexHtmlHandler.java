package org.apache.coyote.http11.handler;

import static org.apache.coyote.http11.response.HttpStatusCode.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusCode;

public class IndexHtmlHandler implements HttpHandler {

	private final String endPoint = "/index.html";
	private final String fileName = "static/index.html";

	@Override
	public boolean isSupported(final HttpRequest request) {
		return request.getEndPoint().equals(endPoint);
	}

	@Override
	public HttpResponse handleTo(final HttpRequest request) {
		try {
			return new HttpResponse(
				OK_200,
				resolveBody()
			);
		} catch (final IOException e) {
			//에러메시지 추상화 방법 고민하기
			return new HttpResponse(
				HttpStatusCode.INTERNAL_SERVER_ERROR_500,
				"예상치 못한 에러가 발생했습니다."
			);
		}
	}

	private String resolveBody() throws IOException {
		final String pathValue = getClass().getClassLoader().getResource(fileName).getPath();
		final Path path = new File(pathValue).toPath();
		final String body = new String(Files.readAllBytes(path));

		return String.join(System.lineSeparator(),
			"Content-Type: text/html;charset=utf-8 ",
			String.format("Content-Length: %d ", body.getBytes().length),
			"",
			body
		);
	}
}
