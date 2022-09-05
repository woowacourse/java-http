package org.apache.coyote.http11.http11handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.util.Map;
import org.apache.coyote.ExtensionContentType;
import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Http11HandlerTest {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    @DisplayName("defaultPageHandler extractElements 테스트")
    @Test
    void extractElements_DefaultPageHandler() {
        Http11Handler http11Handler = new DefaultPageHandler();
        Map<String, String> elements = http11Handler.handle(log, "/");

        assertAll(
                () -> assertThat(elements.get("Content-Type")).isEqualTo(ExtensionContentType.HTML.getContentType()),
                () -> assertThat(elements.get("Content-Length")).isEqualTo(Integer.toString("Hello world!".length())),
                () -> assertThat(elements.get("body")).isEqualTo("Hello world!")
        );
    }

    @DisplayName("staticResourceHandler extractElements 테스트")
    @Test
    void extractElements_StaticResourceHandler() {
        Http11Handler http11Handler = new IndexPageHandler();
        Map<String, String> elements = http11Handler.handle(log, "/index.html");

        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        assertAll(
                () -> assertThat(elements.get("Content-Type")).isEqualTo(ExtensionContentType.HTML.getContentType()),
                () -> assertThat(elements.get("Content-Length")).isEqualTo("5564"),
                () -> assertThat(elements.get("body")).isEqualTo(new String(Files.readAllBytes(new File(resource.getFile()).toPath())))
        );
    }
}
