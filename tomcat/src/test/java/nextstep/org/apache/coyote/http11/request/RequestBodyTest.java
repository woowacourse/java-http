package nextstep.org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import org.apache.coyote.http11.request.RequestBody;
import org.junit.jupiter.api.Test;
import support.RequestFixture;

class RequestBodyTest {

    @Test
    void get() throws IOException {
        // given
        final RequestBody requestBody = RequestFixture.createBody("key1=value1&key2=value2");

        // when
        final String actual1 = requestBody.get("key1");
        final String actual2 = requestBody.get("key2");

        // then
        assertAll(
                () -> assertThat(actual1).isEqualTo("value1"),
                () -> assertThat(actual2).isEqualTo("value2")
        );
    }
}
