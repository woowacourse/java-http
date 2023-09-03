package org.apache.coyote.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.coyote.http.util.HeaderDto;
import org.apache.coyote.http.util.exception.InvalidHeaderContentException;
import org.apache.coyote.http.util.exception.InvalidHeaderSizeException;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpHeadersTest {

    @Test
    void from_headerContents_메서드는_유효한_헤더를_전달하면_HttpHeaders를_초기화한다() {
        final String headerContents = "Content-Type: application/json";

        final HttpHeaders actual = HttpHeaders.from(headerContents);

        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(actual).isNotNull();
            softAssertions.assertThat(actual.findValue("Content-Type")).isEqualTo("application/json");
        });
    }

    @Test
    void from_headerContents_메서드는_유효하지_않은_헤더를_전달하면_예외가_발생한다() {
        final String invalidHeaderContents = "Content-Type: ";

        assertThatThrownBy(() -> HttpHeaders.from(invalidHeaderContents))
                .isInstanceOf(InvalidHeaderContentException.class)
                .hasMessageContaining("유효한 헤더 값이 아닙니다.");
    }

    @Test
    void 생성자는_헤더를_전달하면_HttpHeaders를_초기화한다() {
        final Map<String, String> headers = Map.of("Content-Type", "application/json");

        final HttpHeaders actual = new HttpHeaders(headers);

        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(actual).isNotNull();
            softAssertions.assertThat(actual.findValue("Content-Type")).isEqualTo("application/json");
        });
    }

    @Test
    void 생성자는_유효하지_않은_수의_헤더를_전달하면_예외가_발생한다() {
        final Map<String, String> invalidHeaders = Collections.emptyMap();

        assertThatThrownBy(() -> new HttpHeaders(invalidHeaders))
                .isInstanceOf(InvalidHeaderSizeException.class)
                .hasMessageContaining("유효한 헤더 크기가 아닙니다.");
    }

    @Test
    void from_headerDtos_메서드는_headerDtos를_전달하면_HttpHeaders를_초기화한다() {
        final HeaderDto headerDto = new HeaderDto("Content-Type", "application/json");

        final HttpHeaders actual = HttpHeaders.from(List.of(headerDto));

        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(actual).isNotNull();
            softAssertions.assertThat(actual.findValue("Content-Type")).isEqualTo("application/json");
        });
    }

    @Test
    void findValue_메서드는_key를_전달하면_value를_반환한다() {
        final HttpHeaders headers = HttpHeaders.from("Content-Type: application/json");

        final String actual = headers.findValue("Content-Type");

        assertThat(actual).isEqualTo("application/json");
    }

    @Test
    void convertHeaders_메서드는_호출하면_헤더를_HTTP_메세지_형식으로_반환한다() {
        final String header = "Content-Type: application/json ";
        final HttpHeaders headers = HttpHeaders.from(header);

        final String actual = headers.convertHeaders();

        assertThat(actual).isEqualTo(header);
    }
}
