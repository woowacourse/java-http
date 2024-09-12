package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.coyote.http11.http.request.HttpMethod;
import org.apache.coyote.http11.http.request.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestMessageReaderTest {

	@DisplayName("Http 요청 메시지를 읽는다.")
	@Test
	void read() throws IOException {
		String body = """
			{
			  "name": "John Doe",
			  "age": 30,
			  "email": "john.doe@example.com",
			  "isActive": true
			}""";

		String httpRequest = String.join("\r\n",
			"POST /api/endpoint?hi=hello&bangga= HTTP/1.1 ",
			"Host: example.com ",
			"Content-Length: " + body.length(),
			"Content-Type: application/json ",
			"Accept: application/json, text/plain, */* ",
			"",
			body);

		InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());
		HttpRequest request = HttpRequestMessageReader.read(inputStream);

		System.out.println(request.getRequestBody());
		assertThat(request.getRequestLine()).satisfies(
			requestLine -> {
				assertThat(requestLine.getMethod()).isEqualTo(HttpMethod.POST);
				assertThat(requestLine.getRequestPath()).isEqualTo("/api/endpoint");
				assertThat(requestLine.getHttpVersion()).isEqualTo("HTTP/1.1");
			}
		);
		assertThat(request.getHeaders()).satisfies(
			headers -> {
				assertThat(headers.getValue("Host")).containsOnly("example.com");
				assertThat(headers.getValue("Content-Length")).containsOnly(String.valueOf(body.length()));
				assertThat(headers.getValue("Content-Type")).containsOnly("application/json");
				assertThat(headers.getValue("Accept")).containsExactly("application/json", "text/plain", "*/*");
			}
		);
		assertThat(request.getRequestBody()).contains("name\": \"John Doe", "age\": 30",
			"email\": \"john.doe@example.com",
			"isActive\": true");
	}

	@DisplayName("Content-Length가 0인 경우 빈 body를 반환한다.")
	@Test
	void readEmptyBody() throws IOException {
		String httpRequest = String.join("\r\n",
			"GET /api/endpoint?hi=hello&bangga= HTTP/1.1 ",
			"Host: example.com ",
			"Content-Length: 0",
			"Content-Type: application/json ",
			"Accept: application/json, text/plain, */* ",
			"");

		InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());
		HttpRequest request = HttpRequestMessageReader.read(inputStream);

		assertThat(request.getRequestBody()).isEmpty();
	}

	@DisplayName("Content-Length와 실제 body의 길이가 다른 경우 예외를 던진다.")
	@Test
	void readMismatchContentLength() {
		String body = "Hello world!";
		String httpRequest = String.join("\r\n",
			"POST /api/endpoint?hi=hello&bangga= HTTP/1.1 ",
			"Host: example.com ",
			"Content-Length: " + (body.length() + 1),
			"Content-Type: application/json ",
			"Accept: application/json, text/plain, */* ",
			"",
			body);

		InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());
		assertThatThrownBy(() -> HttpRequestMessageReader.read(inputStream))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Content-Length mismatch");
	}
}
