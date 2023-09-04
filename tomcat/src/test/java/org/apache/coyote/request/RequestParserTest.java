package org.apache.coyote.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.coyote.response.Resource;
import org.apache.coyote.response.ResourceType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestParserTest {

    @Test
    @DisplayName("주어진 InputStream의 정보를 파싱하여 resource 객체를 생성한다.")
    void getResource_normal() throws IOException {
        InputStream inputStream = new ByteArrayInputStream("GET /index.html HTTP/1.1".getBytes());
        RequestParser requestParser = new RequestParser(inputStream);

        Resource resource = requestParser.getResource();

        assertAll(
                () -> assertThat(resource.getUrl().getFile()).contains("/index.html"),
                () -> assertThat(resource.getResourceTypes()).contains(ResourceType.HTML.getResourceType()),
                () -> assertThat(resource.isExists()).isTrue()
        );
    }

    @Test
    @DisplayName("주어진 url이 /라면 index.html에 대한 resource를 생성한다.")
    void getResource_home() throws IOException {
        InputStream inputStream = new ByteArrayInputStream("GET / HTTP/1.1".getBytes());
        RequestParser requestParser = new RequestParser(inputStream);

        Resource resource = requestParser.getResource();

        assertAll(
                () -> assertThat(resource.getUrl().getFile()).contains("/index.html"),
                () -> assertThat(resource.getResourceTypes()).contains(ResourceType.HTML.getResourceType()),
                () -> assertThat(resource.isExists()).isTrue()
        );
    }


    @Test
    @DisplayName("주어진 url의 파일이 없다면 url이 null이고 isExists가 false인 resource 객체가 반환한다.")
    void getResourceUrl_noFile() throws IOException {
        InputStream inputStream = new ByteArrayInputStream(
                "GET /trestset/qweqsdae/asdawdqd/qwdqweqw HTTP/1.1".getBytes());
        RequestParser requestParser = new RequestParser(inputStream);

        Resource resource = requestParser.getResource();

        assertAll(
                () -> assertThat(resource.getUrl().getFile()).contains("/404.html"),
                () -> assertThat(resource.getResourceTypes()).contains(ResourceType.HTML.getResourceType()),
                () -> assertThat(resource.isExists()).isFalse()
        );
    }
}
