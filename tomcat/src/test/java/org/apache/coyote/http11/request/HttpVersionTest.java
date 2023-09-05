package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.*;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpVersionTest {

	@Test
	@DisplayName("http version 이름으로 객체를 반환할 수 있다.")
	void createHttpVersion() {
		final String str = "HTTP/1.1";

		final HttpVersion version = HttpVersion.from(str);

		assertThat(version)
			.isEqualTo(HttpVersion.HTTP_1_1);
	}

	@Test
	@DisplayName("없는 형태의 입력인 경우 예외를 반환한다.")
	void noSuchException() {
		final String str = "HTTP/0.0";

		assertThatThrownBy(() -> HttpVersion.from(str))
			.isInstanceOf(NoSuchElementException.class);
	}

}
