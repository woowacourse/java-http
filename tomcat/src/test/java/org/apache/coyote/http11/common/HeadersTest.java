package org.apache.coyote.http11.common;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HeadersTest {
	@DisplayName("HTTP 요청에서 헤더를 성공적으로 파싱한다.")
	@Test
	public void parseRequestHeader() throws IOException {
		String headers = "Host: localhost\n" +
			"Connection: keep-alive\n" +
			"\n";

		BufferedReader reader = new BufferedReader(new StringReader(headers));

		Headers result = Headers.parseRequestHeader(reader);

		Assertions.assertThat("localhost").isEqualTo(result.getValue(HeaderKey.HOST));
		Assertions.assertThat("keep-alive").isEqualTo(result.getValue(HeaderKey.CONNECTION));
	}
}