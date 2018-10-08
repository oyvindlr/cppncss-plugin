package hudson.plugins.cppncss;

import hudson.model.Action;
import hudson.model.HealthReport;
import hudson.plugins.cppncss.parser.Statistic;
import hudson.model.Run;
import hudson.plugins.cppncss.parser.StatisticsResult;
import hudson.plugins.helpers.ScmBrowserLink;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 * TODO javadoc.
 * 
 * @author Stephen Connolly
 * @since 08-Jan-2008 21:15:05
 */
public class CppNCSSBuildIndividualReport extends
		AbstractBuildReport<Run<?, ?>> implements Action {

	private HealthReport healthReport;
	
	private ScmBrowserLink link;

	public CppNCSSBuildIndividualReport(StatisticsResult results,
			Integer functionCcnViolationThreshold,
			Integer functionNcssViolationThreshold) {
		super(results, functionCcnViolationThreshold,
				functionNcssViolationThreshold);
	}

	/**
	 * Write-once setter for property 'build'.
	 * 
	 * @param build
	 *            The value to set the build to.
	 */
	@Override
	public synchronized void setBuild(Run<?, ?> build) {
		super.setBuild(build);
		if (this.getBuild() != null) {
			getResults().setOwner(this.getBuild());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public HealthReport getBuildHealth() {
		return healthReport;
	}

	public void setBuildHealth(HealthReport healthReport) {
		this.healthReport = healthReport;
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

	public AbstractBuildReport getDynamic(String name, StaplerRequest req,
			StaplerResponse rsp) {
	    StatisticsResult fileResult = getResults().singleFileResult(name); 
	    CppNcssBuildFunctionIndividualReport cppFunction = new CppNcssBuildFunctionIndividualReport(
					fileResult, getFunctionCcnViolationThreshold(),
					getFunctionNcssViolationThreshold());
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
			cppFunction.setBuild(this.getBuild());
			cppFunction.setFilereport(this);
			return cppFunction;
		} else {
			return this;
		}
	}
}
