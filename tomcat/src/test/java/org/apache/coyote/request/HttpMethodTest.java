package org.apache.coyote.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.apache.coyote.exception.NoSuchHttpMethodException;
import org.junit.jupiter.api.Test;

class HttpMethodTest {

    @Test
    void httpMethod를_조회한다() {
        HttpMethod actual = HttpMethod.from("GET");
        assertThat(actual).isEqualTo(HttpMethod.GET);
    }
    
    @Test
    void 해당하는_httpMethod가_없으면_예외가_발생한다() {
        assertThatThrownBy(() -> HttpMethod.from("NONE"))
                .isInstanceOf(NoSuchHttpMethodException.class);
    }
}
