package nextstep.jwp.framework.infrastructure.mapping;

import org.junit.jupiter.api.DisplayName;

@DisplayName("HttpRequestMapping 테스트")
class HttpRequestMappingTest {

//    private final RequestMapping requestMapping = FactoryConfiguration.requestMapping();
//
//    @DisplayName("findAdapter 메서드는")
//    @Nested
//    class Describe_findAdapter {
//
//        @DisplayName("Http 요청이 정적 파일 요청이라면")
//        @Nested
//        class Context_static_file_request {
//
//            @DisplayName("StaticRequestAdapter를 반환한다.")
//            @Test
//            void it_returns_staticRequestAdapter() {
//                // given
//                HttpRequest httpRequest = HttpRequest.ofStaticFile("/css/style.css");
//
//                // when
//                RequestAdapter adapter = requestMapping.findAdapter(httpRequest);
//
//                // then
//                assertThat(adapter).isInstanceOf(StaticRequestAdapter.class);
//            }
//        }
//
//        @DisplayName("GET Mapping 컨트롤러에 부합하는 요청이라면")
//        @Nested
//        class Context_get_mapping_controller {
//
//            @DisplayName("PageGetRequestAdapter를 반환한다.")
//            @Test
//            void it_returns_pageGetRequestAdapter() {
//                // given
//                HttpRequestHeader httpRequestHeader =
//                    HttpRequestHeader.from(Arrays.asList("GET /login HTTP/1.1"));
//                HttpRequest httpRequest =
//                    new HttpRequest(httpRequestHeader, new HttpRequestBody(null));
//
//                // when
//                RequestAdapter adapter = requestMapping.findAdapter(httpRequest);
//
//                // then
//                assertThat(adapter).isInstanceOf(PageGetRequestAdapter.class);
//            }
//        }
//
//        @DisplayName("Post Mapping 컨트롤러의 로그인에 부합하는 요청이라면")
//        @Nested
//        class Context_post_mapping_login_controller {
//
//            @DisplayName("LoginRequestAdapter를 반환한다.")
//            @Test
//            void it_returns_loginRequestAdapter() {
//                // given
//                HttpRequestHeader httpRequestHeader =
//                    HttpRequestHeader.from(Arrays.asList("POST /login HTTP/1.1"));
//                HttpRequest httpRequest =
//                    new HttpRequest(httpRequestHeader, new HttpRequestBody(null));
//
//                // when
//                RequestAdapter adapter = requestMapping.findAdapter(httpRequest);
//
//                // then
//                assertThat(adapter).isInstanceOf(LoginRequestAdapter.class);
//            }
//        }
//
//        @DisplayName("Post Mapping 컨트롤러의 회원가입에 부합하는 요청이라면")
//        @Nested
//        class Context_post_mapping_register_controller {
//
//            @DisplayName("RegisterRequestAdapter를 반환한다.")
//            @Test
//            void it_returns_registerRequestAdapter() {
//                // given
//                HttpRequestHeader httpRequestHeader =
//                    HttpRequestHeader.from(Arrays.asList("POST /register HTTP/1.1"));
//                HttpRequest httpRequest =
//                    new HttpRequest(httpRequestHeader, new HttpRequestBody(null));
//
//                // when
//                RequestAdapter adapter = requestMapping.findAdapter(httpRequest);
//
//                // then
//                assertThat(adapter).isInstanceOf(RegisterRequestAdapter.class);
//            }
//        }
//
//        @DisplayName("어댑터를 찾을 수 없는 요청이라면")
//        @Nested
//        class Context_invalid_request {
//
//            @DisplayName("NotFoundRequestAdapter를 반환한다.")
//            @Test
//            void it_returns_notFoundRequestAdapter() {
//                // given
//                HttpRequestHeader httpRequestHeader =
//                    HttpRequestHeader.from(Arrays.asList("POST /rafder HTTP/1.1"));
//                HttpRequest httpRequest =
//                    new HttpRequest(httpRequestHeader, new HttpRequestBody(null));
//
//                // when
//                RequestAdapter adapter = requestMapping.findAdapter(httpRequest);
//
//                // then
//                assertThat(adapter).isInstanceOf(NotFoundRequestAdapter.class);
//            }
//        }
//    }
}
