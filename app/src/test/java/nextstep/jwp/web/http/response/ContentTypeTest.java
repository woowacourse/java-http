package nextstep.jwp.web.http.response;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.jwp.resource.FileType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ContentTypeTest {

    @DisplayName("파일 타입으로 컨텐트 타입을 찾는다.")
    @Test
    void findByFileTypeTest() {
        //given
        //when
        ContentType html = ContentType.findByFileType(FileType.HTML);
        ContentType plainText = ContentType.findByFileType(FileType.PLAIN_TEXT);
        //then
        assertThat(html).isEqualTo(ContentType.HTML);
        assertThat(plainText).isEqualTo(ContentType.PLAIN_TEXT);
    }
}