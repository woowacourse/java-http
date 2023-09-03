package org.apache.coyote.request;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.Map;
import org.apache.coyote.common.ContentType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpRequestBodyTest {

    @Nested
    class 바디_추출 {

        @Test
        void JSON_요청이라면_JSON_형식에_맞게_바디를_반환한다() {
            final String jsonBody = "account=gugu&email=gugu@naver.com&password=password";
            final HttpRequestBody httpRequestBody = new HttpRequestBody(jsonBody);

            final Map<String, String> body = httpRequestBody.getBody(ContentType.APPLICATION_JSON);

            assertSoftly(softly -> {
                softly.assertThat(body.get("account")).isEqualTo("gugu");
                softly.assertThat(body.get("email")).isEqualTo("gugu@naver.com");
                softly.assertThat(body.get("password")).isEqualTo("password");
            });
        }

        @Test
        void JSON_요청이_아니라면_body_키를_통해_그대로_반환한다() {
            final String jsonBody = "account=gugu&email=gugu@naver.com&password=password";
            final HttpRequestBody httpRequestBody = new HttpRequestBody(jsonBody);

            final Map<String, String> body = httpRequestBody.getBody(ContentType.TEXT_PLAIN);

            assertThat(body.get("body")).isEqualTo(jsonBody);
        }
    }
}
