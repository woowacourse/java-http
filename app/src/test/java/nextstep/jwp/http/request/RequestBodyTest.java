package nextstep.jwp.http.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("RequestBody 테스트")
class RequestBodyTest {

    @DisplayName("RequestBody queryParams 테스트")
    @Test
    void queryParams() {
        //given
        final RequestBody requestBody = new RequestBody("account=inbi&password=1234");

        //when
        //then
        assertThat(requestBody.getParameter("account")).isEqualTo("inbi");
        assertThat(requestBody.getParameter("password")).isEqualTo("1234");
    }
}