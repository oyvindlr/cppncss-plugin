package hudson.plugins.helpers;

import static org.junit.Assert.*;

import org.junit.Test;

public class ScmBrowserLinkTest {

    @Test
    public void variablesAreReplacedByTheirContent() {
        ScmBrowserLink link = new ScmBrowserLink("http://rai.com/browse?file=$file&hash=$hash#$line");
        link.setCommitIdentifier("ABCD");
        link.setFilePath("/src/help.java");
        link.setLineNumber("20");
        
        assertEquals("http://rai.com/browse?file=/src/help.java&hash=ABCD#20", link.get());
    }

    @Test
    public void backslashesAreReplacedByForwardSlashes() {
        ScmBrowserLink link = new ScmBrowserLink("$file");
        link.setFilePath("\\path\\to\\file.txt");
        
        assertEquals("/path/to/file.txt", link.get());
    }
}
