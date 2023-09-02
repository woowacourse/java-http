package org.apache.coyote.http11.http;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

class RequestHeaderTest {

    @Test
    void header가_key_value로_이루어지지_않으면_예외발생() throws IOException {
        StringBuilder invalidHeader = new StringBuilder();

        invalidHeader.append("Content-Type: text/html;charset=utf-8 ").append('\n');
        invalidHeader.append("Content-Length").append('\n');
        invalidHeader.append("");

        StringReader stringReader = new StringReader(invalidHeader.toString());
        BufferedReader bufferedReader = new BufferedReader(stringReader);
//        System.out.println(bufferedReader.readLine());
        Assertions.assertThatThrownBy(() -> RequestHeader.from(bufferedReader))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
