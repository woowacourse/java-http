package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StaticResourceLoaderTest {

    @Test
    void root() throws IOException {
        // given
        final StaticResourceLoader loader = new StaticResourceLoader();

        // when
        final StaticResource staticResource = loader.load("/");

        // then
        assertThat(staticResource.getBody()).isEqualTo("Hello world!");
        assertThat(staticResource.getContentType()).isEqualTo("text/html");
    }

    @Test
    void staticResource() throws IOException {
        // given
        final StaticResourceLoader loader = new StaticResourceLoader();

        // when
        final StaticResource staticResource = loader.load("/index.html");

        // then
        assertThat(staticResource.getBody()).contains("<!DOCTYPE html>");
        assertThat(staticResource.getContentType()).isEqualTo("text/html");
    }

    @Test
    void htmlWithoutExtension() throws IOException {
        // given
        final StaticResourceLoader loader = new StaticResourceLoader();

        // when
        final StaticResource staticResource = loader.load("/login");

        // then
        assertThat(staticResource.getBody()).contains("<!DOCTYPE html>");
        assertThat(staticResource.getContentType()).isEqualTo("text/html");
    }

    @Test
    void contentType() throws IOException {
        // given
        final StaticResourceLoader loader = new StaticResourceLoader();

        // when
        final StaticResource css = loader.load("/css/styles.css");
        final StaticResource javascript = loader.load("/assets/chart-area.js");

        // then
        assertThat(css.getContentType()).isEqualTo("text/css");
        assertThat(javascript.getContentType()).isEqualTo("application/javascript");
    }

    @Test
    void notFound() {
        // given
        final StaticResourceLoader loader = new StaticResourceLoader();

        // when & then
        assertThatThrownBy(() -> loader.load("/not-found.html"))
                .isInstanceOf(FileNotFoundException.class)
                .hasMessage("/not-found.html");
    }

    @Test
    void pathTraversal() {
        // given
        final StaticResourceLoader loader = new StaticResourceLoader();

        // when & then
        assertThatThrownBy(() -> loader.load("/../nextstep.txt"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("정적 리소스 루트를 벗어난 경로");
    }

    @Test
    void encodedPathTraversal() {
        // given
        final StaticResourceLoader loader = new StaticResourceLoader();

        // when & then
        assertThatThrownBy(() -> loader.load("/%2e%2e/nextstep.txt"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("정적 리소스 루트를 벗어난 경로");
    }

    @Test
    void jarResource(@TempDir Path tempDirectory) throws IOException {
        // given
        final String expectedBody = "<html>안녕하세요</html>";
        final Path jarPath = tempDirectory.resolve("static-resources.jar");
        writeJarResource(jarPath, "static/index.html", expectedBody);
        final URI jarUri = jarPath.toUri();
        final URL jarUrl = jarUri.toURL();

        final StaticResource staticResource;

        // when
        try (URLClassLoader classLoader = new URLClassLoader(new URL[]{jarUrl}, null)) {
            final StaticResourceLoader loader = new StaticResourceLoader(classLoader);
            staticResource = loader.load("/index.html");
        }

        // then
        assertThat(staticResource.getBody()).isEqualTo(expectedBody);
        assertThat(staticResource.getContentType()).isEqualTo("text/html");
    }

    private void writeJarResource(Path jarPath, String resourcePath, String body) throws IOException {
        final OutputStream outputStream = Files.newOutputStream(jarPath);

        try (JarOutputStream jarOutputStream = new JarOutputStream(outputStream)) {
            final JarEntry jarEntry = new JarEntry(resourcePath);
            jarOutputStream.putNextEntry(jarEntry);
            jarOutputStream.write(body.getBytes(StandardCharsets.UTF_8));
            jarOutputStream.closeEntry();
        }
    }
}
