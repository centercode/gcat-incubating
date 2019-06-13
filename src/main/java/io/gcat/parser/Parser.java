package io.gcat.parser;

public interface Parser {

    void feed(String line);

    String query(String sql);

}
