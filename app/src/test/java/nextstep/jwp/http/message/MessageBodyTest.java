package nextstep.jwp.http.message;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MessageBodyTest {

    @DisplayName("MessageBody 를 생성한다.")
    @Test
    void create() {
        String ggyool = "ggyool";
        byte[] bytes = {103, 103, 121, 111, 111, 108};

        MessageBody messageBody = new MessageBody(ggyool);
        assertThat(messageBody.getBytes()).isEqualTo(bytes);
    }

    @DisplayName("비어있는 MessageBody 를 생성한다.")
    @Test
    void createEmpty() {
        MessageBody messageBody = MessageBody.empty();
        assertThat(messageBody.getBytes()).isEqualTo(new byte[0]);
        assertThat(messageBody).isSameAs(MessageBody.empty());
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

        MessageBody messageBody = new MessageBody(bytes);
        assertThat(messageBody.asString()).isEqualTo(expect);
    }
}
