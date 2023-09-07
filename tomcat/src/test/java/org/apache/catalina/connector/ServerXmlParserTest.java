package org.apache.catalina.connector;

import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ServerXmlParserTest {

    @Test
    void SERVER_XML_파일을_파싱할_수_있다() {
        // given, when, then
        assertThatCode(ServerXmlParser::parse)
                .doesNotThrowAnyException();
    }
}
