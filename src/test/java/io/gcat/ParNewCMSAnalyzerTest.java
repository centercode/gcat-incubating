package io.gcat;

import io.gcat.util.DateUtil;
import org.junit.Test;

public class ParNewCMSAnalyzerTest {

    ParNewCMSAnalyzer analyzer = new ParNewCMSAnalyzer(null, null);

    @Test
    public void feed() throws Exception {
        String l = "2019-05-05T15:52:07.063+0800: 3790784.573: " +
                "[GC (Allocation Failure) 2019-05-05T15:52:07.064+0800: 3790784.573: " +
                "[ParNew: 1704620K->19722K(1887488K), 0.0293905 secs] " +
                "13109374K->11433651K(31247616K), 0.0297339 secs] " +
                "[Times: user=0.56 sys=0.00, real=0.03 secs]";
        analyzer.feed(l);
        analyzer.feed(l);
        analyzer.query(null);
    }

    @Test
    public void dateFormatTest() throws Exception {
        String dt =
//                "2019-05-05T11:46:06Z";
//                "2019-05-05T11:46:06.001Z";
//                "2019-05-05T11:46:06+0800";
                "2019-05-05T11:46:06.632+0800";
        System.out.println(DateUtil.parse(dt));
    }
}