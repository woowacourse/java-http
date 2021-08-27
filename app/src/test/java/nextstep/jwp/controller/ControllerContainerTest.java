package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import nextstep.TestUtil;
import nextstep.jwp.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ControllerContainerTest {

    @DisplayName("IndexPageController를 찾는다")
    @Test
    void findController() throws IOException {
        String firstLine = "GET /index.html HTTP/1.1";
        HttpRequest httpRequest = TestUtil.createRequest(firstLine);

        assertThat(ControllerContainer.findController(httpRequest)).isInstanceOf(IndexPageController.class);
    }
}