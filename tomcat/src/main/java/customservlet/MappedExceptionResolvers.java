package customservlet;

import java.util.Map;
import java.util.Map.Entry;

public class MappedExceptionResolvers {

    private final Map<Class<? extends RuntimeException>, ExceptionResolver> exceptionResolvers;

    private MappedExceptionResolvers(
            final Map<Class<? extends RuntimeException>, ExceptionResolver> exceptionResolvers) {
        this.exceptionResolvers = exceptionResolvers;
    }

    public static MappedExceptionResolvers from(
            final Map<Class<? extends RuntimeException>, ExceptionResolver> exceptionResolvers) {
        return new MappedExceptionResolvers(exceptionResolvers);
    }

    public ExceptionResolver get(final Exception exception) {
        return exceptionResolvers.entrySet()
                .stream()
                .filter(it -> it.getKey().isInstance(exception))
                .map(Entry::getValue)
                .findFirst()
                .orElseThrow(() -> new RuntimeException(exception.getClass().getName() + "를 처리할 Resolver가 존재하지 않습니다."));
    }
}
