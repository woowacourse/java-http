package nextstep.jwp.framework.http.httpStatusState;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URL;
import nextstep.jwp.framework.http.common.HttpPath;
import nextstep.jwp.framework.http.common.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpStatusStateTest {

    @DisplayName("OK 상태일 때 index.html 페이지를 반환한다.")
    @Test
    void statusOk() {
        //given
        HttpOKStatus httpOKStatus = new HttpOKStatus(HttpStatus.OK, new HttpPath("/index.html"));
        URL expected = getClass().getClassLoader().getResource("static/index.html");

        //when
        URL resource = httpOKStatus.resource();

        //then
        assertThat(resource).isEqualTo(expected);
    }

    @DisplayName("CREATED 상태일 때 CREATED 를 반환한다.")
    @Test
    void statusCreated() {
        //given
        HttpOKStatus status = new HttpOKStatus(HttpStatus.CREATED, new HttpPath("/index.html"));

        //when
        HttpStatusState state = status.state();

        //then
        assertThat(state).isInstanceOf(HttpCreatedStatus.class);
    }

    @DisplayName("FOUND 상태일 때 FOUND 를 반환한다.")
    @Test
    void statusFound() {
        //given
        HttpOKStatus status = new HttpOKStatus(HttpStatus.FOUND, new HttpPath("/index.html"));

        //when
        HttpStatusState state = status.state();

        //then
        assertThat(state).isInstanceOf(HttpFoundStatus.class);
    }

    @DisplayName("UNAUTHORIZED 상태일 때 UNAUTHORIZED 를 반환한다.")
    @Test
    void statusUnauthorized() {
        //given
        HttpOKStatus status = new HttpOKStatus(HttpStatus.UNAUTHORIZED, new HttpPath("/index.html"));

        //when
        HttpStatusState state = status.state();

        //then
        assertThat(state).isInstanceOf(HttpUnauthorizedStatus.class);
    }

    @DisplayName("NOT_FOUND 상태일 때 NOT_FOUND 를 반환한다.")
    @Test
    void statusNotFound() {
        //given
        HttpOKStatus status = new HttpOKStatus(HttpStatus.NOT_FOUND, new HttpPath("/index.html"));

        //when
        HttpStatusState state = status.state();

        //then
        assertThat(state).isInstanceOf(HttpNotFoundStatus.class);
    }
}
