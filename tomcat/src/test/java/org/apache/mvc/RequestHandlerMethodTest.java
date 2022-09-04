package org.apache.mvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.ResponseEntity;
import org.apache.exception.TempException;
import org.apache.mvc.handlerchain.RequestHandlerMethod;
import org.junit.jupiter.api.Test;

class RequestHandlerMethodTest {

    @Test
    void createHttpRequest() {
        // given & when
        Controller controller = new Controller() {
            public ResponseEntity myMethod(HttpRequest httpRequest) {
                return new ResponseEntity(HttpStatus.OK, "hello");
            }
        };

        // then
        assertThatNoException().isThrownBy(() -> new RequestHandlerMethod(
                controller,
                controller.getClass().getMethod("myMethod", HttpRequest.class)
        ));
    }

    @Test
    void handleRequestByInvocation() throws NoSuchMethodException {
        // given
        Controller controller = new Controller() {
            public ResponseEntity myMethod(HttpRequest httpRequest) {
                return new ResponseEntity(HttpStatus.OK, "hello");
            }
        };
        RequestHandlerMethod handler = new RequestHandlerMethod(
                controller,
                controller.getClass().getMethod("myMethod", HttpRequest.class)
        );

        // when
        ResponseEntity entity = handler.handle(null);

        // then
        assertThat(entity.getBody()).isEqualTo("hello");
    }

    @Test
    void throwsExceptionWithInvalidReturnType() {
        // given & when
        Controller controller = new Controller() {
            public int myMethod(HttpRequest httpRequest) {
                return 1;
            }
        };

        // then
        assertThatThrownBy(() -> new RequestHandlerMethod(
                controller,
                controller.getClass().getMethod("myMethod", HttpRequest.class)
        )).isInstanceOf(TempException.class);
    }

    @Test
    void throwsExceptionWithInvalidParameterLength() {
        // given & when
        Controller controller = new Controller() {
            public ResponseEntity myMethod(HttpRequest httpRequest, int a) {
                return new ResponseEntity(HttpStatus.OK, "hello");
            }
        };

        // then
        assertThatThrownBy(() -> new RequestHandlerMethod(
                controller,
                controller.getClass().getMethod("myMethod", HttpRequest.class, int.class)
        )).isInstanceOf(TempException.class);
    }

    @Test
    void throwsExceptionWithInvalidParameterType() {
        // given & when
        Controller controller = new Controller() {
            public ResponseEntity myMethod(int invaliParamter) {
                return new ResponseEntity(HttpStatus.OK, "hello");
            }
        };

        // then
        assertThatThrownBy(() -> new RequestHandlerMethod(
                controller,
                controller.getClass().getMethod("myMethod", int.class)
        )).isInstanceOf(TempException.class);
    }

    @Test
    void throwsExceptionWithInvalidInstance() {
        // given & when
        Controller controller = new Controller() {
            public ResponseEntity myMethod(HttpRequest httpRequest) {
                return new ResponseEntity(HttpStatus.OK, "hello");
            }
        };
        Object invalidInstance = new Object();

        // then
        assertThatThrownBy(() -> new RequestHandlerMethod(
                invalidInstance,
                controller.getClass().getMethod("myMethod", HttpRequest.class)
        )).isInstanceOf(TempException.class);
    }
}
