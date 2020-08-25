package com.flowdock.jenkins;

import hudson.model.AbstractBuild;
import hudson.model.Result;

public enum BuildResult {
    SUCCESS("was successful"),
    FAILURE("failed"),
    UNSTABLE("was unstable"),
    ABORTED("was aborted"),
    NOT_BUILT("was not built"),
    FIXED("was fixed");

    private String humanResult;

    private BuildResult(String displayName) {
        this.humanResult = displayName;
    }

    public static BuildResult fromBuild(AbstractBuild build) {
        Result currResult = build.getResult();
        if (currResult == null) {
          return FAILURE;
        }

        if (currResult.equals(Result.SUCCESS)) {
            AbstractBuild prevBuild = build.getPreviousBuild();
            if (prevBuild == null) {
              return SUCCESS;
            }
            Result prevResult = prevBuild.getResult();
            if (prevResult == null || Result.FAILURE.equals(prevResult) || Result.UNSTABLE.equals(prevResult)) {
                return FIXED;
            }
            return SUCCESS;
        } else if (currResult.equals(Result.UNSTABLE)) {
            return UNSTABLE;
        } else if (currResult.equals(Result.ABORTED)) {
            return ABORTED;
        } else if (currResult.equals(Result.NOT_BUILT)) {
            return NOT_BUILT;
        }
        return FAILURE;
    }

    public String getHumanResult() {
        return humanResult;
    }
}
