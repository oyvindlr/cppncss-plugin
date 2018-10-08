package hudson.plugins.cppncss;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import hudson.EnvVars;
import hudson.Extension;
import org.jenkinsci.Symbol;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.Environment;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;

import hudson.plugins.helpers.BuildProxy;
import hudson.plugins.helpers.Ghostwriter;
import hudson.plugins.helpers.ScmBrowserLink;
import hudson.plugins.helpers.health.HealthMetric;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import jenkins.tasks.SimpleBuildStep;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.beanutils.ConvertUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

/**
 * TODO javadoc.
 *
 * @author Stephen Connolly
 * @since 08-Jan-2008 21:24:06
 */
public class CppNCSSPublisher extends Recorder implements SimpleBuildStep {

    private String reportFilenamePattern;
    private Integer functionCcnViolationThreshold = 10;
    private Integer functionNcssViolationThreshold = 100;
    private CppNCSSHealthTarget[] targets;
    private String linkText;

    @DataBoundConstructor
    public CppNCSSPublisher(String reportFilenamePattern, Integer functionCcnViolationThreshold, Integer functionNcssViolationThreshold, CppNCSSHealthTarget[] targets) {
		reportFilenamePattern.getClass();
        this.reportFilenamePattern = reportFilenamePattern;
        this.functionCcnViolationThreshold = functionCcnViolationThreshold;
        this.functionNcssViolationThreshold = functionNcssViolationThreshold;
        
        this.targets = targets == null ? new CppNCSSHealthTarget[0] : targets;
    }
    
    @DataBoundSetter
    public void setScmBrowserLinkText(String linkText) {
        this.linkText = linkText;
    }

    public String getReportFilenamePattern() {
        return reportFilenamePattern;
    }
    
    public String getScmBrowserLinkText() {
        return linkText;
    }

	public Integer getFunctionCcnViolationThreshold() {
		return functionCcnViolationThreshold;
	}

	public Integer getFunctionNcssViolationThreshold() {
		return functionNcssViolationThreshold;
	}

	// TODO: replace by lists
	@SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "Legacy code, suppressed due to the performance reasons")
	public CppNCSSHealthTarget[] getTargets() {
        return targets;
    }

    /**
     * {@inheritDoc}
     */
    public boolean needsToRunAfterFinalized() {
        return false;
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    private Ghostwriter newGhostwriter(ScmBrowserLink link) {
        return new CppNCSSGhostwriter(reportFilenamePattern, functionCcnViolationThreshold, functionNcssViolationThreshold, link, targets);
    }

    @Override
    public void perform(Run<?,?> run, FilePath workspace, Launcher launcher, TaskListener listener) {
        ScmBrowserLink link = createScmBrowserLink(run, listener);
        CppNCSSProjectIndividualReport report = new CppNCSSProjectIndividualReport(run.getParent(), functionCcnViolationThreshold, functionNcssViolationThreshold);
        report.setScmBrowserLink(link);
        ActionGetter getter = new ActionGetter();
        getter.addProjectAction(report);
        run.addAction(getter);
        
        try {
            BuildProxy.doPerform(newGhostwriter(link), run, workspace, listener);
        } catch (IOException | InterruptedException e) {
            run.setResult(Result.FAILURE);
            e.printStackTrace(listener.getLogger());
        }
    }

 
    private ScmBrowserLink createScmBrowserLink(Run<?, ?> run, TaskListener listener) {
        if (linkText == null || linkText.isEmpty())
            return null;
        try {
            EnvVars env = run.getEnvironment(listener);
            String hash = env.get("GIT_COMMIT");
            ScmBrowserLink link = new ScmBrowserLink(linkText, hash);
            return link;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace(listener.getLogger());
            return null;
        }
    }


    @Extension @Symbol("cppncss")
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        /**
         * {@inheritDoc}
         */
        public String getDisplayName() {
            return "Publish " + PluginImpl.DISPLAY_NAME;
        }

        static {
            ConvertUtils.register(CppNCSSHealthMetrics.CONVERTER, CppNCSSHealthMetrics.class);
        }

        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        public HealthMetric[] getMetrics() {
            return CppNCSSHealthMetrics.values();
        }
    }
    
    /** This isn't actually an action itself, but only a way to get 
     * project actions through {@link ActionGetter#getProjectActions}. Therefore all the 
     * methods implemented from {@link Action} return null.
     *
     */
    protected static class ActionGetter implements SimpleBuildStep.LastBuildAction {

        private Collection<Action> projectActions = new ArrayList<Action>();

        @Override
        public String getIconFileName() {
            return null;
        }

        @Override
        public String getDisplayName() {
            return null;
        }

        @Override
        public String getUrlName() {
            return null;
        }

        @Override
        public Collection<? extends Action> getProjectActions() {
            return projectActions;
        }

        public void addProjectAction(Action action) {
            projectActions.add(action);
        }

    }

}
