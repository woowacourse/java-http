package support;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.HttpRequest;

public class HttpRequestFixture {

	public static HttpRequest createGetMethod(String uri) throws IOException {
		String requestString = String.join("\r\n",
			"GET " + uri + " HTTP/1.1 ",
			"Host: localhost:8080 ",
			"Connection: keep-alive ",
			"",
			"");
		return HttpRequest.from(new ByteArrayInputStream(requestString.getBytes()));
	}

	public static HttpRequest createGetMethodWithSessionId(String uri, String sessionId) throws IOException {
		String requestString = String.join("\r\n",
			"GET " + uri + " HTTP/1.1 ",
			"Host: localhost:8080 ",
			"Connection: keep-alive ",
			"Cookie: JSESSIONID="+sessionId,
			"",
			"");
		return HttpRequest.from(new ByteArrayInputStream(requestString.getBytes()));
	}

	public static HttpRequest createLoginPostMethod() throws IOException {
		String requestString = String.join("\r\n",
			"POST /login HTTP/1.1 ",
			"Host: localhost:8080 ",
			"Connection: keep-alive ",
			"Content-Length: 30 ",
			"",
			"account=gugu&password=password");
		return HttpRequest.from(new ByteArrayInputStream(requestString.getBytes()));
	}

	public static HttpRequest createLoginPostMethodWithInvalidPassword() throws IOException {
		String requestString = String.join("\r\n",
			"POST /login HTTP/1.1 ",
			"Host: localhost:8080 ",
			"Connection: keep-alive ",
			"Content-Length: 30 ",
			"",
			"account=gugu&password=passwrod");
		return HttpRequest.from(new ByteArrayInputStream(requestString.getBytes()));
	}

	public static HttpRequest createRegisterPostMethod() throws IOException {
		String requestString = String.join("\r\n",
			"POST /register HTTP/1.1 ",
			"Host: localhost:8080 ",
			"Connection: keep-alive ",
			"Content-Length: 60 ",
			"",
			"account=gugugu&password=password&email=hkkang%40woowahan.com");
		return HttpRequest.from(new ByteArrayInputStream(requestString.getBytes()));
	}

	public static HttpRequest createRegisterPostMethodWithAlreadyExistAccount() throws IOException {
		String requestString = String.join("\r\n",
			"POST /register HTTP/1.1 ",
			"Host: localhost:8080 ",
			"Connection: keep-alive ",
			"Content-Length: 58 ",
			"",
			"account=gugu&password=password&email=hkkang%40woowahan.com");
		return HttpRequest.from(new ByteArrayInputStream(requestString.getBytes()));
	}

	private HttpRequestFixture() {
	}
}
