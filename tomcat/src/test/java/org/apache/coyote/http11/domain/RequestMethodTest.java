package org.apache.coyote.http11.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.apache.coyote.http11.request.domain.RequestMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class RequestMethodTest {

    @ParameterizedTest
    @ValueSource(strings = {"FIND", "SEARCH", "PAAATCH"})
    @DisplayName("일치하는 메서드가 없는 경우 에러를 발생한다.")
    void findMethod_WhenNoCorrespondMethod(String inputRequestMethod) {
        assertThrows(IllegalArgumentException.class, () -> RequestMethod.findMethod(inputRequestMethod));
    }

    @Test
    @DisplayName("일치하는 메서드를 찾아서 반환한다.")
    void findMethod_WhenCorrespondMethod() {
        String requestMethod = "GET";

        assertEquals(RequestMethod.findMethod(requestMethod), RequestMethod.GET);
    }
}
