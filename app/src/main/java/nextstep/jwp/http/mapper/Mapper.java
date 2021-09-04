package nextstep.jwp.http.mapper;

public interface Mapper<T, S> {
    T resolve(S s);
}
