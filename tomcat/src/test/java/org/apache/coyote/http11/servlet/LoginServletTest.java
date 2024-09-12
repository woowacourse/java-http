package org.apache.coyote.http11.servlet;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.apache.coyote.http11.common.VersionOfProtocol;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StatusCode;
import org.apache.coyote.http11.response.StatusMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginServletTest {

	@DisplayName("로그인 POST 요청에 성공한다.")
	@Test
	void loginSuccess() throws IOException {
		String requestData = "POST /login HTTP/1.1\r\n" +
			"Content-Length: 30\r\n\r\n" +
			"account=gugu&password=password";

		BufferedReader reader = new BufferedReader(new StringReader(requestData));
		HttpRequest request = new HttpRequest(reader);
		HttpResponse response = new HttpResponse();

		LoginServlet loginServlet = new LoginServlet();
		loginServlet.doPost(request, response);

		assertThat(new VersionOfProtocol("HTTP/1.1")).isEqualTo(response.getVersionOfProtocol());
		assertThat(new StatusCode(302)).isEqualTo(response.getStatusCode());
		assertThat(new StatusMessage("Found")).isEqualTo(response.getStatusMessage());
		assertThat(response.getHeaders().getValue("Set-Cookie")).isNotNull();
		assertThat("http://localhost:8080/index.html").isEqualTo(response.getHeaders().getValue("Location"));
	}

	@DisplayName("로그인 페이지 조회에 성공한다.")
	@Test
	void loginPageSuccess() throws IOException {
		String requestData = "GET /login HTTP/1.1\r\n";

		BufferedReader reader = new BufferedReader(new StringReader(requestData));
		HttpRequest request = new HttpRequest(reader);
		HttpResponse response = new HttpResponse();

		LoginServlet loginServlet = new LoginServlet();
		loginServlet.doGet(request, response);

		assertThat(new VersionOfProtocol("HTTP/1.1")).isEqualTo(response.getVersionOfProtocol());
		assertThat(new StatusCode(200)).isEqualTo(response.getStatusCode());
		assertThat(new StatusMessage("OK")).isEqualTo(response.getStatusMessage());
	}
}