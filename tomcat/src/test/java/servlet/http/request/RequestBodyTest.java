package servlet.http.request;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.Test;

class RequestBodyTest {

    @Test
    void RequestBody_객체를_생성한다() {
        // given
        String bodies = "account=prin&password=1q2w3e4r!";

        // when
        RequestBody actual = RequestBody.from(bodies);

        // then
        assertSoftly(softly -> {
            softly.assertThat(actual.getValue("account")).isEqualTo("prin");
            softly.assertThat(actual.getValue("password")).isEqualTo("1q2w3e4r!");
        });
    }

    @Test
    void key_value_구분자가_없는_경우_예외가_발생한다() {
        // given
        String bodies = "account=prin&password&team=ddangkong";

        // when & then
        assertThatThrownBy(() -> RequestBody.from(bodies))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("잘못된 Body입니다. body: 'password'");
    }

    @Test
    void key_value_구분자가_두_개_이상일_경우_예외가_발생한다() {
        // given
        String bodies = "account=prin&password=1q2w3e4r!=1";

        // when & then
        assertThatThrownBy(() -> RequestBody.from(bodies))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("잘못된 Body입니다. body: 'password=1q2w3e4r!=1'");
    }

    @Test
    void query_param이_비어있을_경우_예외가_발생한다() {
        // given
        String bodies = "account=prin&&password=1q2w3e4r!";

        // when & then
        assertThatThrownBy(() -> RequestBody.from(bodies))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("잘못된 Body입니다. body: ''");
    }

    @Test
    void key가_비어있을_경우_예외가_발생한다() {
        // given
        String bodies = "account=prin&=1q2w3e4r!";

        // when & then
        assertThatThrownBy(() -> RequestBody.from(bodies))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("key 또는 value가 비어있습니다. key: '', value: '1q2w3e4r!'");
    }

    @Test
    void value가_비어있을_경우_예외가_발생한다() {
        // given
        String bodies = "account=prin&password=";

        // when & then
        assertThatThrownBy(() -> RequestBody.from(bodies))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("key 또는 value가 비어있습니다. key: 'password', value: ''");
    }

    @Test
    void key_value가_비어있을_경우_예외가_발생한다() {
        // given
        String bodies = "account=prin&=";

        // when & then
        assertThatThrownBy(() -> RequestBody.from(bodies))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("key 또는 value가 비어있습니다. key: '', value: ''");
    }

    @Test
    void RequestBody가_비어있을_경우_key를_조회할_때_예외가_발생한다() {
        // given
        RequestBody bodies = RequestBody.from(null);

        // when & then
        assertThatThrownBy(() -> bodies.getValue("any"))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Request body가 비어있습니다.");
    }

    @Test
    void RequestBody의_key가_존재하지_않을_경우_예외가_발생한댜() {
        // given
        String bodies = "account=prin&password=1q2w3e4r!";

        // when
        RequestBody actual = RequestBody.from(bodies);

        // then
        assertThatThrownBy(() -> actual.getValue("name"))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Request body에 해당 key가 존재하지 않습니다. key: name");
    }
}
