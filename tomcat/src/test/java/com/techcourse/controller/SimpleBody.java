package com.techcourse.controller;

import jakarta.http.HttpBody;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

class SimpleBody implements HttpBody {

    private final Map<String, String> data;

    public SimpleBody(HashMap<String, String> data) {
        this.data = data;
    }

    @Override
    public Optional<String> get(String key) {
        return Optional.ofNullable(data.get(key));
    }

    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }

    @Override
    public int getSize() {
        return data.size();
    }
}
