package org.apache.coyote.response;

import static org.apache.coyote.response.ContentType.*;
import static org.apache.coyote.response.StatusCode.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @DisplayName("302 Found 응답시에는 Redirect 헤더를 포함해서 응답한다.")
    @Test
    public void returnWithLocation() throws IOException {
        //given
        final StatusCode statusCode = FOUND;
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        final HttpResponse httpResponse = new HttpResponse(statusCode, HTML, responseBody, "/index.html");

        //when
        final String response = httpResponse.getResponse();

        //then
        assertThat(response).contains("Location: /index.html");
    }
}
