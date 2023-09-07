package org.apache.coyote.adapter;

import static org.apache.coyote.FixtureFactory.DEFAULT_HEADERS;
import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.FixtureFactory;
import org.apache.coyote.request.Request;
import org.apache.coyote.view.Resource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StringAdapterTest {

    @Test
    @DisplayName("hello world라는 정보가 담긴 response가 생성된다.")
    void doHandle() {
        Request request = FixtureFactory.getRequest("/", DEFAULT_HEADERS);

        String expected = "Hello world!";

        Resource actual = new StringAdapter().adapt(request);

        assertThat(actual.getValue()).contains(expected);
    }
}
