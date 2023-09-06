//package org.apache.coyote.http11;
//
//import static org.assertj.core.api.SoftAssertions.assertSoftly;
//
//import java.util.List;
//import org.junit.jupiter.api.Test;
//
//@SuppressWarnings("NonAsciiCharacters")
//class ResponseBodyTest {
//
//    @Test
//    void 기본_path_인_경우_heelo_world_를_출력한다() {
//        // given
//        final String info = "/";
//        final ResponseBody responseBody = ResponseBody.from(info, HttpStatus.OK, HttpVersion.HTTP_1_1);
//
//        // when
//        final String responseMessage = responseBody.getMessage();
//
//        // then
//        assertSoftly(softly -> {
//            softly.assertThat(responseMessage).contains("Hello world!");
//            softly.assertThat(responseMessage).contains("HTTP/1.1 200 OK");
//            softly.assertThat(responseMessage).contains("Content-Type: text/html;charset=utf-8");
//        });
//    }
//
//    @Test
//    void 기본_path_아닌경우_path_를_보고_알맞은_응답을_반환한다() {
//        // given
//        final String info = "/index";
//        final ResponseBody responseBody = ResponseBody.from(info, HttpStatus.OK, HttpVersion.HTTP_1_1);
//
//        // when
//        final String responseMessage = responseBody.getMessage();
//
//        // then
//        assertSoftly(softly -> {
//            softly.assertThat(responseMessage).contains("HTTP/1.1 200 OK");
//            softly.assertThat(responseMessage).contains("Content-Type: text/html;charset=utf-8");
//        });
//    }
//
//    @Test
//    void 정상적으로_redirect_할_수_있다() {
//        // given when
//        final String page = "/index.html";
//        final String redirectResponse = ResponseBody.redirectResponse(page, HttpVersion.HTTP_1_1);
//
//        // then
//        assertSoftly(softly -> {
//            softly.assertThat(redirectResponse).contains("HTTP/1.1 302 FOUND");
//            softly.assertThat(redirectResponse).contains("Location: " + page);
//        });
//    }
//
//    @Test
//    void 세션을_추가해서_redirect_할_수_있다() {
//        // given
//        final HttpRequest httpRequest = HttpRequest.of(List.of("GET /index?name=wuga HTTP/1.1"), null);
//        final String page = "/index.html";
//        final String sessionId = "abcd1234";
//
//        // when
//        final String redirectResponse = ResponseBody.redirectResponse(page, httpRequest, sessionId);
//
//        // then
//        assertSoftly(softly -> {
//            softly.assertThat(redirectResponse).contains("HTTP/1.1 302 FOUND");
//            softly.assertThat(redirectResponse).contains("Location: " + page);
//            softly.assertThat(redirectResponse).contains("Set-Cookie: " + "JSESSIONID=" + sessionId);
//        });
//    }
//}
