package nextstep.jwp.application.mapping;

import nextstep.jwp.application.exception.UnauthorizedException;
import nextstep.jwp.application.exception.handler.HtmlNotFoundExceptionHandler;
import nextstep.jwp.application.exception.handler.RuntimeExceptionHandler;
import nextstep.jwp.application.exception.handler.StaticResourceNotFoundExceptionHandler;
import nextstep.jwp.application.exception.handler.UnauthorizedExceptionHandler;
import nextstep.jwp.application.exception.handler.UriMappingNotFoundExceptionHandler;
import nextstep.jwp.framework.exception.ExceptionHandler;
import nextstep.jwp.framework.exception.HtmlNotFoundException;
import nextstep.jwp.framework.exception.StaticResourceNotFoundException;
import nextstep.jwp.framework.exception.UriMappingNotFoundException;
import nextstep.jwp.framework.mapper.ExceptionHandlerMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExceptionHandlerMappingsTest {

    private final ExceptionHandlerMapper exceptionHandlerMapper = ExceptionHandlerMapper.getInstance();

    @DisplayName("매핑 확인 (UnauthorizedException)")
    @Test
    void unauthorizedException() {
        ExceptionHandler exceptionHandler = exceptionHandlerMapper.resolve(new UnauthorizedException(""));
        assertThat(exceptionHandler).isInstanceOf(UnauthorizedExceptionHandler.class);
    }

    @DisplayName("매핑 확인 (UriMappingNotFoundException)")
    @Test
    void UriMappingNotFoundException() {
        ExceptionHandler exceptionHandler = exceptionHandlerMapper.resolve(new UriMappingNotFoundException(""));
        assertThat(exceptionHandler).isInstanceOf(UriMappingNotFoundExceptionHandler.class);
    }

    @DisplayName("매핑 확인 (HtmlNotFoundException)")
    @Test
    void HtmlNotFoundException() {
        ExceptionHandler exceptionHandler = exceptionHandlerMapper.resolve(new HtmlNotFoundException(""));
        assertThat(exceptionHandler).isInstanceOf(HtmlNotFoundExceptionHandler.class);
    }

    @DisplayName("매핑 확인 (StaticResourceNotFoundException)")
    @Test
    void StaticResourceNotFoundException() {
        ExceptionHandler exceptionHandler = exceptionHandlerMapper.resolve(new StaticResourceNotFoundException(""));
        assertThat(exceptionHandler).isInstanceOf(StaticResourceNotFoundExceptionHandler.class);
    }

    @DisplayName("매핑 확인 (RuntimeException)")
    @Test
    void RuntimeException() {
        ExceptionHandler exceptionHandler = exceptionHandlerMapper.resolve(new RuntimeException(""));
        assertThat(exceptionHandler).isInstanceOf(RuntimeExceptionHandler.class);
    }
}
