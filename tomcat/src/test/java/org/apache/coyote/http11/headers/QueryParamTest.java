package org.apache.coyote.http11.headers;

import static org.assertj.core.api.Assertions.*;

import java.util.Map;
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
				"key1", "value1",
				"key2", "value2"
			)
		);
		assertThat(actual)
			.usingRecursiveComparison()
			.isEqualTo(expected);
	}

	@Test
	@DisplayName("key값으로 value를 조회할 수 있다.")
	void get() {
		final String queryKey = "key1";
		final String queryValue = "value1";
		final QueryParam queryParam = new QueryParam(Map.of(
			queryKey, queryValue
		));

		final String actual = queryParam.get(queryKey);

		assertThat(actual)
			.isEqualTo(queryValue);
	}
}
