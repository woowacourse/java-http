package nextstep.jwp.testutils;

import nextstep.jwp.framework.common.HttpStatusCode;
import nextstep.jwp.framework.message.MessageBody;
import nextstep.jwp.framework.message.response.HttpResponseMessage;

import static org.assertj.core.api.Assertions.assertThat;

public class TestHttpResponseUtils {

    public static void assertStatusCode(HttpResponseMessage httpResponse, HttpStatusCode httpStatusCode) {
        assertThat(httpResponse.statusCode()).isEqualTo(httpStatusCode);
    }

    public static void assertEmptyBody(HttpResponseMessage httpResponse) {
        MessageBody messageBody = httpResponse.getBody();
        assertThat(messageBody.isEmpty()).isTrue();
    }

    public static void assertHeaderIncludes(HttpResponseMessage httpResponse, String key, String value) {
        assertThat(httpResponse.takeHeaderValue(key))
                .get()
                .isEqualTo(value);
    }

    public static void assertContainsBodyString(HttpResponseMessage httpResponse, String body) {
        MessageBody messageBody = httpResponse.getBody();
        assertThat(messageBody.asString()).contains(body);
    }
}
