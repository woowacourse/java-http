package org.qupring;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.qupring.mvc.ApplicationScanner;

public class ApplicationScannerTest {

    @Test
    void 스캐너_테스트() {
        // given
        ApplicationScanner scanner = new ApplicationScanner();

        // when
        Map<String, String> resources = scanner.scanForResources();

        // then
        assertThat(resources.size()).isNotEqualTo(0);
    }
}
