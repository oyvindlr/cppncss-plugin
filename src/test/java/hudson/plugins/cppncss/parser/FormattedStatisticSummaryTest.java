package hudson.plugins.cppncss.parser;

import static org.junit.Assert.*;

import org.junit.Test;

public class FormattedStatisticSummaryTest {

    @Test
    public void getHtmlSummaryReportsCorrectAbsoluteAndDifferences() {
        FormattedStatisticSummary fss = new FormattedStatisticSummary(10, 11, 12, 7, 6, 14);
        assertEquals("<ul><li>CCN: 7 (-3)</li><li>Functions: 6 (-5)</li><li>NCSS: 14 (+2)</li></ul>",
                fss.getHtmlSummary());
    }

    @Test
    public void getHtmlSummaryShowsNoChangeText() {
        FormattedStatisticSummary fss = new FormattedStatisticSummary(10, 11, 12, 10, 11, 12);
        assertEquals(
                "<ul><li>CCN: 10 (No change)</li><li>Functions: 11 (No change)</li><li>NCSS: 12 (No change)</li></ul>",
                fss.getHtmlSummary());
    }

}
