package org.apache.coyote.http11.handler;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.FileHandler;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.response.ResponseEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class FrontRequestHandlerTest {

    private final FrontRequestHandler frontRequestHandler = new FrontRequestHandler(RequestHandlerMapping.init());

    @Nested
    @DisplayName("handle 메소드는")
    class Handle {

        @Test
        @DisplayName("statc file uri를 반환하는 핸들러가 실행되면 해당 파일 정보가 포함된 ResponseEntity를 반환한다.")
        void success_file() throws IOException {
            // given
            final Map<String, String> queryParams = new HashMap<>();
            queryParams.put("account", "gugu");
            queryParams.put("password", "password");

            // when
            final ResponseEntity response = frontRequestHandler.handle("/login", queryParams);

            // then
            final URL url = FileHandler.class.getClassLoader().getResource("static/login.html");
            final Path path = Path.of(url.getPath());
            final byte[] fileBytes = Files.readAllBytes(path);

            assertThat(response).extracting("httpStatus", "mimeType", "body")
                    .containsExactly(HttpStatus.OK, Files.probeContentType(path), new String(fileBytes));
        }

        @Test
        @DisplayName("입력 받은 path와 매핑되는 핸들러가 없다면 Not Fount ResponseEntity를 반환한다.")
        void success_notFountResponse() throws IOException {
            // given
            final Map<String, String> queryParams = new HashMap<>();

            // when
            final ResponseEntity response = frontRequestHandler.handle("/wrong", queryParams);

            // then
            final URL url = FileHandler.class.getClassLoader().getResource("static/404.html");
            final Path path = Path.of(url.getPath());
            final byte[] fileBytes = Files.readAllBytes(path);

            assertThat(response).extracting("httpStatus", "mimeType", "body")
                    .containsExactly(HttpStatus.NOT_FOUND, Files.probeContentType(path), new String(fileBytes));
        }

        @Test
        @DisplayName("매핑한 핸들러 실행 도중 예외가 발생하면 Server Error ResponseEntity를 반환한다.")
        void success_ServerErrorResponse() throws IOException {
            // given
            final Map<String, String> queryParams = new HashMap<>();

            // when
            final ResponseEntity response = frontRequestHandler.handle("/login", queryParams);

            // then
            final URL url = FileHandler.class.getClassLoader().getResource("static/500.html");
            final Path path = Path.of(url.getPath());
            final byte[] fileBytes = Files.readAllBytes(path);

            assertThat(response).extracting("httpStatus", "mimeType", "body")
                    .containsExactly(HttpStatus.SERVER_ERROR, Files.probeContentType(path), new String(fileBytes));
        }
    }
}