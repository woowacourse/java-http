package com.techcourse.web.util;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FormUrlEncodedParserTest {

	@DisplayName("Form Url Encoded 문자열을 파싱한다.")
	@Test
	void parse() {
		String body = "name=abc&password=1234";

		Map<String, String> result = FormUrlEncodedParser.parse(body);

		assertThat(result).containsEntry("name", "abc").containsEntry("password", "1234");
	}

	@DisplayName("입력된 문자열이 없는 경우 예외를 던진다.")
	@Test
	void parse_WithEmptyInput() {
	    String body = "";

		assertThatThrownBy(() -> FormUrlEncodedParser.parse(body))
		    .isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Body is empty");
	}

	@DisplayName("Key만 존재하면 빈 문자열로 Value를 반환한다.")
	@Test
	void parse_WithEmptyValue() {
	    String body = "name=&password=1234";

		Map<String, String> result = FormUrlEncodedParser.parse(body);

		assertThat(result).containsEntry("name", "").containsEntry("password", "1234");
	}
}
