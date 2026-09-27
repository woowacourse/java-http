package org.apache.coyote.http11;

import org.apache.coyote.http11.enums.HttpStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HttpResponseTest {

    @Test
    void 응답은_200_상태와_빈_헤더와_본문으로_초기화된다() {
        final HttpResponse response = new HttpResponse();

        assertThat(response.status()).isEqualTo(HttpStatus.OK);
        assertThat(response.headers()).isEmpty();
        assertThat(response.body()).isEmpty();
    }

    @Test
    void 상태와_헤더를_변경할_수_있다() {
        final HttpResponse response = new HttpResponse();

        response.setStatus(HttpStatus.SEE_OTHER);
        response.addHeader("Location", "/login");

        assertThat(response.status()).isEqualTo(HttpStatus.SEE_OTHER);
        assertThat(response.headers()).containsEntry("Location", "/login");
        assertThatThrownBy(() -> response.headers().put("Name", "Value"))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void 응답_본문은_외부의_배열_변경으로부터_보호된다() {
        final HttpResponse response = new HttpResponse();
        final byte[] body = {1, 2, 3};

        response.setBody(body);
        body[0] = 9;
        final byte[] returnedBody = response.body();
        returnedBody[1] = 9;

        assertThat(response.body()).containsExactly(1, 2, 3);
    }
}
