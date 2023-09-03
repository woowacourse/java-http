package org.apache.coyote.http11.headers;

import static org.assertj.core.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpCookieTest {

	@Test
	@DisplayName("cookie의 value가 들어오면 파싱하여 저장한다.")
	void createHttpCookie() {
		final String cookieValue
			= "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46";

		final HttpCookie actual = HttpCookie.from(cookieValue);

		final Map<String, String> expected = Map.of(
			"yummy_cookie", "choco",
			"tasty_cookie", "strawberry",
			"JSESSIONID", "656cef62-e3c4-40bc-a8df-94732920ed46"
		);
		assertThat(actual.getCookies())
			.containsExactlyInAnyOrderEntriesOf(expected);
	}

	@Test
	@DisplayName("jssession id가 헤더에 존재하는지 확인한다.")
	void isExistJSessionId() {
		final String cookieValue
			= "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46";
		final HttpCookie cookie = HttpCookie.from(cookieValue);

		assertThat(cookie.isExistJSessionId())
			.isTrue();
	}
}
