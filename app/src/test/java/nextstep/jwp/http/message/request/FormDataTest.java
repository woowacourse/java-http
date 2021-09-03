package nextstep.jwp.http.message.request;

import nextstep.jwp.http.message.MessageBody;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class FormDataTest {

    @DisplayName("RequestBody로 FormData 를 생성한다.")
    @Test
    void createFromBody() {
        // given
        Map<String, String> expected = Map.of(
                "account", "ggyool",
                "password", "password",
                "email", "ggyool@never.com"
        );

        String formDataMessage = "account=ggyool&password=password&email=ggyool@never.com";
        MessageBody messageBody = new MessageBody(formDataMessage);

        // when
        FormData formData = FormData.from(messageBody);

        // then
        assertThat(formData.toMap()).containsAllEntriesOf(expected);
    }
}
