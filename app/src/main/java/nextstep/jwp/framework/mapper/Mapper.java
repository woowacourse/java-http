package nextstep.jwp.framework.mapper;

public interface Mapper<T, S> {
    T resolve(S s);
}
