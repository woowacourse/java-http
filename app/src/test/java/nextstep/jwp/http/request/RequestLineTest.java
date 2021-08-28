package nextstep.jwp.http.request;

import org.junit.jupiter.api.DisplayName;

@DisplayName("request line")
class RequestLineTest {

//    @Test
//    void BufferedReader를_사용하여_line를_읽어_객체를_생성한다() throws NotFoundException, InvalidHttpRequestException {
//        final String requestLineAsString = "GET /index.html HTTP/1.1";
//        final HttpMethod expectedMethod = HttpMethod.GET;
//        final String expectedPath = "/index.html";
//
//        final InputStream inputStream = new ByteArrayInputStream(requestLineAsString.getBytes());
//        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
//        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//
//        final RequestLine actualLine = new RequestLine(bufferedReader);
//
//        assertAll(
//                () -> assertThat(actualLine.getMethod()).isEqualTo(expectedMethod),
//                () -> assertThat(actualLine.getPath()).isEqualTo(expectedPath)
//        );
//    }
}