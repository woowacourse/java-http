package org.apache.coyote.http11.response;

import static org.assertj.core.api.SoftAssertions.*;

import org.apache.coyote.Cookie;
import org.apache.coyote.MimeType;
import org.apache.coyote.response.ResponseHeader;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class Http11ResponseHeaderGeneratorTest {

	@Test
	void ResponseHeader를_생성한다() {
		final var responseHeader = new ResponseHeader();
		responseHeader.addContentType(MimeType.HTML);
		responseHeader.addContentLength("abc");
		responseHeader.addCookie(new Cookie("cookie1", "abc"));
		responseHeader.addCookie(new Cookie("cookie2", "dfg"));

		final var generate = Http11ResponseHeaderGenerator.generate(responseHeader);

		assertSoftly(softly -> {
			softly.assertThat(generate).contains("Content-Type: text/html;charset=utf-8");
			softly.assertThat(generate).contains("Content-Length: 3");
			softly.assertThat(generate).contains("Set-Cookie: cookie1=abc; cookie2=dfg;");
		});
	}
}
