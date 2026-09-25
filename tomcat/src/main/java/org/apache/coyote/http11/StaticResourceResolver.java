package org.apache.coyote.http11;

import org.apache.coyote.http11.request.requestline.RequestPath;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class StaticResourceResolver {
    private final Path root;

    public StaticResourceResolver(Path root) {
        this.root = root.toAbsolutePath().normalize();
    }

    public Optional<Path> resolve(final RequestPath requestPath) {
        final Path target = root
                .resolve(requestPath.getValue().substring(1)) // 앞의 '/' 제거: 절대경로로 해석되지 않게
                .normalize();

        if (!target.startsWith(root)) {
            return Optional.empty(); // 루트 밖으로 나감
        }
        if (!Files.isRegularFile(target)) {
            return Optional.empty();
        }
        return Optional.of(target);
    }
}
