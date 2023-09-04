package org.apache.coyote.http11.util;

import static org.assertj.core.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class QueryParserTest {

	@Test
	@DisplayName("쿼리를 파싱할 수 있다.")
	void parse() {
		final String query = "account=gugu&password=password&email=hkkang%40woowahan.com";

		final Map<String, String> actual = QueryParser.parse(query);

		final Map<String, String> expected = Map.of(
			"account", "gugu",
			"password", "password",
			"email", "hkkang%40woowahan.com"
		);
		assertThat(actual)
			.containsExactlyInAnyOrderEntriesOf(expected);
	}

}
