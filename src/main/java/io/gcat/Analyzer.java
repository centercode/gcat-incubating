package io.gcat;

public interface Analyzer {

    public void feed(String line);

    public String query(String sql);

}
