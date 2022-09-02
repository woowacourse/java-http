package nextstep.jwp.http;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;

class RequestParamsTest {

    @Test
    void query_parameter가_빈값이_들어오는_경우_빈_Map이_생성된다() {
        RequestParams actual = RequestParams.from("");

        assertThat(actual.getValues()).isEmpty();
    }

    @Test
    void query_paramter를_받아_RequestParams를_생성한다() {
        Map<String, String> expected = Map.of("account", "gugu", "password", "password");
        RequestParams actual = RequestParams.from("account=gugu&password=password");

        assertThat(actual.getValues()).isEqualTo(expected);
    }
}
