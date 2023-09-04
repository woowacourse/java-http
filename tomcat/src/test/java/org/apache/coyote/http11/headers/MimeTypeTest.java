package org.apache.coyote.http11.headers;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MimeTypeTest {

	@Test
	@DisplayName("요청의 endPoint로 mimeType을 확인할 수 있다.")
	void parseEndpointTest() {
		final String endPoint = "index.html";

		final MimeType actual = MimeType.parseEndpoint(endPoint);

		assertThat(actual)
			.isEqualTo(MimeType.HTML);
	}

}
