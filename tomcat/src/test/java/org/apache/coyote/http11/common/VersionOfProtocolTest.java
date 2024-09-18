package org.apache.coyote.http11.common;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class VersionOfProtocolTest {
	@DisplayName("요청 헤더의 첫번째 라인에서 VersionOfProtocol을 성공적으로 추출한다.")
	@Test
	void parseRequestToPath() {
		String requestLine = "GET / HTTP/1.1";

		VersionOfProtocol versionOfProtocol = VersionOfProtocol.parseReqeustToVersionOfProtocol(requestLine);

		Assertions.assertThat(versionOfProtocol).isEqualTo(new VersionOfProtocol("HTTP/1.1"));
	}
}