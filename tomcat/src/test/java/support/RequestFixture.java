package support;

import org.apache.coyote.http11.Http11Processor;

@SuppressWarnings("NonAsciiCharacters")
public class RequestFixture {

	public static StubSocket HTML_요청(String name) {
		final var socket = new StubSocket(String.join("\r\n",
			"GET /" + name + ".html HTTP/1.1 ",
			"Host: localhost:8080 ",
			"Connection: keep-alive ",
			"",
			""
		));
		final Http11Processor processor = new Http11Processor(socket);
		processor.process(socket);
		return socket;
	}

	public static StubSocket 회원가입_패이지_요청() {
		final var socket = new StubSocket(String.join("\r\n",
			"GET /register HTTP/1.1 ",
			"Host: localhost:8080 ",
			"Connection: keep-alive ",
			"",
			""));
		final Http11Processor processor = new Http11Processor(socket);
		processor.process(socket);
		return socket;
	}

	public static StubSocket 회원가입_요청(String account, String password, String email) {
		String body = "account=" + account + "&password=" + password + "&email=" + email;
		final var socket = new StubSocket(
			String.join("\r\n",
				"POST /register HTTP/1.1 ",
				"Host: localhost:8080 ",
				"Connection: keep-alive ",
				"Content-Type: application/x-www-form-urlencoded",
				"Content-Length: " + body.length(),
				"Accept: */*",
				"",
				body
			));
		final Http11Processor processor = new Http11Processor(socket);
		processor.process(socket);
		return socket;
	}

	public static StubSocket 로그인_페이지_요청() {
		final var socket = new StubSocket(
			String.join("\r\n",
				"GET /login HTTP/1.1 ",
				"Host: localhost:8080 ",
				"Connection: keep-alive ",
				"",
				""
			));
		final Http11Processor processor = new Http11Processor(socket);
		processor.process(socket);
		return socket;
	}

	public static StubSocket 로그인_페이지_요청(String jsessionId) {
		final var socket = new StubSocket(
			String.join("\r\n",
				"GET /login HTTP/1.1 ",
				"Host: localhost:8080 ",
				"Connection: keep-alive ",
				"Cookie: JSESSIONID=" + jsessionId,
				"",
				""
			));
		final Http11Processor processor = new Http11Processor(socket);
		processor.process(socket);
		return socket;
	}

	public static StubSocket 로그인_요청(String account, String password) {
		String body = "account=" + account + "&password=" + password;
		final var socket = new StubSocket(
			String.join("\r\n",
				"POST /login HTTP/1.1 ",
				"Host: localhost:8080 ",
				"Connection: keep-alive ",
				"Content-Type: application/x-www-form-urlencoded",
				"Content-Length: " + body.length(),
				"",
				body
			));
		final Http11Processor processor = new Http11Processor(socket);
		processor.process(socket);
		return socket;
	}
}
