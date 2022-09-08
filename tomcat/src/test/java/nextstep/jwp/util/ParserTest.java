package nextstep.jwp.util;

import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ParserTest {

	@DisplayName("url 부분에서 Param 가 정상적으로 파싱된다.")
	@Test
	void findParam() {
		// given
		String url = "/login?account=gugu&password=password";

		// when
		final Map<String, String> param = Parser.findParam(url);

		// then
		Assertions.assertThat(param.size()).isEqualTo(2);
	}

	@DisplayName("url 에서 파일 타입을 추출한다.")
	@Test
	void parseFileType() {
		// given
		String url = "/login.html?account=gugu&password=password";

		// when
		final String path = Parser.parseFileType(url);

		// then
		Assertions.assertThat(path).isEqualTo("html");

	}

	@DisplayName("url 에서 파일 이름을 추출한다.")
	@Test
	void convertResourceFileName() {
		// given
		String url = "/login.html?account=gugu&password=password";

		// when
		final String path = Parser.convertResourceFileName(url);

		// then
		Assertions.assertThat(path).isEqualTo("/login.html");

	}
}
