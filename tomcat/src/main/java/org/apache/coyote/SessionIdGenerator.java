package org.apache.coyote;

@FunctionalInterface
public interface SessionIdGenerator {

    String generate();
}
