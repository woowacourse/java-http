package nextstep.jwp.web.http;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("HTTP 요청 및 응답 헤더 관련 로직을 테스트한다.")
class HeadersTest {

    @DisplayName("요청에서 들어온 헤더들을 리스트 형태로 넣으면 파싱하여 map 자료구조에 저장한다. - "
        + "multi-value 헤더가 있을 수 있으므로 List<String>으로 저장")
    @Test
    void Headers_constructorWithList_Success() {
        // given
        String acceptHeaderValueAsString =
            "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,"
                + "image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9";
        List<String> rawHeader = List.of(
            "Accept: " + acceptHeaderValueAsString,
            "Content-Length: 30",
            "Content-Type: application/x-www-form-urlencoded"
        );

        List<String> acceptHeaderValues = Arrays
            .asList(acceptHeaderValueAsString.split(",").clone());

        // when
        Headers headers = new Headers(rawHeader);

        // then
        assertThat(headers.getValue("Accept")).isEqualTo(acceptHeaderValues);
        assertThat(headers.getValue("Content-Length")).isEqualTo(List.of("30"));
        assertThat(headers.getValue("Content-Type"))
            .isEqualTo(List.of("application/x-www-form-urlencoded"));
    }

    @DisplayName("헤더 key와 value로 값을 추가할 수 있다. - 처음 추가하는 값 / 성공")
    @Test
    void addHeader_initAdd_Success() {
        // given
        Headers headers = new Headers();
        String header = "Content-Length";
        String value = "30";

        // when
        headers.add(header, value);

        // then
        assertThat(headers.getValue(header)).isEqualTo(List.of(value));
    }

    @DisplayName("헤더 key와 value로 값을 추가할 수 있다. - 이전에 존재하는 헤더에 값 추가 / 성공")
    @Test
    void addHeader_addExistingHeaderValue_Success() {
        // given
        Headers headers = new Headers();
        String header = "Accept";
        String initialValue = "text/html";
        String newValue = "application/xhtml+xml";

        headers.add(header, initialValue);

        // when
        headers.add(header, newValue);

        // then
        assertThat(headers.getValue(header)).containsExactly(initialValue, newValue);
    }

    @DisplayName("헤더에 추가된 Content-Length 값을 리턴한다. - 성공")
    @Test
    void getContentLength_validContentLength_Success() {
        // given
        Headers headers = new Headers();
        String header = "Content-Length";
        String value = "30";
        headers.add(header, value);

        // when
        int actualContentLength = headers.getContentLength();

        // then
        assertThat(actualContentLength).isEqualTo(Integer.parseInt(value));
    }

    @DisplayName("헤더에 추가된 Content-Length 값이 없으면 0을 리턴한다. - 성공")
    @Test
    void getContentLength_NoContentLength_Success() {
        // given
        Headers headers = new Headers();

        // when
        int actualContentLength = headers.getContentLength();

        // then
        assertThat(actualContentLength).isZero();
    }

    @DisplayName("저장하고 있는 headers에 대해서 알맞은 형식으로 출력한다. - 성공")
    @Test
    void asString_Success() {
        // given
        Headers headers = new Headers();
        headers.add("Accept", "text/html");
        headers.add("Accept", "application/xhtml+xml");
        headers.add("Content-Length", "30");
        headers.add("Content-Type", "application/x-www-form-urlencoded");

        // when
        String expected = String.join("\r\n",
            "Accept: text/html,application/xhtml+xml",
            "Content-Length: 30",
            "Content-Type: application/x-www-form-urlencoded",
            "");

        // then
        assertThat(headers.asString()).isEqualTo(expected);
    }
}
