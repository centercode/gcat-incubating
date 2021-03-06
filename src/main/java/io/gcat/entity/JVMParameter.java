package io.gcat.entity;

import java.util.LinkedHashMap;

public class JVMParameter {
    /**
     * 1.6, 1.7, 1.8...
     **/
    private String jvmVersion;

    private LinkedHashMap<String, String> data = new LinkedHashMap<>();

    public String get(String key) {
        return data.get(key);
    }

    public Integer getInt(String key) {
        String s = data.get(key);
        if (null == s) {
            return null;
        }

        return Integer.valueOf(s);
    }

    public Long getLong(String key) {
        String s = data.get(key);
        if (null == s) {
            return null;
        }

        return Long.valueOf(s);
    }

    public Boolean is(String key) {
        String s = data.get(key);
        if (null == s) {
            return null;
        }

        return Boolean.valueOf(s);
    }

    public void put(String key, String value) {
        data.put(key, value);
    }

    public String getJvmVersion() {
        return jvmVersion;
    }

    public JVMParameter setJvmVersion(String jvmVersion) {
        this.jvmVersion = jvmVersion;
        return this;
    }
}
