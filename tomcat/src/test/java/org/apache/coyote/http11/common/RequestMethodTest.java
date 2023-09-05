package org.apache.coyote.http11.common;

import org.apache.coyote.http11.request.RequestMethod;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RequestMethodTest {

    @Test
    void 메서드에_맞는_RequestMethod를_반환한다() {
        RequestMethod result = RequestMethod.find("GET");
        RequestMethod expected = RequestMethod.GET;

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void 존재하지_않는_메서드는_예외발생() {
        assertThatThrownBy(() -> RequestMethod.find("KK"))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
