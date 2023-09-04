package org.apache.coyote.detector;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.NoSuchElementException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FileDetectorTest {

  @Test
  @DisplayName("detect() : 주어진 위치에 resource가 존재하지 않으면 NoSuchElementException가 발생할 수 있다.")
  void test_detect_NoSuchElementException() throws Exception {
    //given
    final String resourcePath = "aaaasdfkl;j;";

    //when & then
    assertThatThrownBy(() -> FileDetector.detect(resourcePath))
        .isInstanceOf(NoSuchElementException.class);
  }

  @Test
  @DisplayName("detect() : 주어진 위치에 resource가 존재하면 정상적으로 수행된다.")
  void test_detect() throws Exception {
    //given
    final String resourcePath = "static/index.html";

    //when & then
    assertDoesNotThrow(() -> FileDetector.detect(resourcePath));
  }
}
