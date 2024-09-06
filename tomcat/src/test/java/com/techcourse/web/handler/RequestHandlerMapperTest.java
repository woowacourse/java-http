package com.techcourse.web.handler;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class RequestHandlerMapperTest {

	@DisplayName("/login 경로에 대한 핸들러를 찾는다.")
	@ParameterizedTest
	@ValueSource(strings = {"/login", "/login?account=account", "/login/1"})
	void findLoginHandler(String value) {

		RequestHandler handler = RequestHandlerMapper.getInstance().findHandler(value);

		assertThat(handler).isInstanceOf(LoginHandler.class);
	}

	@DisplayName("/ 경로에 대한 핸들러를 찾는다.")
	@Test
	void findPageHandler() {
		RequestHandler handler = RequestHandlerMapper.getInstance().findHandler("/");

		assertThat(handler).isInstanceOf(PageHandler.class);
	}

	@DisplayName("정적 파일에 대한 핸들러를 찾는다.")
	@ParameterizedTest
	@ValueSource(strings = {"/login.html", "/index.html", "/style.css", "/script.js", "/favicon.ico"})
	void findStaticFileHandler(String value) {
		RequestHandler handler = RequestHandlerMapper.getInstance().findHandler(value);

		assertThat(handler).isInstanceOf(PageHandler.class);
	}
}
