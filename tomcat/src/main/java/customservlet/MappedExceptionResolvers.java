package customservlet;

import customservlet.exception.NotFoundExceptionResolverException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class MappedExceptionResolvers {

    private static final MappedExceptionResolvers mappedExceptionResolvers = new MappedExceptionResolvers();
    private static final Map<Class<? extends RuntimeException>, ExceptionResolver> urlMap = new HashMap<>();

    private MappedExceptionResolvers() {
    }

    public static MappedExceptionResolvers getInstance() {
        return mappedExceptionResolvers;
    }

    public ExceptionResolver getResolver(final Exception exception) {
        return urlMap.entrySet()
                .stream()
                .filter(it -> it.getKey().isInstance(exception))
                .map(Entry::getValue)
                .findFirst()
                .orElseThrow(() -> new NotFoundExceptionResolverException(exception.getClass()));
    }

    public void addResolver(final Class<? extends RuntimeException> exceptionClass,
                            final ExceptionResolver exceptionResolver) {
        urlMap.put(exceptionClass, exceptionResolver);
    }
}
