package customservlet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import customservlet.exception.NotFoundExceptionResolverException;
import nextstep.jwp.presentation.exceptionresolver.NotFoundExceptionResolver;
import org.apache.coyote.http11.exception.NotFoundException;
import org.apache.coyote.http11.exception.NotFoundResourceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MappedExceptionResolversTest {

    private MappedExceptionResolvers mappedExceptionResolvers;

    @BeforeEach
    void setUp() {
        this.mappedExceptionResolvers = MappedExceptionResolvers.getInstance();
    }

    @Test
    void 예외클래스를_처리할_Resolver를_찾는다() {
        // given
        final NotFoundExceptionResolver notFoundExceptionResolver = new NotFoundExceptionResolver();
        mappedExceptionResolvers.addResolver(NotFoundException.class, notFoundExceptionResolver);

        // when
        final ExceptionResolver actual = mappedExceptionResolvers.getResolver(new NotFoundResourceException());

        // then
        assertThat(actual).isEqualTo(notFoundExceptionResolver);
    }

    @Test
    void 예외클래스를_처리할_Resolver가_없으면_예외가_발생한다() {
        // given, when, then
        assertThatThrownBy(() -> mappedExceptionResolvers.getResolver(new NotFoundResourceException()))
                .isInstanceOf(NotFoundExceptionResolverException.class);
    }
}
