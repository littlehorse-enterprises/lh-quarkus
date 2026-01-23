package io.littlehorse.quarkus.rest.gateway.resource.wfspec;

import jakarta.ws.rs.BadRequestException;

public class WorkflowVersion {
    private final int majorVersion;
    private final int revision;

    private WorkflowVersion(int majorVersion, int revision) {
        this.majorVersion = majorVersion;
        this.revision = revision;
    }

    public static WorkflowVersion of(String version) {
        if (version == null || !version.matches("\\d+\\.\\d+")) {
            throw new BadRequestException("Version should be in the format major.revision");
        }

        String[] versionParts = version.split("\\.");
        return new WorkflowVersion(
                Integer.parseInt(versionParts[0]), Integer.parseInt(versionParts[1]));
    }

    public static WorkflowVersion of(int majorVersion, int revision) {
        return new WorkflowVersion(majorVersion, revision);
    }

    public String toString() {
        return majorVersion + "." + revision;
    }

    public int getMajorVersion() {
        return majorVersion;
    }

    public int getRevision() {
        return revision;
    }

    public String get() {
        return toString();
    }
}
