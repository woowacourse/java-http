//package nextstep.org.apache.coyote.http11;
//
//import org.apache.coyote.http11.HttpExtensionType;
//import org.apache.coyote.http11.HttpResponse;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//class HttpResponseTest {
//
//    @DisplayName("")
//    @Test
//    void extractResponse() {
//        // given
//        final HttpExtensionType httpExtensionType = HttpExtensionType.HTML;
//        final String responseBody = "Hello world!";
//
//        final String expectedResponse = String.join("\r\n",
//                "HTTP/1.1 200 OK ",
//                "Content-Type: " + httpExtensionType.getContentType() + ";charset=utf-8 ",
//                "Content-Length: " + responseBody.getBytes().length + " ",
//                "",
//                responseBody);
//
//        // when
//        final HttpResponse actual = new HttpResponse(httpExtensionType, responseBody);
//
//        // then
//        assertThat(actual.extractResponse()).isEqualTo(expectedResponse);
//    }
//}
