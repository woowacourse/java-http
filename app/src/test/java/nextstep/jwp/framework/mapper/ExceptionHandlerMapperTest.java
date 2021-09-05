package nextstep.jwp.framework.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExceptionHandlerMapperTest {

    @DisplayName("매핑 정보가 없으면 예외를 throw 한다.")
    @Test
    void resolveWithUndefinedMapping() {
        ExceptionHandlerMapper exceptionHandlerMapper = ExceptionHandlerMapper.getInstance();
        MockRuntimeException mockRuntimeException = new MockRuntimeException();
        assertThatThrownBy(() -> exceptionHandlerMapper.resolve(mockRuntimeException))
                .isSameAs(mockRuntimeException);
    }

    private static class MockRuntimeException extends RuntimeException {
    }
}
