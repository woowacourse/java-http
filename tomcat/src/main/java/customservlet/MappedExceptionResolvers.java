package customservlet;

import customservlet.exception.NotFoundExceptionResolverException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class MappedExceptionResolvers {

    private static final MappedExceptionResolvers mappedExceptionResolvers = new MappedExceptionResolvers();
    private static final Map<Class<? extends RuntimeException>, ExceptionResolver> exceptionResolvers = new HashMap<>();

    private MappedExceptionResolvers() {
    }

    public static MappedExceptionResolvers getInstance() {
        return mappedExceptionResolvers;
    }

    public ExceptionResolver getResolver(final Exception exception) {
        return exceptionResolvers.entrySet()
                .stream()
                .filter(it -> it.getKey().isInstance(exception))
                .map(Entry::getValue)
                .findFirst()
                .orElseThrow(() -> new NotFoundExceptionResolverException(exception.getClass()));
    }

    public void addResolver(final Class<? extends RuntimeException> exceptionClass,
                            final ExceptionResolver exceptionResolver) {
        exceptionResolvers.put(exceptionClass, exceptionResolver);
    }
}
