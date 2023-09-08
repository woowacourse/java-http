package org.apache.coyote.http11.auth;

import java.util.List;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.RequestHeader;
import org.apache.coyote.http11.request.line.HttpMethod;
import org.apache.coyote.http11.request.line.RequestLine;
import org.apache.coyote.http11.response.ResponseEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.apache.coyote.http11.request.line.HttpMethod.GET;
import static org.apache.coyote.http11.request.line.HttpMethod.POST;
import static org.apache.coyote.http11.response.HttpStatus.CONFLICT;
import static org.apache.coyote.http11.response.HttpStatus.FOUND;
import static org.apache.coyote.http11.response.HttpStatus.OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class RegisterServiceTest {

    private final RegisterService registerService = new RegisterService();

    @Nested
    class path가_register면 {

        public RequestLine requestLine_생성(HttpMethod httpMethod, String defaultPath) {
            return RequestLine.from(httpMethod.name() + " " + defaultPath + " " + "HTTP/1.1");
        }

        public RequestBody requestBody_생성() {
            String body = "account=베베&password=password&email=mazzi%40woowahan.com";
            return RequestBody.from(body);
        }

        @Nested
        class HTTP_METHOD_GET {

            @Test
            @DisplayName("REGISTER Response를 반환한다.")
            void getRegisterResponseEntity() {
                // given
                RequestLine requestLine = requestLine_생성(GET, "/register");
                RequestBody requestBody = requestBody_생성();

                // when
                ResponseEntity response = registerService.register(requestLine, requestBody);

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
                RequestBody requestBody = requestBody_생성();

                // when
                ResponseEntity response = registerService.register(requestLine, requestBody);

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
                InMemoryUserRepository.deleteAll();
                RequestLine requestLine = requestLine_생성(POST, "/register");
                RequestBody requestBody = requestBody_생성();

                // when
                ResponseEntity response = registerService.register(requestLine, requestBody);

                // then
                assertAll(
                        () -> assertThat(response.getHttpStatus()).isEqualTo(FOUND),
                        () -> assertThat(response.getLocation()).isEqualTo("/index.html")
                );
            }

        }

    }

}
