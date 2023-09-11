package org.apache.coyote.http11.request.handler;

import java.util.List;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.auth.SessionRepository;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.RequestHandler;
import org.apache.coyote.http11.request.HttpHeader;
import org.apache.coyote.http11.request.line.HttpMethod;
import org.apache.coyote.http11.request.line.RequestLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.apache.coyote.http11.request.line.HttpMethod.GET;
import static org.apache.coyote.http11.request.line.HttpMethod.POST;
import static org.apache.coyote.http11.response.HttpStatus.CONFLICT;
import static org.apache.coyote.http11.response.HttpStatus.FOUND;
import static org.apache.coyote.http11.response.HttpStatus.OK;
import static org.apache.coyote.http11.response.HttpStatus.UNAUTHORIZED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class HttpRequestHandlerTest {

    private final RequestHandler requestHandler = new RequestHandler();

    @BeforeEach
    void setUp() {
        InMemoryUserRepository.deleteAll();
    }

    @Nested
    class path에_따라_다른_Http_ResponseEntity를_반환한다 {

        public RequestLine requestLine_생성(HttpMethod httpMethod, String defaultPath) {
            return RequestLine.from(httpMethod.name() + " " + defaultPath + " " + "HTTP/1.1");
        }

        public HttpHeader requestHeader_생성() {
            List<String> requests = List.of(
                    "Host: www.test01.com",
                    "Accept: image/gif, image/jpeg, */*",
                    "Accept-Language: en-us",
                    "Accept-Encoding: gzip, deflate",
                    "User-Agent: Mozilla/4.0",
                    "Content-Length: 35"
            );

            return HttpHeader.from(requests);
        }

        public RequestBody requestBody_생성() {
            String body = "account=베베&password=password&email=mazzi%40woowahan.com";
            return RequestBody.from(body);
        }

        @Nested
        class path가_login이라면 {

            @Nested
            class HTTP_METHOD_GET {

                @Test
                @DisplayName("로그인을 하지 않은 상태일 때 LOGIN Response를 반환한다.")
                void getLoginResponseEntity() {
                    // given
                    RequestLine requestLine = requestLine_생성(GET, "/login");
                    HttpHeader httpHeader = requestHeader_생성();
                    RequestBody requestBody = requestBody_생성();
                    HttpRequest httpRequest = HttpRequest.of(requestLine, httpHeader, requestBody);
                    SessionRepository.clearSessions();

                    // when
                    HttpResponse response = requestHandler.getResponse(httpRequest);

                    // then
                    assertAll(
                            () -> assertThat(response.getHttpStatus()).isEqualTo(OK),
                            () -> assertThat(response.getLocation()).isEqualTo("/login.html")
                    );
                }

            }

            @Nested
            class HTTP_METHOD_POST {

                @Test
                @DisplayName("FOUND Response를 반환한다.")
                void getFoundResponseEntity() {
                    // given
                    InMemoryUserRepository.save(new User(1L, "베베", "password", "rltgjqmduftlagl@gmail.com"));

                    RequestLine requestLine = requestLine_생성(POST, "/login");
                    HttpHeader httpHeader = requestHeader_생성();
                    RequestBody requestBody = requestBody_생성();
                    HttpRequest httpRequest = HttpRequest.of(requestLine, httpHeader, requestBody);

                    // when
                    HttpResponse response = requestHandler.getResponse(httpRequest);

                    // then
                    assertAll(
                            () -> assertThat(response.getHttpStatus()).isEqualTo(FOUND),
                            () -> assertThat(response.getLocation()).isEqualTo("/index.html")
                    );
                }

                @Test
                @DisplayName("UNAUTHORIZED Response를 반환한다.")
                void getUnauthorizedResponseEntity() {
                    // given
                    InMemoryUserRepository.save(new User(1L, "페페", "password", "rltgjqmduftlagl@gmail.com"));

                    RequestLine requestLine = requestLine_생성(POST, "/login");
                    HttpHeader httpHeader = requestHeader_생성();
                    RequestBody requestBody = requestBody_생성();
                    HttpRequest httpRequest = HttpRequest.of(requestLine, httpHeader, requestBody);

                    // when
                    HttpResponse response = requestHandler.getResponse(httpRequest);

                    // then
                    assertAll(
                            () -> assertThat(response.getHttpStatus()).isEqualTo(UNAUTHORIZED),
                            () -> assertThat(response.getLocation()).isEqualTo("/401.html")
                    );
                }

            }

        }

        @Nested
        class path가_register면 {

            @Nested
            class HTTP_METHOD_GET {

                @Test
                @DisplayName("REGISTER Response를 반환한다.")
                void getRegisterResponseEntity() {
                    // given
                    RequestLine requestLine = requestLine_생성(GET, "/register");
                    HttpHeader httpHeader = requestHeader_생성();
                    RequestBody requestBody = requestBody_생성();
                    HttpRequest httpRequest = HttpRequest.of(requestLine, httpHeader, requestBody);

                    // when
                    HttpResponse response = requestHandler.getResponse(httpRequest);

                    // then
                    assertAll(
                            () -> assertThat(response.getHttpStatus()).isEqualTo(OK),
                            () -> assertThat(response.getLocation()).isEqualTo("/register.html")
                    );
                }

            }

            @Nested
            class HTTP_METHOD_POST {

                @Test
                @DisplayName("CONFLICT Response를 반환한다.")
                void getConflictResponseEntity() {
                    // given
                    InMemoryUserRepository.save(new User(1L, "베베", "password", "rltgjqmduftlagl@gmail.com"));

                    RequestLine requestLine = requestLine_생성(POST, "/register");
                    HttpHeader httpHeader = requestHeader_생성();
                    RequestBody requestBody = requestBody_생성();
                    HttpRequest httpRequest = HttpRequest.of(requestLine, httpHeader, requestBody);

                    // when
                    HttpResponse response = requestHandler.getResponse(httpRequest);

                    // then
                    assertAll(
                            () -> assertThat(response.getHttpStatus()).isEqualTo(CONFLICT),
                            () -> assertThat(response.getLocation()).isEqualTo("/409.html")
                    );
                }

                @Test
                @DisplayName("FOUND Response를 반환한다.")
                void getFoundResponseEntity() {
                    // given
                    RequestLine requestLine = requestLine_생성(POST, "/register");
                    HttpHeader httpHeader = requestHeader_생성();
                    RequestBody requestBody = requestBody_생성();
                    HttpRequest httpRequest = HttpRequest.of(requestLine, httpHeader, requestBody);

                    // when
                    HttpResponse response = requestHandler.getResponse(httpRequest);

                    // then
                    assertAll(
                            () -> assertThat(response.getHttpStatus()).isEqualTo(FOUND),
                            () -> assertThat(response.getLocation()).isEqualTo("/index.html")
                    );
                }

            }

        }

    }

}
