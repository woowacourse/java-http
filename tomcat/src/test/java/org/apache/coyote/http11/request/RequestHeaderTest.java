package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;


@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class RequestHeaderTest {

    @Test
    void requestHeader를_입력해_생성한다() {
        // given
        String requestHeader = String.join(
                System.lineSeparator(),
                "Host: localhost:8080 ",
                "Connection: keep-alive "
        );

        // when
        RequestHeader header = RequestHeader.from(requestHeader);

        // then
        assertThat(header).usingRecursiveComparison()
                .isEqualTo(
                        new RequestHeader(Map.of(
                                "HOST", "localhost:8080",
                                "CONNECTION", "keep-alive"
                        ))
                );
    }

    @Test
    void header_field_이름을_입력하면_value_를_반환한다() {
        // given
        String requestHeader = String.join(
                System.lineSeparator(),
                "Host: localhost:8080 ",
                "Connection: keep-alive "
        );
        RequestHeader header = RequestHeader.from(requestHeader);

        // when
        String value = header.get("Host");

        // then
        assertThat(value).isEqualTo("localhost:8080");
    }
}
