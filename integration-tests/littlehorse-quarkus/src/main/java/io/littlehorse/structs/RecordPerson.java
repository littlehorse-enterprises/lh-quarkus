package io.littlehorse.structs;

import io.littlehorse.sdk.worker.LHStructDef;

import java.util.Map;

@LHStructDef("record-person")
public record RecordPerson(
        String firstName,
        String lastName,
        InlineStructDefAddress homeAddress,
        Map<String, InlineStructDefAddress> addressesByLabel) {}
