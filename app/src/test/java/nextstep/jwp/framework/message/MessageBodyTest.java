package nextstep.jwp.framework.message;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MessageBodyTest {

    @DisplayName("MessageBody 를 생성한다.")
    @Test
    void create() {
        String ggyool = "ggyool";
        byte[] bytes = {103, 103, 121, 111, 111, 108};

        MessageBody messageBody = MessageBody.from(ggyool);
        assertThat(messageBody.getBytes()).isEqualTo(bytes);
    }

    @DisplayName("비어있는 MessageBody 를 생성한다.")
    @Test
    void createEmpty() {
        MessageBody emptyBody = MessageBody.empty();
        MessageBody emptyBodyFromBytes = MessageBody.from(new byte[0]);
        MessageBody emptyBodyFromString = MessageBody.from("");

        assertThat(emptyBody.getBytes()).isEqualTo(new byte[0]);
        assertThat(emptyBody)
                .isSameAs(emptyBodyFromBytes)
                .isSameAs(emptyBodyFromString);
    }

    @DisplayName("비어있는 MessageBody 이면 참을 반환한다.")
    @Test
    void isEmpty() {
        MessageBody messageBody = MessageBody.empty();
        assertThat(messageBody.isEmpty()).isTrue();
    }

    @DisplayName("MessageBody 를 문자열로 변환한다.")
    @Test
    void asString() {
        String expect = "ggyool";
        byte[] bytes = {103, 103, 121, 111, 111, 108};

        MessageBody messageBody = MessageBody.from(bytes);
        assertThat(messageBody.asString()).isEqualTo(expect);
    }

    @DisplayName("equals 와 hashCode 검증")
    @Test
    void equalsAndHashCode() {
        // given
        byte[] bytes = {103, 103, 121, 111, 111, 108};
        final MessageBody messageBody = MessageBody.from(bytes);
        final MessageBody otherMessageBody = MessageBody.from(bytes);

        // then
        assertThat(messageBody).isEqualTo(otherMessageBody)
                .hasSameHashCodeAs(otherMessageBody);
    }
}
