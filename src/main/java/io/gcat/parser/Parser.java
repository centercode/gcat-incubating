package io.gcat.parser;

public interface Parser {

    public void feed(String line);

    public void stop();

    public String query(String sql);

}
