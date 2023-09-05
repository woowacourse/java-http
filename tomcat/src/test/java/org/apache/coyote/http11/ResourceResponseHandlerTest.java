package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ResourceResponseHandlerTest {

    private final ResourceResponseHandler resourceResponseHandler = new ResourceResponseHandler();

    @Test
    void notFoundResource() throws IOException {
        //given
        final var noResourceUri = "/hi";

        //when
        HttpResponse httpResponse = resourceResponseHandler.handleStaticResponse(noResourceUri);

        //then
        final var resource = getClass().getClassLoader().getResource("static/404.html");
        final var body = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        final var expected = HttpResponse.builder()
                .setHttpStatus(HttpStatus.NOT_FOUND)
                .setContentType(new ContentType("text/html"))
                .setBody(body)
                .build();

        assertThat(httpResponse).usingRecursiveComparison()
                .isEqualTo(expected);

    }

}
