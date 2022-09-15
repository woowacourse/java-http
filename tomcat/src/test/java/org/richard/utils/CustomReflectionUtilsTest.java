package org.richard.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.lang.reflect.Method;
import java.util.function.Function;
import nextstep.jwp.view.NextstepController;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.servlet.DispatcherServlet;

class CustomReflectionUtilsTest {

    @DisplayName("newInstance 메서드는")
    @Nested
    class newInstance {

        @DisplayName("전달받은 class의 기본 생성자를 이용해 인스턴스를 생성한다")
        @Test
        void newInstance() {
            // given
            final Class<DispatcherServlet> clazz = DispatcherServlet.class;

            // when
            final DispatcherServlet actual = CustomReflectionUtils.newInstance(clazz);

            // then
            assertAll(
                    () -> assertThat(actual).isNotNull(),
                    () -> assertThat(actual).isInstanceOf(clazz)
            );
        }
    }

    @DisplayName("runnableByMethod 메서드는")
    @Nested
    class runnableByMethod {

        @DisplayName("전달받은 인스턴스와 메서드 정보를 이용해 Function을 반환한다")
        @Test
        void runnableByMethod() throws NoSuchMethodException {
            // given
            final NextstepController instance = new NextstepController();
            final Method method = instance.getClass().getMethod("hello", HttpRequest.class);
            final String expected = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: 12 ",
                    "",
                    "Hello world!"
            );

            // when
            final Function<HttpRequest, HttpResponse> actual = CustomReflectionUtils.runnableByMethod(instance, method);
            final String responseHttpMessage = actual.apply(null).getResponseHttpMessage();

            // then
            assertAll(
                    () -> assertThat(actual).isNotNull(),
                    () -> assertThat(responseHttpMessage).isEqualTo(expected)
            );
        }
    }
}
