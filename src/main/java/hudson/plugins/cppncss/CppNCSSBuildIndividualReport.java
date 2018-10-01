package hudson.plugins.cppncss;

import hudson.model.Action;
import hudson.model.HealthReport;
import hudson.model.Run;
import hudson.plugins.cppncss.parser.Statistic;
import hudson.plugins.cppncss.parser.StatisticsResult;
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
	
	private String escapeName(String name) {
	    return name.replace(':', '.').replace('\\', '.').replace('/', '.');
	}
	
	private StatisticsResult singleFileResult(String name) {
	    StatisticsResult result = new StatisticsResult();
	    Predicate<Statistic> fileNamesMatch = s -> escapeName(s.getParentElement()).contains(name); 
        Collection<Statistic> singleFileFunctionStats = getResults().getFunctionResults().stream()
                .filter(fileNamesMatch).collect(Collectors.toList());
        fileNamesMatch = s -> escapeName(s.getName()).contains(name);
        Collection<Statistic> singleFileFileStats = getResults().getFileResults().stream()
                .filter(fileNamesMatch).collect(Collectors.toList());
        result.setFunctionResults(singleFileFunctionStats);
        result.setFileResults(singleFileFileStats);
        return result;
    }

	public AbstractBuildReport getDynamic(String name, StaplerRequest req,
			StaplerResponse rsp) {
//		if (cppFunction == null) {
	    CppNcssBuildFunctionIndividualReport cppFunction = new CppNcssBuildFunctionIndividualReport(
					singleFileResult(name), getFunctionCcnViolationThreshold(),
					getFunctionNcssViolationThreshold());
        //CppNcssBuildFunctionIndividualReport cppFunction = new CppNcssBuildFunctionIndividualReport(
        //getResults(), getFunctionCcnViolationThreshold(),
        //getFunctionNcssViolationThreshold());
	    
	//	}
		if (name.length() >= 1) {
			cppFunction.setFileName(name);
			cppFunction.setBuild(this.getBuild());
			cppFunction.setFilereport(this);
			return cppFunction;
		} else {
			return this;
		}
	}
}
