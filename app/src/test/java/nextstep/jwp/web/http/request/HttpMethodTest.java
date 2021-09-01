package nextstep.jwp.web.http.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class HttpMethodTest {

    @DisplayName("이름으로 메소드를 검색한다.")
    @ParameterizedTest
    @CsvSource({"get,GET", "post,POST", "patch,PATCH",
        "put,PUT", "options,OPTIONS", "delete,DELETE",
        "trace,TRACE"
    })
    void findByNameTest(String name, HttpMethod method) {
        //given
        //when
        //then
        assertThat(HttpMethod.findByName(name)).isEqualTo(method);
    }
}