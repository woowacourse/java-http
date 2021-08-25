package nextstep.jwp.framework.infrastructure.mapping;

import java.lang.annotation.Annotation;
import java.util.Objects;

public class Key {

    private final String url;
    private final Class<? extends Annotation> annotation;

    public Key(String url, Class<? extends Annotation> annotation) {
        this.url = url;
        this.annotation = annotation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Key key = (Key) o;
        return Objects.equals(url, key.url) && Objects
            .equals(annotation, key.annotation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, annotation);
    }
}
