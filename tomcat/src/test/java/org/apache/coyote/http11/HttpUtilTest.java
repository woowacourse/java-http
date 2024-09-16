package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpUtilTest {

	@DisplayName("쿼리스트링을 파싱한다")
	@Test
	void parseQueryString() {
		// given
		String queryString = "account=gugu&password=password";
		// when
		Map<String, String> parsedQueryString = HttpUtil.parseQueryString(queryString);
		//then
		assertThat(parsedQueryString).containsEntry("account", "gugu");
		assertThat(parsedQueryString).containsEntry("password", "password");
	}
}
