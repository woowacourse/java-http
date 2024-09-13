package org.apache.coyote.http11.servlet;

import static org.apache.coyote.http11.common.HeaderKey.*;
import static org.assertj.core.api.Assertions.*;

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

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

class RegisterServletTest {

	@DisplayName("회원가입에 성공한다.")
	@Test
	void registerSuccess() throws IOException {
		String requestData = "POST /login HTTP/1.1\r\n" +
			"Content-Length: 60\r\n\r\n" +
			"account=testAccount&mail=test@mail.com&password=testPassword";
		BufferedReader reader = new BufferedReader(new StringReader(requestData));

		HttpRequest request = new HttpRequest(reader);
		HttpResponse response = new HttpResponse();

		RegisterServlet servlet = new RegisterServlet();
		servlet.doPost(request, response);

		User expectedUser = new User("testAccount", "test@mail.com", "testPassword");
		User savedUser = InMemoryUserRepository.findByAccount("testAccount").orElse(null);
		assertThat(savedUser).isNotNull();
		assertThat(expectedUser).isEqualTo(savedUser);

		assertThat(response.getVersionOfProtocol()).isEqualTo(new VersionOfProtocol("HTTP/1.1"));
		assertThat(response.getStatusCode()).isEqualTo(new StatusCode(302));
		assertThat(response.getStatusMessage()).isEqualTo(new StatusMessage("Found"));

		assertThat(response.getHeaders().getValue(LOCATION)).isNotNull();
		assertThat(response.getHeaders().getValue(LOCATION)).isEqualTo("http://localhost:8080/index.html");
	}

	@DisplayName("회원가입 페이지를 조회에 성공한다.")
	@Test
	void registerFailure() throws IOException {
		String requestData = "GET /register HTTP/1.1\r\n";
		BufferedReader reader = new BufferedReader(new StringReader(requestData));

		HttpRequest request = new HttpRequest(reader);
		HttpResponse response = new HttpResponse();

		RegisterServlet servlet = new RegisterServlet();
		servlet.doGet(request, response);

		assertThat(response.getVersionOfProtocol()).isEqualTo(new VersionOfProtocol("HTTP/1.1"));
		assertThat(response.getStatusCode()).isEqualTo(new StatusCode(200));
		assertThat(response.getStatusMessage()).isEqualTo(new StatusMessage("OK"));
	}
}