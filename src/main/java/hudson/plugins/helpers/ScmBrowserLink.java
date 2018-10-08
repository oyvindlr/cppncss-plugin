package hudson.plugins.helpers;

import java.io.Serializable;

public class ScmBrowserLink implements Serializable{
    
    private static final long serialVersionUID = 5629395658716950097L;
    private final String urlWithVariables;
    private String commitId;
    private String filePath;
    private String lineNumber;
    
    /** Create a new browser link
     * 
     * @param urlWithVariables The link to the SCM browser, but with variables $hash, $file and $line
     * where the filename, line number and commit ID (typically commit hash) will go
     * E.g: http://stash.omnicorp.com/browse/$file?at=$hash#$line
     */
    public ScmBrowserLink(String urlWithVariables) {
        this.urlWithVariables = urlWithVariables; 
    }
    
    /** Create a new browser link
     * 
     * @param urlWithVariables The link to the SCM browser, but with variables $hash, $file and $line
     * where the filename, line number and commit ID (typically commit hash) will go
     * E.g: http://stash.omnicorp.com/browse/$file?at=$hash#$line
     * @param commitId The commit identifier, e.g git commit hash
     */
    public ScmBrowserLink(String urlWithVariables, String commitId) {
        this(urlWithVariables); 
        this.commitId = commitId;
    }
        
    /**
     * 
     * @param commitIdentifier Typically the git commit hash
     */
    public void setCommitIdentifier(String commitIdentifier) {
        this.commitId = commitIdentifier;
    }
    
    public void setFilePath(String filePath) {
        this.filePath = replaceBackslashes(filePath);
    }
    
    /** Replace backslashes with forward slashes, in case this is a windows path
     */
    private String replaceBackslashes(String s) {
       return s.replaceAll("\\\\", "/");
    }

    public void setLineNumber(String lineNumber) {
        this.lineNumber = lineNumber;
    }
    
    public String get() {
        String ret = urlWithVariables;
        if (commitId != null) {
            ret = ret.replaceAll("\\$hash", commitId);
        }
        if (filePath != null) {
            ret = ret.replaceAll("\\$file", filePath);
        }
        if (lineNumber != null) {
            ret = ret.replaceAll("\\$line", lineNumber);
        }        
        return ret;
    }
    
    @Override
    public String toString() {
        return get();
    }
}
