package nextstep.jwp.resolver;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import nextstep.jwp.http.response.HttpResponseBody;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class HtmlResolverTest {

    @DisplayName("html파일이 존재하는 url이 들어오면 isSuitable에서 반환하는지 확인")
    @ParameterizedTest
    @CsvSource({"wedge/good/index, false", "foo/foofoo, false", "abc.html, false", "nextstep, true"})
    void isSuitableTest(String filePath, boolean expectedResult) {
        //given
        HtmlResolver htmlResolver = new HtmlResolver();
        //when
        //then
        assertThat(htmlResolver.isExist(filePath, "")).isEqualTo(expectedResult);
    }

    @DisplayName("알맞은 데이터를 반환하는지 확인")
    @Test
    void getViewTest() throws IOException {
        //given
        HtmlResolver htmlResolver = new HtmlResolver();
        String filePath = "nextstep";
        //when
        HttpResponseBody responseBody = htmlResolver.resolve(filePath, "");
        //then
        String body = responseBody.body();
        assertThat(body).isEqualTo(""
            + "<!DOCTYPE html>\n"
            + "<html lang=\"en\">\n"
            + "I'm on the next Level 절대적 룰을 지켜\n"
            + "내 손을 놓지말아 결속은 나의 무기\n"
            + "광야로 걸어가 알아 내 homeground 위협에 맞서서 재껴라 재껴라 재껴라"
        );
    }
}