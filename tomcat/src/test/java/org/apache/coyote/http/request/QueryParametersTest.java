package org.apache.coyote.http.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class QueryParametersTest {

    @Test
    void fromUrlContent_메서드는_유효한_url을_전달하면_QueryParameters를_초기화한다() {
        final String validUrlContent = "/login?user=gugu&password=password";

        final Parameters actual = Parameters.fromUrlContent(validUrlContent);

        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(actual).isExactlyInstanceOf(Parameters.class);
            softAssertions.assertThat(actual.size()).isEqualTo(2);
            softAssertions.assertThat(actual.findValue("user")).isEqualTo("gugu");
            softAssertions.assertThat(actual.findValue("password")).isEqualTo("password");
        });
    }

    @Test
    void fromUrlContent_메서드는_유효하지_않은_url을_전달하면_빈_QueryParmaeter를_반환한다() {
        final String InvalidUrlContent = "/login";

        final Parameters actual = Parameters.fromUrlContent(InvalidUrlContent);

        assertThat(actual.size()).isZero();
    }

    @Test
    void fromBodyContent_메서드는_유효한_바디를_전달하면_QueryParameters를_초기화한다() {
        final String validBodyContent = "user=gugu&password=password";

        final Parameters actual = Parameters.fromBodyContent(validBodyContent);

        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(actual).isExactlyInstanceOf(Parameters.class);
            softAssertions.assertThat(actual.findValue("user")).isEqualTo("gugu");
            softAssertions.assertThat(actual.findValue("password")).isEqualTo("password");
        });
    }

    @Test
    void fromBodyContent_메서드는_유효하지_않은_바디을_전달하면_빈_QueryParmaeter를_반환한다() {
        final String InvalidUrlContent = "{\"user\" : \"gugu\"}";

        final Parameters actual = Parameters.fromBodyContent(InvalidUrlContent);

        assertThat(actual.size()).isZero();
    }

    @Test
    void size_메서드는_호출하면_query_paramter의_수를_반환한다() {
        final Parameters parameters = Parameters.EMPTY;

        final int actual = parameters.size();

        assertThat(actual).isZero();
    }
}
