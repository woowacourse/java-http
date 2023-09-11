package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.*;

import org.apache.coyote.response.StatusCode;
import org.apache.coyote.response.StatusLine;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class Http11StatusLineGenerateTest {

	@Test
	void StatusLine을_생성한다() {
		final var statusLine = new StatusLine(StatusCode.OK);

		final var generate = Http11StatusLineGenerate.generate(statusLine);

		assertThat(generate).isEqualTo("HTTP/1.1 200 OK ");
	}
}
