package org.apache.support;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class FileUtilsTest {

    @Test
    void getMimeTypeFromPath() {
        final String staticFilePath = FileUtils.getStaticFilePathFromUri("/index.html");
        final String mimeType = FileUtils.getMimeTypeFromPath(staticFilePath);

        assertThat(mimeType).isEqualTo("text/html");
    }

    @Test
    void getStaticFilePathFromUri() {
        final String staticFilePath = FileUtils.getStaticFilePathFromUri("/index.html");

        assertThat(staticFilePath).endsWith("/build/resources/main/static/index.html");
    }

    @Test
    void getResourceFromUri() {
        final String path = FileUtils.getResourceFromUri("/index.html")
                .get()
                .getPath();

        assertThat(path).endsWith("/build/resources/main/static/index.html");
    }

    @Test
    void readContentByStaticFilePath() {
        final String staticFilePath = FileUtils.getStaticFilePathFromUri("/test.html");
        final String content = FileUtils.readContentByStaticFilePath(staticFilePath);

        assertThat(content).isEqualTo("testHtml");
    }
}