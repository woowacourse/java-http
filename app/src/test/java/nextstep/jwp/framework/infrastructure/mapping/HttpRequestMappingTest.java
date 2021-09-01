package nextstep.jwp.framework.infrastructure.mapping;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.Arrays;
import nextstep.jwp.framework.config.FactoryConfiguration;
import nextstep.jwp.framework.controller.Controller;
import nextstep.jwp.framework.controller.custom.IndexPageController;
import nextstep.jwp.framework.controller.custom.LoginController;
import nextstep.jwp.framework.controller.custom.RegisterController;
import nextstep.jwp.framework.controller.standard.StaticResourceController;
import nextstep.jwp.framework.infrastructure.exception.NotFoundException;
import nextstep.jwp.framework.infrastructure.http.request.HttpRequest;
import nextstep.jwp.framework.infrastructure.http.request.HttpRequestBody;
import nextstep.jwp.framework.infrastructure.http.request.HttpRequestHeader;
import nextstep.jwp.framework.infrastructure.http.status.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("HttpRequestMapping 테스트")
class HttpRequestMappingTest {

    private final RequestMapping requestMapping = FactoryConfiguration.requestMapping();

    @DisplayName("findController 메서드는")
    @Nested
    class Describe_findController {

        @DisplayName("Http 요청이 정적 파일 요청이라면")
        @Nested
        class Context_static_file_request {

            @DisplayName("StaticResourceController를 반환한다.")
            @Test
            void it_returns_staticResourceController() {
                // given
                HttpRequest httpRequest = HttpRequest.ofStaticFile("/css/style.css");

                // when
                Controller controller = requestMapping.findController(httpRequest);

                // then
                assertThat(controller).isInstanceOf(StaticResourceController.class);
            }
        }

        @DisplayName(" / Get Mapping 컨트롤러에 부합하는 요청이라면")
        @Nested
        class Context_get_mapping_controller {

            @DisplayName("IndexPageController를 반환한다.")
            @Test
            void it_returns_indexPageController() {
                // given
                HttpRequestHeader httpRequestHeader =
                    HttpRequestHeader.from(Arrays.asList("GET / HTTP/1.1"));
                HttpRequest httpRequest =
                    new HttpRequest(httpRequestHeader, new HttpRequestBody(null));

                // when
                Controller controller = requestMapping.findController(httpRequest);

                // then
                assertThat(controller).isInstanceOf(IndexPageController.class);
            }
        }

        @DisplayName("Post Mapping 컨트롤러의 로그인에 부합하는 요청이라면")
        @Nested
        class Context_post_mapping_login_controller {

            @DisplayName("LoginController를 반환한다.")
            @Test
            void it_returns_loginController() {
                // given
                HttpRequestHeader httpRequestHeader =
                    HttpRequestHeader.from(Arrays.asList("POST /login HTTP/1.1"));
                HttpRequest httpRequest =
                    new HttpRequest(httpRequestHeader, new HttpRequestBody(null));

                // when
                Controller controller = requestMapping.findController(httpRequest);

                // then
                assertThat(controller).isInstanceOf(LoginController.class);
            }
        }

        @DisplayName("Post Mapping 컨트롤러의 회원가입에 부합하는 요청이라면")
        @Nested
        class Context_post_mapping_register_controller {

            @DisplayName("RegisterController를 반환한다.")
            @Test
            void it_returns_registerController() {
                // given
                HttpRequestHeader httpRequestHeader =
                    HttpRequestHeader.from(Arrays.asList("POST /register HTTP/1.1"));
                HttpRequest httpRequest =
                    new HttpRequest(httpRequestHeader, new HttpRequestBody(null));

                // when
                Controller controller = requestMapping.findController(httpRequest);

                // then
                assertThat(controller).isInstanceOf(RegisterController.class);
            }
        }

        @DisplayName("컨트롤러를 찾을 수 없는 요청이라면")
        @Nested
        class Context_invalid_request {

            @DisplayName("404 예외를 반환한다.")
            @Test
            void it_returns_notFoundController() {
                // given
                HttpRequestHeader httpRequestHeader =
                    HttpRequestHeader.from(Arrays.asList("POST /rafder HTTP/1.1"));
                HttpRequest httpRequest =
                    new HttpRequest(httpRequestHeader, new HttpRequestBody(null));

                // when, then
                assertThatCode(() -> requestMapping.findController(httpRequest))
                    .isInstanceOf(NotFoundException.class)
                    .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND);
            }
        }
    }
}
