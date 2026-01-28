package io.littlehorse.quarkus.rest.gateway.resource.server;

import io.littlehorse.sdk.common.proto.LittleHorseVersion;

public record ServerInformationResponse(String version) {
    public ServerInformationResponse(LittleHorseVersion littleHorseVersion) {
        this("%d.%d.%d%s"
                .formatted(
                        littleHorseVersion.getMajorVersion(),
                        littleHorseVersion.getMinorVersion(),
                        littleHorseVersion.getPatchVersion(),
                        littleHorseVersion.hasPreReleaseIdentifier()
                                ? "-" + littleHorseVersion.getPreReleaseIdentifier()
                                : ""));
    }
}
