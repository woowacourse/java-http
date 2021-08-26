package nextstep.jwp.http.message;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MessageBodyTest {

    @DisplayName("MessageBody를 생성한다.")
    @Test
    void create() {
        String ggyool = "ggyool";
        byte[] bytes = {103, 103, 121, 111, 111, 108};

        MessageBody messageBody = new MessageBody(ggyool);
        assertThat(messageBody.getBytes()).isEqualTo(bytes);
    }
}