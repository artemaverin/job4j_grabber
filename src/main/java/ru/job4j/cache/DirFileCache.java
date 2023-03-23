package ru.job4j.cache;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DirFileCache extends AbstractCache<String, String> {

    private final String cachingDir;

    public DirFileCache(String cachingDir) {
        this.cachingDir = cachingDir;
    }

    public String getCachingDir() {
        return cachingDir;
    }

    @Override
    protected String load(String key) {
        String data = "";
        try {
            data = Files.readString(Path.of(getCachingDir() + key));
            put(key, data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
}
