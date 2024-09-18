package org.apache.coyote.http11.common;

import static org.apache.coyote.http11.common.Body.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BodyTest {

	@DisplayName("HTTP 요청에서 본문을 성공적으로 파싱한다.")
	@Test
	void parseRequestBody() throws IOException {
		HashMap<String, String> headerMap = new HashMap<>();
		headerMap.put("Content-Length", "5");
		Headers headers = new Headers(headerMap);

		String bodyContent = "Hello";
		BufferedReader reader = new BufferedReader(new StringReader(bodyContent));

		Body body = Body.parseRequestBody(headers, reader);

		Assertions.assertThat(body).isEqualTo(new Body("Hello"));
	}
}