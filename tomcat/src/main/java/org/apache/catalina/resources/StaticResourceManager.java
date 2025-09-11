package org.apache.catalina.resources;

public class StaticResourceManager extends ResourceManager {

    @Override
    public String resolve(final String responsePath) {
        return responsePath;
    }
}
