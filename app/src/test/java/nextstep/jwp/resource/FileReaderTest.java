package nextstep.jwp.resource;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class FileReaderTest {

    @DisplayName("파일을 읽는 기능")
    @Test
    void fileReadTest() throws IOException {
        //given
        FilePath filePath = new FilePath("nextstep", "txt", "");
        //when
        FileReader fileReader = new FileReader(filePath);
        fileReader.readAllFile();
        //then
        assertThat(fileReader.readAllFile()).isEqualTo("I'm on the next Level 절대적 룰을 지켜\n"
            + "내 손을 놓지말아 결속은 나의 무기\n"
            + "광야로 걸어가 알아 내 homeground 위협에 맞서서 재껴라 재껴라 재껴라");
    }
}
