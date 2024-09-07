package org.apache.catalina.session;

@FunctionalInterface
public interface SessionGenerator {

    Session create();
}
