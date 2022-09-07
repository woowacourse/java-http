package servlet.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.NoSuchElementException;
import nextstep.jwp.controller.exception.BaseHandler;
import nextstep.jwp.controller.exception.NotFoundHandler;
import nextstep.jwp.controller.exception.UnauthorizedHandler;
import nextstep.jwp.exception.InvalidPasswordException;
import org.apache.coyote.http11.response.element.HttpStatus;
import org.junit.jupiter.api.Test;

class ExceptionMappingImplTest {

    @Test
    void map() {
        ExceptionMapping exceptionMapping = new ExceptionMappingImpl(
                List.of(new NotFoundHandler(), new UnauthorizedHandler()), new BaseHandler());

        assertThat(exceptionMapping.map(new NoSuchElementException()).getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(exceptionMapping.map(new InvalidPasswordException()).getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(exceptionMapping.map(new Exception()).getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
