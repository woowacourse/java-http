package org.apache.coyote.http11.request;

import static org.assertj.core.api.SoftAssertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class Http11RequestHeaderParserTest {

	@Test
	void RequestHeader_파싱() {
		final var requestHeaders = List.of(
			"Host: localhost:8080",
			"Connection: keep-alive",
			"Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46"
		);

		final var parsed = Http11RequestHeaderParser.parse(requestHeaders);

		assertSoftly(softly -> {
			softly.assertThat(parsed.find("Host")).isEqualTo("localhost:8080");
			softly.assertThat(parsed.find("Connection")).isEqualTo("keep-alive");
			softly.assertThat(parsed.findCookie("yummy_cookie").get()).isEqualTo("choco");
			softly.assertThat(parsed.findSessionId().get()).isEqualTo("656cef62-e3c4-40bc-a8df-94732920ed46");
		});
	}
}
