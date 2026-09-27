package org.apache.catalina.connector;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class ConnectorTest {

    @Test
    void 포트가_유효한_범위를_벗어나면_예외가_발생한다() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Connector(65536, 100, 250));
    }

    @Test
    void acceptCount가_0_이하면_예외가_발생한다() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Connector(0, 0, 250));
    }

    @Test
    void maxThreads가_0_이하면_예외가_발생한다() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Connector(0, 100, 0));
    }
}
