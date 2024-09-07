package servlet.http.request;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.Test;

class RequestBodyTest {

    @Test
    void RequestBody_객체를_생성한다() {
        // given
        String requestBody = "account=prin&password=1q2w3e4r!";

        // when
        RequestBody actual = RequestBody.from(requestBody);

        // then
        assertSoftly(softly -> {
            softly.assertThat(actual.getValue("account")).isEqualTo("prin");
            softly.assertThat(actual.getValue("password")).isEqualTo("1q2w3e4r!");
        });
    }

    @Test
    void RequestBody의_key가_존재하지_않을_경우_예외가_발생한댜() {
        // given
        String queryParams = "account=prin&password=1q2w3e4r!";

        // when
        RequestBody actual = RequestBody.from(queryParams);

        // then
        assertThatThrownBy(() -> actual.getValue("name"))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Request body가 존재하지 않습니다.");
    }
}
