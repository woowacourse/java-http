package org.apache.coyote.http11.request;

import static org.apache.coyote.HttpMethod.*;
import static org.assertj.core.api.SoftAssertions.*;

import org.apache.coyote.HttpProtocolVersion;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class Http11RequestLineParserTest {

	@Test
	void RequestLine을_올바르게_파싱한다() {
		final var requestLine = "GET /login?account=gugu&password=pw HTTP/1.1";

		final var parsed = Http11RequestLineParser.parse(requestLine);

		assertSoftly(softly -> {
			softly.assertThat(parsed.hasMethod(GET)).isTrue();
			softly.assertThat(parsed.findQueryParam("account")).isEqualTo("gugu");
			softly.assertThat(parsed.findQueryParam("password")).isEqualTo("pw");
			softly.assertThat(parsed.getVersion()).isEqualTo(HttpProtocolVersion.HTTP11);
		});
	}
}
