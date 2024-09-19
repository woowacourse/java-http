package org.apache.coyote.http11;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.InputStream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.FakeRequests;
import support.StubSocket;

class HttpReaderTest {

    @DisplayName("request가 주어졌을 때 한 줄씩 파싱할 수 있다")
    @Test
    void readLines() throws IOException {
        StubSocket socket = new StubSocket(FakeRequests.teLineRequest);
        HttpReader httpReader = new HttpReader(socket.getInputStream());
        Assertions.assertThat(httpReader.readLines()).hasSize(10);
    }
}
