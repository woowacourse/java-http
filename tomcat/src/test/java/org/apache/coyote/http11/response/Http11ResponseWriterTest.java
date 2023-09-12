package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.*;

import java.io.BufferedWriter;

import org.apache.coyote.response.Response;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class Http11ResponseWriterTest {

	@Test
	void statusLine이_null일_때_응답으로_바꾸려고하면_예외_발생() {
		final var response = new Response();
		final var writer = new Http11ResponseWriter(Mockito.mock(BufferedWriter.class));

		assertThatThrownBy(() -> writer.write(response))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("올바르지 않은 Response 입니다.");
	}
}
