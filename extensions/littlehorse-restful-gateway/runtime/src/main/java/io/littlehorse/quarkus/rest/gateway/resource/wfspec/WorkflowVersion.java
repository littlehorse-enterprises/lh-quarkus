package io.littlehorse.quarkus.rest.gateway.resource.wfspec;

import jakarta.ws.rs.BadRequestException;

public record WorkflowVersion(String version) {

    public WorkflowVersion(int majorVersion, int revision) {
        this("%d.%d".formatted(majorVersion, revision));
    }

    public WorkflowVersion {
        if (version == null || !version.matches("\\d+\\.\\d+")) {
            throw new BadRequestException("Version should be in the format major.revision");
        }
    }

    @Override
    public String toString() {
        return version;
    }

    public int majorVersion() {
        return getVersionPart(WorkflowVersionPart.MAJOR);
    }

    public int revision() {
        return getVersionPart(WorkflowVersionPart.REVISION);
    }

    private int getVersionPart(WorkflowVersionPart part) {
        return Integer.parseInt(version.split("\\.")[part.ordinal()]);
    }
}
