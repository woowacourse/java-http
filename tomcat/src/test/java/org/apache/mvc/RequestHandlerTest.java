package org.apache.mvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.apache.coyote.http11.ResponseEntity;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.exception.TempException;
import org.junit.jupiter.api.Test;

class RequestHandlerTest {

    @Test
    void createHttpRequest() {
        // given & when
        Controller controller = new Controller() {
            public ResponseEntity myMethod(HttpRequest httpRequest) {
                return new ResponseEntity(HttpStatus.OK, "hello", determineContentType(path));
            }
        };

        // then
        assertThatNoException().isThrownBy(() -> new RequestHandler(
                controller,
                controller.getClass().getMethod("myMethod", HttpRequest.class)
        ));
    }

    @Test
    void handleRequestByInvocation() throws NoSuchMethodException {
        // given
        Controller controller = new Controller() {
            public ResponseEntity myMethod(HttpRequest httpRequest) {
                return new ResponseEntity(HttpStatus.OK, "hello", determineContentType(path));
            }
        };
        RequestHandler handler = new RequestHandler(
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
        assertThatThrownBy(() -> new RequestHandler(
                controller,
                controller.getClass().getMethod("myMethod", HttpRequest.class)
        )).isInstanceOf(TempException.class);
    }

    @Test
    void throwsExceptionWithInvalidParameterLength() {
        // given & when
        Controller controller = new Controller() {
            public ResponseEntity myMethod(HttpRequest httpRequest, int a) {
                return new ResponseEntity(HttpStatus.OK, "hello", determineContentType(path));
            }
        };

        // then
        assertThatThrownBy(() -> new RequestHandler(
                controller,
                controller.getClass().getMethod("myMethod", HttpRequest.class, int.class)
        )).isInstanceOf(TempException.class);
    }

    @Test
    void throwsExceptionWithInvalidParameterType() {
        // given & when
        Controller controller = new Controller() {
            public ResponseEntity myMethod(int invaliParamter) {
                return new ResponseEntity(HttpStatus.OK, "hello", determineContentType(path));
            }
        };

        // then
        assertThatThrownBy(() -> new RequestHandler(
                controller,
                controller.getClass().getMethod("myMethod", int.class)
        )).isInstanceOf(TempException.class);
    }

    @Test
    void throwsExceptionWithInvalidInstance() {
        // given & when
        Controller controller = new Controller() {
            public ResponseEntity myMethod(HttpRequest httpRequest) {
                return new ResponseEntity(HttpStatus.OK, "hello", determineContentType(path));
            }
        };
        Object invalidInstance = new Object();

        // then
        assertThatThrownBy(() -> new RequestHandler(
                invalidInstance,
                controller.getClass().getMethod("myMethod", HttpRequest.class)
        )).isInstanceOf(TempException.class);
    }
}
