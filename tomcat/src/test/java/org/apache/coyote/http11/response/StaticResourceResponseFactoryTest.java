package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StaticResourceResponseFactoryTest {

    @Test
    @DisplayName("ContentType을 생성할 수 있어야 한다")
    void testCreateContentType() {
        String pathWithExtension = "/index.html";
        StaticResourceResponseFactory factory = new StaticResourceResponseFactory(pathWithExtension);

        String result = factory.createContentType();

        assertThat(result).isEqualTo("text/html;charset=utf-8");
    }

    @Test
    @DisplayName("Static Resource 파일을 읽어올 수 있어야 한다")
    void testCreateResponseBody() throws Exception {
        String pathWithExtension = "/index.html";
        StaticResourceResponseFactory factory = new StaticResourceResponseFactory(pathWithExtension);

        String result = factory.createResponseBody();

        URL resource = getClass().getClassLoader().getResource("static/index.html");
        String expected = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(result).isEqualTo(expected);
    }
}
