package io.gcat.parser;

import io.gcat.util.Utils;
import org.junit.Test;

public class CMSParNewParserTest {

    CMSParNewParser parser = new CMSParNewParser(null);

    @Test
    public void feed() throws Exception {
        String l = "2019-05-05T15:52:07.063+0800: 3790784.573: " +
                "[GC (Allocation Failure) 2019-05-05T15:52:07.064+0800: 3790784.573: " +
                "[ParNew: 1704620K->19722K(1887488K), 0.0293905 secs] " +
                "13109374K->11433651K(31247616K), 0.0297339 secs] " +
                "[Times: user=0.56 sys=0.00, real=0.03 secs]";
        parser.feed(l);
        parser.feed(l);
        parser.query(null);
    }

    @Test
    public void dateFormatTest() throws Exception {
        String dt =
                "2019-05-05T11:46:06Z";
//                "2019-05-05T11:46:06.001Z";
//                "2019-05-05T11:46:06+0800";
//                "2019-05-05T11:46:06.632+0800";
        System.out.println(Utils.parse(dt));
    }
}