package com.flowdock.jenkins;

import com.flowdock.jenkins.exception.FlowdockException;
import hudson.EnvVars;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.BuildWrapper;
import hudson.tasks.BuildWrapperDescriptor;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

public class BuildStart extends BuildWrapper {

    private final String flowToken;
    private final String flowThread;
    private final String notificationTags;
    private final boolean chatStart;

    @DataBoundConstructor
    public BuildStart(String flowToken, String flowThread, String notificationTags,
        String chatStart, String chatNotification,
        String chatStatus, String notifySuccess, String notifyFailure, String notifyFixed,
        String notifyUnstable, String notifyAborted, String notifyNotBuilt) {
        this.flowToken = flowToken;
        this.flowThread = flowThread;
        this.notificationTags = notificationTags;
        this.chatStart = chatStart != null && chatStart.equals("true");
    }

    public Environment setUp(AbstractBuild build, Launcher launcher, BuildListener listener) {

      if (!this.chatStart)
        return;

      PrintStream logger = listener.getLogger();

      try {
        FlowdockAPI api = new FlowdockAPI(getDescriptor().apiUrl(), flowToken);
        ChatMessage chatMsg = ChatMessage.startBuild(build, buildResult, listener);
        chatMsg.setTags(vars.expand(notificationTags));
        api.pushChatMessage(chatMsg);
        logger.println("Flowdock: Chat notification sent successfully");
      }
    }
}
