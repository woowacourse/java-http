package org.apache.coyote.http11.error;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.apache.coyote.http11.error.errorhandler.Error401Handler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ErrorHandlerMapperTest {

    @DisplayName("각 에러 상황에 따라 맞는 errorHandler가 있는지 판단 할 수 있다")
    @Test
    void hasHandler() {
        Exception exsitsException = new SecurityException();
        Exception noneExsitsException = new Exception();

        assertAll(
                () -> assertThat(ErrorHandlerMapper.hasErrorHandler(exsitsException.getClass())).isTrue(),
                () -> assertThat(ErrorHandlerMapper.hasErrorHandler(noneExsitsException.getClass())).isFalse()
        );
    }
}
