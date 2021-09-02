package nextstep.jwp.framework.http.common;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpBodyTest {

    @DisplayName("Query Params 생성을 확인한다.")
    @Test
    void create() {
        //given
        HttpBody body = new HttpBody(new QueryParams("account=hi&password=bye"));

        //then
        assertThat(body.getQueryParams()).hasSize(2);
    }

    @DisplayName("body 여부를 확인한다.")
    @Test
    void checkBody() {
        //given
        HttpBody body = new HttpBody();

        //then
        assertThat(body.hasNotBody()).isTrue();
    }

    @DisplayName("Query Params 출력을 확인한다.")
    @Test
    void printQueryParams() {
        //given
        HttpBody body = new HttpBody(new QueryParams("account=hi&password=bye"));

        //then
        assertThat(body.toString()).isEqualTo("password: bye\r\naccount: hi\r\n");
    }
}
