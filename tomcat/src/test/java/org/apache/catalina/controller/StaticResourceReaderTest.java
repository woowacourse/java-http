package org.apache.catalina.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StaticResourceReaderTest {

    @DisplayName("정적 리소스를 읽는다.")
    @Test
    void readResourceTest() {
        // given
        String location = "/hello.html";
        ClassLoader classLoader = getClass().getClassLoader();

        // when
        byte[] content = StaticResourceReader.readResource(classLoader, location);

        // then
        assertThat(content).isEqualTo("Hello world!\n".getBytes());
    }

    @DisplayName("정적 리소스가 없을 때 404 페이지를 읽는다.")
    @Test
    void readResourceTestWhenResourceNotFound() {
        // given
        String location = "/notfound.jpg";
        ClassLoader classLoader = getClass().getClassLoader();

        // when
        byte[] content = StaticResourceReader.readResource(classLoader, location);

        // then
        assertThat(content).contains("This requested URL was not found on this server.".getBytes());
    }
}
