package com.techcourse.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.apache.coyote.resource.ResourceParser;
import org.junit.jupiter.api.Test;
import support.TestHttpUtils;

class IndexControllerTest {

    @Test
    void 요청_URI가_index이고_GET_메서드라면_메인화면으로_이동한다() throws IOException {
        final var result = TestHttpUtils.sendGet("http://127.0.0.1:8080", "/index");

        File indexPage = ResourceParser.getRequestFile("/index.html");
        String expectBody = new String(Files.readAllBytes(indexPage.toPath()));

        assertThat(result.body()).isEqualTo(expectBody);
        assertThat(result.statusCode()).isEqualTo(200);
    }
}
