package com.flowdock.jenkins;

import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.PluginWrapper;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildWrapper;
import hudson.tasks.BuildWrapperDescriptor;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import jenkins.YesNoMaybe;
// import jenkins.tasks.SimpleBuildWrapper0;
import org.kohsuke.stapler.DataBoundConstructor;

public final class FlowdockBuildStart extends BuildWrapper {

    private static final Logger LOGGER = Logger.getLogger(FlowdockBuildStart.class.getName());

    private String apiUrl = "https://api.flowdock.com";

    private final String flowToken;
    private final String notificationTags;

    @DataBoundConstructor
    public FlowdockBuildStart(String flowToken, String notificationTags) {
        this.flowToken = flowToken;
        this.notificationTags = notificationTags;
    }

    @Override
    public Environment setUp(AbstractBuild build, Launcher launcher, BuildListener listener) {

        try {
            FlowdockAPI api = new FlowdockAPI(this.apiUrl, this.flowToken);
            EnvVars vars = build.getEnvironment(listener);
            ChatMessage chatMsg = ChatMessage.startBuild(build, launcher, listener);
            chatMsg.setTags(vars.expand(this.notificationTags));
            api.pushChatMessage(chatMsg);
        }
        catch(Exception e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
        }

        return new Environment() {};
    }

    public String getFlowToken() {
        return flowToken;
    }

    public String getNotificationTags() {
        return notificationTags;
    }

    @Extension
    public static class DescriptorImpl extends BuildWrapperDescriptor {
        @Override
        public boolean isApplicable(AbstractProject<?, ?> item) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Send FlowDock notification on job start";
        }
    }
}
