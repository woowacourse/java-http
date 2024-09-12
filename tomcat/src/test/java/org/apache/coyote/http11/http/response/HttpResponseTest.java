package org.apache.coyote.http11.http.response;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

	@DisplayName("Body가 있을 때 응답 메시지를 생성한다.")
	@Test
	void toResponseMessage_WithBody() {
		HttpResponse response = new HttpResponse();
		response.setStatusCode(HttpStatusCode.OK);
		response.setBody("text/html", "Hello world!".getBytes());

		String expected = """
		HTTP/1.1 200 OK \r
		Content-Type: text/html;charset=utf-8 \r
		Content-Length: 12 \r
		\r
		Hello world!""";

		assertThat(response.toResponseMessage()).isEqualTo(expected);
	}

	@DisplayName("Body가 없을 때 응답 메시지를 생성한다.")
	@Test
	void toResponseMessage_WithoutBody() {
		HttpResponse response = new HttpResponse();
		response.setStatusCode(HttpStatusCode.OK);

		String expected = """
		HTTP/1.1 200 OK \r
		\r
		""";

		assertThat(response.toResponseMessage()).isEqualTo(expected);
	}
}
