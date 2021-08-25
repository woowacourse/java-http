package nextstep.jwp.http.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestHeadersTest {

    @DisplayName("헤더 값을 추가한다.")
    @Test
    void addTest() {
        //given
        HttpRequestHeaders httpRequestHeaders = new HttpRequestHeaders();
        String key = "content-type";
        String value = "application/json";
        //when
        httpRequestHeaders.add(key, value);
        //then
        assertThat(httpRequestHeaders.toValuesString(key)).isEqualTo(value);
        httpRequestHeaders.add(key, value);
        assertThat(httpRequestHeaders.toValuesString(key)).isEqualTo(value+","+value);

    }

    @DisplayName("중복 헤더값을 추가한다.")
    @Test
    void addListTest() {
        //given
        HttpRequestHeaders httpRequestHeaders = new HttpRequestHeaders();
        String key = "content-type";
        String value1 = "application/json";
        String value2 = "text/HTML";
        String value3 = "text/plain";
        //when
        httpRequestHeaders.add(key, List.of(value1, value2, value3));
        //then
        String expectedResult = value1 + "," + value2 + "," + value3;
        assertThat(httpRequestHeaders.toValuesString(key)).isEqualTo(expectedResult);
    }


}