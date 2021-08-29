package nextstep.jwp.web.http.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import nextstep.jwp.web.http.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpHeadersTest {

    @DisplayName("헤더 값을 추가한다.")
    @Test
    void addTest() {
        //given
        HttpHeaders httpHeaders = new HttpHeaders();
        String key = "content-type";
        String value = "application/json";
        //when
        httpHeaders.add(key, value);
        //then
        assertThat(httpHeaders.toValuesString(key)).isEqualTo(value);
        httpHeaders.add(key, value);
        assertThat(httpHeaders.toValuesString(key)).isEqualTo(value + "," + value);

    }

    @DisplayName("중복 헤더값을 추가한다.")
    @Test
    void addListTest() {
        //given
        HttpHeaders httpHeaders = new HttpHeaders();
        String key = "content-type";
        String value1 = "application/json";
        String value2 = "text/HTML";
        String value3 = "text/plain";
        //when
        httpHeaders.add(key, List.of(value1, value2, value3));
        //then
        String expectedResult = value1 + "," + value2 + "," + value3;
        assertThat(httpHeaders.toValuesString(key)).isEqualTo(expectedResult);
    }


}