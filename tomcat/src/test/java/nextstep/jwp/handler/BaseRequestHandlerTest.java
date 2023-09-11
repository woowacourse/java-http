package nextstep.jwp.handler;

import static org.assertj.core.api.SoftAssertions.*;

import org.apache.coyote.HttpMethod;
import org.apache.coyote.HttpProtocolVersion;
import org.apache.coyote.request.Request;
import org.apache.coyote.request.RequestBody;
import org.apache.coyote.request.RequestHeader;
import org.apache.coyote.request.RequestLine;
import org.apache.coyote.request.RequestUri;
import org.apache.coyote.response.Response;
import org.apache.coyote.response.StatusCode;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class BaseRequestHandlerTest {

	@Test
	void get요청시_HelloWorld반환() {
		// given
		final var baseRequestHandler = new BaseRequestHandler();
		final var requestLine = new RequestLine(HttpMethod.GET, new RequestUri("/"), HttpProtocolVersion.HTTP11);
		final var request = new Request(requestLine, RequestHeader.empty(), RequestBody.empty());
		final var response = new Response();

		// when
		baseRequestHandler.doGet(request, response);

		// then
		assertSoftly(softly -> {
			softly.assertThat(response.getStatusLine().getCode()).isEqualTo(StatusCode.OK);
			softly.assertThat(response.getResponseBody()).isEqualTo("Hello world!");
		});
	}
}
