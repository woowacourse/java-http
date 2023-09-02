package org.apache.coyote.http11.headers;

import static org.assertj.core.api.Assertions.*;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.coyote.http11.request.QueryParam;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class QueryParamTest {

	@Test
	@DisplayName("엔드포인트로 queryParam을 생성할 수 있다.")
	void createQueryParam() {
		final Map<String, String> queryParams = Map.of(
			"key1", "value1",
			"key2", "value2"
		);
		final String queryString = queryParams.entrySet()
			.stream()
			.map(entry -> String.format("%s=%s", entry.getKey(), entry.getValue()))
			.collect(Collectors.joining("&"));
		final String endPoint = "/index.html?" + queryString;

		final QueryParam actual = QueryParam.from(endPoint);

		final QueryParam expected = new QueryParam(
			Map.of(
				"key1", Set.of("value1"),
				"key2", Set.of("value2")
			)
		);
		assertThat(actual)
			.usingRecursiveComparison()
			.isEqualTo(expected);
	}
}
