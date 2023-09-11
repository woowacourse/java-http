package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.common.Cookie;
import org.apache.coyote.http11.common.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @Test
    @DisplayName("ResponseBody에 값을 변경하면, Content-Length가 자동으로 업데이트 된다.")
    void updateContentLength_IfUpdateHttpBody() {
        //given
        HttpResponse httpResponse = HttpResponse.createEmptyResponse();
        httpResponse.setBody("");
        String before = new String(httpResponse.getBytes());

        assertThat(before).contains("Content-Length: 0");

        //when
        httpResponse.setBody("테스트를 위한 콘텐츠 바디 !!");

        //then
        String after = new String(httpResponse.getBytes());

        assertThat(after).contains("Content-Length: " + "테스트를 위한 콘텐츠 바디 !!".getBytes().length);
    }

    @Test
    @DisplayName("JSESSIONID 값을 삭제하도록 Cookie를 설정할 수 있다.")
    void setDeletingJSESSIONID() {
        //given
        HttpResponse httpResponse = HttpResponse.createEmptyResponse();
        String expected = "Set-Cookie: JSESSIONID=; Max-Age=0";

        //when
        httpResponse.setDeletingJSessionCookie();

        //then
        String actual = new String(httpResponse.getBytes());
        assertThat(actual).contains(expected);
    }

    @Test
    @DisplayName("Response에 저장되어 있는 값을 응답 형식의 문자열로 반환할 수 있다.")
    void extractForResponse() {
        //given
        HttpResponse httpResponse = HttpResponse.createEmptyResponse();

        httpResponse.setStatus(HttpStatus.OK);
        httpResponse.setContentType("text/plain");
        httpResponse.setBody("테스트 바디");
        httpResponse.setCookie("JSESSIONID", Cookie.createWithEmptyAttribute("test"));

        String expected = String.join(System.lineSeparator(), "HTTP/1.1 200 OK ", "Content-Type: text/plain ",
                "Content-Length: 16 ", "Set-Cookie: JSESSIONID=test ", "", "테스트 바디");

        //when

        String actual = new String(httpResponse.getBytes());

        //then
        assertThat(expected).isEqualTo(actual);
    }


}