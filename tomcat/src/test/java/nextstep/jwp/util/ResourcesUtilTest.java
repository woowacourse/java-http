package nextstep.jwp.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nextstep.jwp.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;

class ResourcesUtilTest {

    @Test
    void 파일이름을_읽어_파일정보를_반환한다() {
        String fileName = "/index.html";
        String actual = ResourcesUtil.readStaticResource(fileName, this.getClass());

        assertThat(actual).isNotNull();
    }

    @Test
    void 없는_파일이름으로_파일정보를_반환하는_경우_예외가_발생한다() {
        String fileName = "notFound.html";

        assertThatThrownBy(() -> ResourcesUtil.readStaticResource(fileName, this.getClass()))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
