package org.apache.coyote.http11.request.handler;

import java.util.List;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.auth.Cookie;
import org.apache.coyote.http11.auth.Session;
import org.apache.coyote.http11.auth.SessionRepository;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.request.body.RequestBody;
import org.apache.coyote.http11.request.header.RequestHeader;
import org.apache.coyote.http11.request.line.HttpMethod;
import org.apache.coyote.http11.request.line.RequestLine;
import org.apache.coyote.http11.response.ResponseEntity;
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

class RequestHandlerTest {

    private final RequestHandler requestHandler = new RequestHandler();

    @BeforeEach
    void setUp() {
        InMemoryUserRepository.deleteAll();
    }

    @Nested
    class path에_따라_다른_ResponseEntity를_반환한다 {

        public RequestLine requestLine_생성(HttpMethod httpMethod, String defaultPath) {
            return RequestLine.from(httpMethod.name() + " " + defaultPath + " " + "HTTP/1.1");
        }

        public RequestHeader requestHeader_생성() {
            List<String> requests = List.of(
                    "Host: www.test01.com",
                    "Accept: image/gif, image/jpeg, */*",
                    "Accept-Language: en-us",
                    "Accept-Encoding: gzip, deflate",
                    "User-Agent: Mozilla/4.0",
                    "Content-Length: 35"
            );

            return RequestHeader.from(requests);
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
                    RequestHeader requestHeader = requestHeader_생성();
                    RequestBody requestBody = requestBody_생성();
                    Request request = Request.of(requestLine, requestHeader, requestBody);
                    SessionRepository.clearSessions();

                    // when
                    ResponseEntity response = requestHandler.getResponse(request);

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
                    RequestHeader requestHeader = requestHeader_생성();
                    RequestBody requestBody = requestBody_생성();
                    Request request = Request.of(requestLine, requestHeader, requestBody);

                    // when
                    ResponseEntity response = requestHandler.getResponse(request);

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
                    RequestHeader requestHeader = requestHeader_생성();
                    RequestBody requestBody = requestBody_생성();
                    Request request = Request.of(requestLine, requestHeader, requestBody);

                    // when
                    ResponseEntity response = requestHandler.getResponse(request);

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
                    RequestHeader requestHeader = requestHeader_생성();
                    RequestBody requestBody = requestBody_생성();
                    Request request = Request.of(requestLine, requestHeader, requestBody);

                    // when
                    ResponseEntity response = requestHandler.getResponse(request);

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
                    RequestHeader requestHeader = requestHeader_생성();
                    RequestBody requestBody = requestBody_생성();
                    Request request = Request.of(requestLine, requestHeader, requestBody);

                    // when
                    ResponseEntity response = requestHandler.getResponse(request);

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
                    RequestHeader requestHeader = requestHeader_생성();
                    RequestBody requestBody = requestBody_생성();
                    Request request = Request.of(requestLine, requestHeader, requestBody);

                    // when
                    ResponseEntity response = requestHandler.getResponse(request);

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
