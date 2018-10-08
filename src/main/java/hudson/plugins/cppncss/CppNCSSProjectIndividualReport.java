package hudson.plugins.cppncss;

import hudson.model.Job;
import hudson.model.ProminentProjectAction;
import hudson.plugins.cppncss.parser.StatisticsResult;
import hudson.plugins.helpers.AbstractProjectAction;
import hudson.plugins.helpers.ScmBrowserLink;

import javax.annotation.CheckForNull;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 * TODO javadoc.
 * 
 * @author Stephen Connolly
 * @since 08-Jan-2008 22:05:48
 */
public class CppNCSSProjectIndividualReport extends
		AbstractProjectReport<Job<?, ?>> implements
		ProminentProjectAction {

	private Integer functionCcnViolationThreshold;
	private Integer functionNcssViolationThreshold;
	
	@CheckForNull
	private ScmBrowserLink link;

	public CppNCSSProjectIndividualReport(Job<?, ?> project,
			Integer functionCcnViolationThreshold,
			Integer functionNcssViolationThreshold) {
		super(project, functionCcnViolationThreshold,
				functionNcssViolationThreshold);
		this.functionCcnViolationThreshold = functionCcnViolationThreshold;
		this.functionNcssViolationThreshold = functionNcssViolationThreshold;
	}

	protected Class<? extends AbstractBuildReport> getBuildActionClass() {
		return CppNCSSBuildIndividualReport.class;
	}
	
	   
    public void setScmBrowserLink(ScmBrowserLink link) {
        this.link = link;
    }
    
    public boolean hasScmBrowserLink() {
        return link != null;
    }

    public String getLinkToLineNumber(String lineNumber) {     
        if (link != null) {
            link.setLineNumber(lineNumber);
            return link.get();
        }
        return "";
    }

    @Override
    public AbstractProjectAction getDynamic(String name, StaplerRequest req, StaplerResponse rsp) {
        StatisticsResult fileResult = getResults().singleFileResult(name);
        CppNCSSProjectFunctionIndividualReport cppFunction = new CppNCSSProjectFunctionIndividualReport(fileResult,
                getProject(), functionCcnViolationThreshold, functionNcssViolationThreshold);
        if (name.length() >= 1) {
            String fileName = name;
            if (fileResult.getFileResults().iterator().hasNext()) {
                fileName = fileResult.getFileResults().iterator().next().getName();
            }
            if (link != null) {
                link.setFilePath(fileName);
            }
            cppFunction.setScmBrowserLink(link);
            cppFunction.setFileName(fileName);
            cppFunction.setFilereport(this);
            return cppFunction;
        } else {
            return this;
		}
	}

}
