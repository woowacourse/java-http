package org.apache.coyote.http11;

import static org.apache.coyote.http11.HttpMethod.findHttpMethod;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
class HttpMethodTest {

    @Test
    void 해당_문자열에_대한_HTTP_METHOD_를_찾을_수_있다() {
        assertEquals(HttpMethod.GET, findHttpMethod("GET"));
        assertEquals(HttpMethod.POST, findHttpMethod("POST"));
        assertEquals(HttpMethod.PUT, findHttpMethod("PUT"));
        assertEquals(HttpMethod.DELETE, findHttpMethod("DELETE"));
    }

    @Test
    void 존재하지_않는_메소드문자열에_대해_찾을_경우_예외가_발생한다() {
        assertThatThrownBy(() -> findHttpMethod("INVALID_METHOD"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 메소드문자열이_없는_경우_예외가_발생한다() {
        assertThatThrownBy(() -> findHttpMethod(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 메소드문자열이_빈값인_경우_예외가_발생한다() {
        assertThatThrownBy(() -> findHttpMethod(""))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
