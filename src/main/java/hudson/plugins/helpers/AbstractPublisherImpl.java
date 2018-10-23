package hudson.plugins.helpers;

import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.Recorder;
import jenkins.tasks.SimpleBuildStep;

import java.io.IOException;

/**
 * An abstract Publisher that is designed to work with a Ghostwriter.
 *
 * @author Stephen Connolly
 * @since 28-Jan-2008 22:32:46
 */
public abstract class AbstractPublisherImpl extends Recorder implements SimpleBuildStep {

    /**
     * Creates the configured Ghostwriter.
     *
     * @return returns the configured Ghostwriter.
     */
    protected abstract Ghostwriter newGhostwriter();

    @Override
    public void perform(Run<?,?> run, FilePath workspace, Launcher launcher, TaskListener listener) {
        try {
            BuildProxy.doPerform(newGhostwriter(), run, workspace, listener);
        } catch (IOException | InterruptedException e) {
            run.setResult(Result.FAILURE);
            e.printStackTrace(listener.getLogger());
        }
    }
}