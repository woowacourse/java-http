package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class RequestMethodTest {

    @Test
    void findMethodWithLowerString() {
        // given
        String methodString = "get";
        // when
        RequestMethod method = RequestMethod.findMethod(methodString);
        // then
        assertThat(method).isEqualTo(RequestMethod.GET);
    }

    @Test
    void findMethodWithUpperString() {
        // given
        String methodString = "GET";
        // when
        RequestMethod method = RequestMethod.findMethod(methodString);
        // then
        assertThat(method).isEqualTo(RequestMethod.GET);
    }

}
