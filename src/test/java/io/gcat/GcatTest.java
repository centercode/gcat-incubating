package io.gcat;

import org.junit.Test;

import java.io.IOException;

public class GcatTest {

    @Test
    public void run() throws IOException {
        Gcat.main(new String[]{"/tmp/gc.log"});
    }
}