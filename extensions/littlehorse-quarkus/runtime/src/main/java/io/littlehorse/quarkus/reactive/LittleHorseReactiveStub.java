package io.littlehorse.quarkus.reactive;

import io.grpc.CallCredentials;
import io.grpc.CallOptions;
import io.grpc.ClientInterceptor;
import io.grpc.Deadline;
import io.littlehorse.sdk.common.proto.LittleHorseGrpc.LittleHorseFutureStub;
import io.quarkus.arc.DefaultBean;
import io.quarkus.arc.Unremovable;
import io.smallrye.mutiny.Uni;

import jakarta.enterprise.context.ApplicationScoped;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.time.Duration;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
@Unremovable
@DefaultBean
public class LittleHorseReactiveStub {
    private final LittleHorseFutureStub futureStub;

    public LittleHorseReactiveStub(LittleHorseFutureStub futureStub) {
        this.futureStub = futureStub;
    }

    public static void main(String[] args) {
        Arrays.stream(LittleHorseFutureStub.class.getDeclaredMethods())
                .filter(method -> !Arrays.asList(Object.class.getMethods()).contains(method))
                .filter(method -> method.getModifiers() == Modifier.PUBLIC)
                .filter(method -> Arrays.stream(LittleHorseReactiveStub.class.getDeclaredMethods())
                        .map(Method::getName)
                        .noneMatch(name -> name.equals(method.getName())))
                .sorted(Comparator.comparing(Method::getName))
                .forEach(method -> {
                    String returnType = ((ParameterizedType) method.getGenericReturnType())
                            .getActualTypeArguments()[0].getTypeName();
                    String parameterType =
                            method.getParameters()[0].getParameterizedType().getTypeName();
                    String name = method.getName();
                    String format =
                            """
                            public Uni<%s> %s(%s request) {
                                return Uni.createFrom().future(futureStub.%s(request));
                            }%n
                            """;
                    System.out.printf((format), returnType, name, parameterType, name);
                });
    }

    public LittleHorseReactiveStub withWaitForReady() {
        return new LittleHorseReactiveStub(futureStub.withWaitForReady());
    }

    public Uni<com.google.protobuf.Empty> deletePrincipal(
            io.littlehorse.sdk.common.proto.DeletePrincipalRequest request) {
        return Uni.createFrom().future(futureStub.deletePrincipal(request));
    }

    public <T> LittleHorseReactiveStub withWaitForReady(CallOptions.Key<T> key, T value) {
        return new LittleHorseReactiveStub(futureStub.withOption(key, value));
    }

    public LittleHorseReactiveStub withOnReadyThreshold(int numBytes) {
        return new LittleHorseReactiveStub(futureStub.withOnReadyThreshold(numBytes));
    }

    public LittleHorseReactiveStub withMaxOutboundMessageSize(int maxSize) {
        return new LittleHorseReactiveStub(futureStub.withMaxOutboundMessageSize(maxSize));
    }

    public LittleHorseReactiveStub withMaxInboundMessageSize(int maxSize) {
        return new LittleHorseReactiveStub(futureStub.withMaxInboundMessageSize(maxSize));
    }

    public LittleHorseReactiveStub withInterceptors(ClientInterceptor... interceptors) {
        return new LittleHorseReactiveStub(futureStub.withInterceptors(interceptors));
    }

    public LittleHorseReactiveStub withExecutor(Executor executor) {
        return new LittleHorseReactiveStub(futureStub.withExecutor(executor));
    }

    public LittleHorseReactiveStub withDeadlineAfter(Duration duration) {
        return new LittleHorseReactiveStub(futureStub.withDeadlineAfter(duration));
    }

    public LittleHorseReactiveStub withDeadlineAfter(long duration, TimeUnit unit) {
        return new LittleHorseReactiveStub(futureStub.withDeadlineAfter(duration, unit));
    }

    public LittleHorseReactiveStub withDeadline(Deadline deadline) {
        return new LittleHorseReactiveStub(futureStub.withDeadline(deadline));
    }

    public LittleHorseReactiveStub withCompression(String compressorName) {
        return new LittleHorseReactiveStub(futureStub.withCompression(compressorName));
    }

    public LittleHorseReactiveStub withCallCredentials(CallCredentials credentials) {
        return new LittleHorseReactiveStub(futureStub.withCallCredentials(credentials));
    }

    public Uni<com.google.protobuf.Empty> assignUserTaskRun(
            io.littlehorse.sdk.common.proto.AssignUserTaskRunRequest request) {
        return Uni.createFrom().future(futureStub.assignUserTaskRun(request));
    }

    public Uni<io.littlehorse.sdk.common.proto.WorkflowEvent> awaitWorkflowEvent(
            io.littlehorse.sdk.common.proto.AwaitWorkflowEventRequest request) {
        return Uni.createFrom().future(futureStub.awaitWorkflowEvent(request));
    }

    public Uni<com.google.protobuf.Empty> cancelUserTaskRun(
            io.littlehorse.sdk.common.proto.CancelUserTaskRunRequest request) {
        return Uni.createFrom().future(futureStub.cancelUserTaskRun(request));
    }

    public Uni<com.google.protobuf.Empty> completeUserTaskRun(
            io.littlehorse.sdk.common.proto.CompleteUserTaskRunRequest request) {
        return Uni.createFrom().future(futureStub.completeUserTaskRun(request));
    }

    public Uni<com.google.protobuf.Empty> deleteExternalEventDef(
            io.littlehorse.sdk.common.proto.DeleteExternalEventDefRequest request) {
        return Uni.createFrom().future(futureStub.deleteExternalEventDef(request));
    }

    public Uni<com.google.protobuf.Empty> deleteScheduledWfRun(
            io.littlehorse.sdk.common.proto.DeleteScheduledWfRunRequest request) {
        return Uni.createFrom().future(futureStub.deleteScheduledWfRun(request));
    }

    public Uni<com.google.protobuf.Empty> deleteStructDef(
            io.littlehorse.sdk.common.proto.DeleteStructDefRequest request) {
        return Uni.createFrom().future(futureStub.deleteStructDef(request));
    }

    public Uni<com.google.protobuf.Empty> deleteTaskDef(
            io.littlehorse.sdk.common.proto.DeleteTaskDefRequest request) {
        return Uni.createFrom().future(futureStub.deleteTaskDef(request));
    }

    public Uni<com.google.protobuf.Empty> deleteUserTaskDef(
            io.littlehorse.sdk.common.proto.DeleteUserTaskDefRequest request) {
        return Uni.createFrom().future(futureStub.deleteUserTaskDef(request));
    }

    public Uni<com.google.protobuf.Empty> deleteWfRun(
            io.littlehorse.sdk.common.proto.DeleteWfRunRequest request) {
        return Uni.createFrom().future(futureStub.deleteWfRun(request));
    }

    public Uni<com.google.protobuf.Empty> deleteWfSpec(
            io.littlehorse.sdk.common.proto.DeleteWfSpecRequest request) {
        return Uni.createFrom().future(futureStub.deleteWfSpec(request));
    }

    public Uni<com.google.protobuf.Empty> deleteWorkflowEventDef(
            io.littlehorse.sdk.common.proto.DeleteWorkflowEventDefRequest request) {
        return Uni.createFrom().future(futureStub.deleteWorkflowEventDef(request));
    }

    public Uni<io.littlehorse.sdk.common.proto.CorrelatedEvent> getCorrelatedEvent(
            io.littlehorse.sdk.common.proto.CorrelatedEventId request) {
        return Uni.createFrom().future(futureStub.getCorrelatedEvent(request));
    }

    public Uni<io.littlehorse.sdk.common.proto.ExternalEvent> getExternalEvent(
            io.littlehorse.sdk.common.proto.ExternalEventId request) {
        return Uni.createFrom().future(futureStub.getExternalEvent(request));
    }

    public Uni<io.littlehorse.sdk.common.proto.ExternalEventDef> getExternalEventDef(
            io.littlehorse.sdk.common.proto.ExternalEventDefId request) {
        return Uni.createFrom().future(futureStub.getExternalEventDef(request));
    }

    public Uni<io.littlehorse.sdk.common.proto.UserTaskDef> getLatestUserTaskDef(
            io.littlehorse.sdk.common.proto.GetLatestUserTaskDefRequest request) {
        return Uni.createFrom().future(futureStub.getLatestUserTaskDef(request));
    }

    public Uni<io.littlehorse.sdk.common.proto.WfSpec> getLatestWfSpec(
            io.littlehorse.sdk.common.proto.GetLatestWfSpecRequest request) {
        return Uni.createFrom().future(futureStub.getLatestWfSpec(request));
    }

    public Uni<io.littlehorse.sdk.common.proto.NodeRun> getNodeRun(
            io.littlehorse.sdk.common.proto.NodeRunId request) {
        return Uni.createFrom().future(futureStub.getNodeRun(request));
    }

    public Uni<io.littlehorse.sdk.common.proto.Principal> getPrincipal(
            io.littlehorse.sdk.common.proto.PrincipalId request) {
        return Uni.createFrom().future(futureStub.getPrincipal(request));
    }

    public Uni<io.littlehorse.sdk.common.proto.ScheduledWfRun> getScheduledWfRun(
            io.littlehorse.sdk.common.proto.ScheduledWfRunId request) {
        return Uni.createFrom().future(futureStub.getScheduledWfRun(request));
    }

    public Uni<io.littlehorse.sdk.common.proto.LittleHorseVersion> getServerVersion(
            com.google.protobuf.Empty request) {
        return Uni.createFrom().future(futureStub.getServerVersion(request));
    }

    public Uni<io.littlehorse.sdk.common.proto.StructDef> getStructDef(
            io.littlehorse.sdk.common.proto.StructDefId request) {
        return Uni.createFrom().future(futureStub.getStructDef(request));
    }

    public Uni<io.littlehorse.sdk.common.proto.TaskDef> getTaskDef(
            io.littlehorse.sdk.common.proto.TaskDefId request) {
        return Uni.createFrom().future(futureStub.getTaskDef(request));
    }

    public Uni<io.littlehorse.sdk.common.proto.TaskDefMetrics> getTaskDefMetricsWindow(
            io.littlehorse.sdk.common.proto.TaskDefMetricsQueryRequest request) {
        return Uni.createFrom().future(futureStub.getTaskDefMetricsWindow(request));
    }

    public Uni<io.littlehorse.sdk.common.proto.TaskRun> getTaskRun(
            io.littlehorse.sdk.common.proto.TaskRunId request) {
        return Uni.createFrom().future(futureStub.getTaskRun(request));
    }

    public Uni<io.littlehorse.sdk.common.proto.TaskWorkerGroup> getTaskWorkerGroup(
            io.littlehorse.sdk.common.proto.TaskDefId request) {
        return Uni.createFrom().future(futureStub.getTaskWorkerGroup(request));
    }

    public Uni<io.littlehorse.sdk.common.proto.Tenant> getTenant(
            io.littlehorse.sdk.common.proto.TenantId request) {
        return Uni.createFrom().future(futureStub.getTenant(request));
    }

    public Uni<io.littlehorse.sdk.common.proto.UserTaskDef> getUserTaskDef(
            io.littlehorse.sdk.common.proto.UserTaskDefId request) {
        return Uni.createFrom().future(futureStub.getUserTaskDef(request));
    }

    public Uni<io.littlehorse.sdk.common.proto.UserTaskRun> getUserTaskRun(
            io.littlehorse.sdk.common.proto.UserTaskRunId request) {
        return Uni.createFrom().future(futureStub.getUserTaskRun(request));
    }

    public Uni<io.littlehorse.sdk.common.proto.Variable> getVariable(
            io.littlehorse.sdk.common.proto.VariableId request) {
        return Uni.createFrom().future(futureStub.getVariable(request));
    }

    public Uni<io.littlehorse.sdk.common.proto.WfRun> getWfRun(
            io.littlehorse.sdk.common.proto.WfRunId request) {
        return Uni.createFrom().future(futureStub.getWfRun(request));
    }

    public Uni<io.littlehorse.sdk.common.proto.WfSpec> getWfSpec(
            io.littlehorse.sdk.common.proto.WfSpecId request) {
        return Uni.createFrom().future(futureStub.getWfSpec(request));
    }

    public Uni<io.littlehorse.sdk.common.proto.WfSpecMetrics> getWfSpecMetricsWindow(
            io.littlehorse.sdk.common.proto.WfSpecMetricsQueryRequest request) {
        return Uni.createFrom().future(futureStub.getWfSpecMetricsWindow(request));
    }

    public Uni<io.littlehorse.sdk.common.proto.WorkflowEvent> getWorkflowEvent(
            io.littlehorse.sdk.common.proto.WorkflowEventId request) {
        return Uni.createFrom().future(futureStub.getWorkflowEvent(request));
    }

    public Uni<io.littlehorse.sdk.common.proto.WorkflowEventDef> getWorkflowEventDef(
            io.littlehorse.sdk.common.proto.WorkflowEventDefId request) {
        return Uni.createFrom().future(futureStub.getWorkflowEventDef(request));
    }

    public Uni<io.littlehorse.sdk.common.proto.ExternalEventList> listExternalEvents(
            io.littlehorse.sdk.common.proto.ListExternalEventsRequest request) {
        return Uni.createFrom().future(futureStub.listExternalEvents(request));
    }

    public Uni<io.littlehorse.sdk.common.proto.NodeRunList> listNodeRuns(
            io.littlehorse.sdk.common.proto.ListNodeRunsRequest request) {
        return Uni.createFrom().future(futureStub.listNodeRuns(request));
    }

    public Uni<io.littlehorse.sdk.common.proto.ListTaskMetricsResponse> listTaskDefMetrics(
            io.littlehorse.sdk.common.proto.ListTaskMetricsRequest request) {
        return Uni.createFrom().future(futureStub.listTaskDefMetrics(request));
    }

    public Uni<io.littlehorse.sdk.common.proto.TaskRunList> listTaskRuns(
            io.littlehorse.sdk.common.proto.ListTaskRunsRequest request) {
        return Uni.createFrom().future(futureStub.listTaskRuns(request));
    }

    public Uni<io.littlehorse.sdk.common.proto.UserTaskRunList> listUserTaskRuns(
            io.littlehorse.sdk.common.proto.ListUserTaskRunRequest request) {
        return Uni.createFrom().future(futureStub.listUserTaskRuns(request));
    }

    public Uni<io.littlehorse.sdk.common.proto.VariableList> listVariables(
            io.littlehorse.sdk.common.proto.ListVariablesRequest request) {
        return Uni.createFrom().future(futureStub.listVariables(request));
    }

    public Uni<io.littlehorse.sdk.common.proto.ListWfMetricsResponse> listWfSpecMetrics(
            io.littlehorse.sdk.common.proto.ListWfMetricsRequest request) {
        return Uni.createFrom().future(futureStub.listWfSpecMetrics(request));
    }

    public Uni<io.littlehorse.sdk.common.proto.WorkflowEventList> listWorkflowEvents(
            io.littlehorse.sdk.common.proto.ListWorkflowEventsRequest request) {
        return Uni.createFrom().future(futureStub.listWorkflowEvents(request));
    }

    public Uni<io.littlehorse.sdk.common.proto.WfSpec> migrateWfSpec(
            io.littlehorse.sdk.common.proto.MigrateWfSpecRequest request) {
        return Uni.createFrom().future(futureStub.migrateWfSpec(request));
    }

    public Uni<io.littlehorse.sdk.common.proto.CorrelatedEvent> putCorrelatedEvent(
            io.littlehorse.sdk.common.proto.PutCorrelatedEventRequest request) {
        return Uni.createFrom().future(futureStub.putCorrelatedEvent(request));
    }

    public Uni<io.littlehorse.sdk.common.proto.ExternalEvent> putExternalEvent(
            io.littlehorse.sdk.common.proto.PutExternalEventRequest request) {
        return Uni.createFrom().future(futureStub.putExternalEvent(request));
    }

    public Uni<io.littlehorse.sdk.common.proto.ExternalEventDef> putExternalEventDef(
            io.littlehorse.sdk.common.proto.PutExternalEventDefRequest request) {
        return Uni.createFrom().future(futureStub.putExternalEventDef(request));
    }

    public Uni<io.littlehorse.sdk.common.proto.Principal> putPrincipal(
            io.littlehorse.sdk.common.proto.PutPrincipalRequest request) {
        return Uni.createFrom().future(futureStub.putPrincipal(request));
    }

    public Uni<io.littlehorse.sdk.common.proto.StructDef> putStructDef(
            io.littlehorse.sdk.common.proto.PutStructDefRequest request) {
        return Uni.createFrom().future(futureStub.putStructDef(request));
    }

    public Uni<io.littlehorse.sdk.common.proto.TaskDef> putTaskDef(
            io.littlehorse.sdk.common.proto.PutTaskDefRequest request) {
        return Uni.createFrom().future(futureStub.putTaskDef(request));
    }

    public Uni<io.littlehorse.sdk.common.proto.Tenant> putTenant(
            io.littlehorse.sdk.common.proto.PutTenantRequest request) {
        return Uni.createFrom().future(futureStub.putTenant(request));
    }

    public Uni<io.littlehorse.sdk.common.proto.UserTaskDef> putUserTaskDef(
            io.littlehorse.sdk.common.proto.PutUserTaskDefRequest request) {
        return Uni.createFrom().future(futureStub.putUserTaskDef(request));
    }

    public Uni<io.littlehorse.sdk.common.proto.WfSpec> putWfSpec(
            io.littlehorse.sdk.common.proto.PutWfSpecRequest request) {
        return Uni.createFrom().future(futureStub.putWfSpec(request));
    }

    public Uni<io.littlehorse.sdk.common.proto.WorkflowEventDef> putWorkflowEventDef(
            io.littlehorse.sdk.common.proto.PutWorkflowEventDefRequest request) {
        return Uni.createFrom().future(futureStub.putWorkflowEventDef(request));
    }

    public Uni<io.littlehorse.sdk.common.proto.RegisterTaskWorkerResponse> registerTaskWorker(
            io.littlehorse.sdk.common.proto.RegisterTaskWorkerRequest request) {
        return Uni.createFrom().future(futureStub.registerTaskWorker(request));
    }

    public Uni<com.google.protobuf.Empty> reportTask(
            io.littlehorse.sdk.common.proto.ReportTaskRun request) {
        return Uni.createFrom().future(futureStub.reportTask(request));
    }

    public Uni<io.littlehorse.sdk.common.proto.WfRun> rescueThreadRun(
            io.littlehorse.sdk.common.proto.RescueThreadRunRequest request) {
        return Uni.createFrom().future(futureStub.rescueThreadRun(request));
    }

    public Uni<com.google.protobuf.Empty> resumeWfRun(
            io.littlehorse.sdk.common.proto.ResumeWfRunRequest request) {
        return Uni.createFrom().future(futureStub.resumeWfRun(request));
    }

    public Uni<io.littlehorse.sdk.common.proto.WfRun> runWf(
            io.littlehorse.sdk.common.proto.RunWfRequest request) {
        return Uni.createFrom().future(futureStub.runWf(request));
    }

    public Uni<io.littlehorse.sdk.common.proto.UserTaskRun> saveUserTaskRunProgress(
            io.littlehorse.sdk.common.proto.SaveUserTaskRunProgressRequest request) {
        return Uni.createFrom().future(futureStub.saveUserTaskRunProgress(request));
    }

    public Uni<io.littlehorse.sdk.common.proto.ScheduledWfRun> scheduleWf(
            io.littlehorse.sdk.common.proto.ScheduleWfRequest request) {
        return Uni.createFrom().future(futureStub.scheduleWf(request));
    }

    public Uni<io.littlehorse.sdk.common.proto.ExternalEventIdList> searchExternalEvent(
            io.littlehorse.sdk.common.proto.SearchExternalEventRequest request) {
        return Uni.createFrom().future(futureStub.searchExternalEvent(request));
    }

    public Uni<io.littlehorse.sdk.common.proto.ExternalEventDefIdList> searchExternalEventDef(
            io.littlehorse.sdk.common.proto.SearchExternalEventDefRequest request) {
        return Uni.createFrom().future(futureStub.searchExternalEventDef(request));
    }

    public Uni<io.littlehorse.sdk.common.proto.NodeRunIdList> searchNodeRun(
            io.littlehorse.sdk.common.proto.SearchNodeRunRequest request) {
        return Uni.createFrom().future(futureStub.searchNodeRun(request));
    }

    public Uni<io.littlehorse.sdk.common.proto.PrincipalIdList> searchPrincipal(
            io.littlehorse.sdk.common.proto.SearchPrincipalRequest request) {
        return Uni.createFrom().future(futureStub.searchPrincipal(request));
    }

    public Uni<io.littlehorse.sdk.common.proto.ScheduledWfRunIdList> searchScheduledWfRun(
            io.littlehorse.sdk.common.proto.SearchScheduledWfRunRequest request) {
        return Uni.createFrom().future(futureStub.searchScheduledWfRun(request));
    }

    public Uni<io.littlehorse.sdk.common.proto.TaskDefIdList> searchTaskDef(
            io.littlehorse.sdk.common.proto.SearchTaskDefRequest request) {
        return Uni.createFrom().future(futureStub.searchTaskDef(request));
    }

    public Uni<io.littlehorse.sdk.common.proto.TaskRunIdList> searchTaskRun(
            io.littlehorse.sdk.common.proto.SearchTaskRunRequest request) {
        return Uni.createFrom().future(futureStub.searchTaskRun(request));
    }

    public Uni<io.littlehorse.sdk.common.proto.TenantIdList> searchTenant(
            io.littlehorse.sdk.common.proto.SearchTenantRequest request) {
        return Uni.createFrom().future(futureStub.searchTenant(request));
    }

    public Uni<io.littlehorse.sdk.common.proto.UserTaskDefIdList> searchUserTaskDef(
            io.littlehorse.sdk.common.proto.SearchUserTaskDefRequest request) {
        return Uni.createFrom().future(futureStub.searchUserTaskDef(request));
    }

    public Uni<io.littlehorse.sdk.common.proto.UserTaskRunIdList> searchUserTaskRun(
            io.littlehorse.sdk.common.proto.SearchUserTaskRunRequest request) {
        return Uni.createFrom().future(futureStub.searchUserTaskRun(request));
    }

    public Uni<io.littlehorse.sdk.common.proto.VariableIdList> searchVariable(
            io.littlehorse.sdk.common.proto.SearchVariableRequest request) {
        return Uni.createFrom().future(futureStub.searchVariable(request));
    }

    public Uni<io.littlehorse.sdk.common.proto.WfRunIdList> searchWfRun(
            io.littlehorse.sdk.common.proto.SearchWfRunRequest request) {
        return Uni.createFrom().future(futureStub.searchWfRun(request));
    }

    public Uni<io.littlehorse.sdk.common.proto.WfSpecIdList> searchWfSpec(
            io.littlehorse.sdk.common.proto.SearchWfSpecRequest request) {
        return Uni.createFrom().future(futureStub.searchWfSpec(request));
    }

    public Uni<io.littlehorse.sdk.common.proto.WorkflowEventIdList> searchWorkflowEvent(
            io.littlehorse.sdk.common.proto.SearchWorkflowEventRequest request) {
        return Uni.createFrom().future(futureStub.searchWorkflowEvent(request));
    }

    public Uni<io.littlehorse.sdk.common.proto.WorkflowEventDefIdList> searchWorkflowEventDef(
            io.littlehorse.sdk.common.proto.SearchWorkflowEventDefRequest request) {
        return Uni.createFrom().future(futureStub.searchWorkflowEventDef(request));
    }

    public Uni<com.google.protobuf.Empty> stopWfRun(
            io.littlehorse.sdk.common.proto.StopWfRunRequest request) {
        return Uni.createFrom().future(futureStub.stopWfRun(request));
    }

    public Uni<io.littlehorse.sdk.common.proto.ValidateStructDefEvolutionResponse>
            validateStructDefEvolution(
                    io.littlehorse.sdk.common.proto.ValidateStructDefEvolutionRequest request) {
        return Uni.createFrom().future(futureStub.validateStructDefEvolution(request));
    }

    public Uni<io.littlehorse.sdk.common.proto.Principal> whoami(
            com.google.protobuf.Empty request) {
        return Uni.createFrom().future(futureStub.whoami(request));
    }

    public Uni<com.google.protobuf.Empty> deleteCorrelatedEvent(
            io.littlehorse.sdk.common.proto.DeleteCorrelatedEventRequest request) {
        return Uni.createFrom().future(futureStub.deleteCorrelatedEvent(request));
    }

    public Uni<io.littlehorse.sdk.common.proto.CorrelatedEventIdList> searchCorrelatedEvent(
            io.littlehorse.sdk.common.proto.SearchCorrelatedEventRequest request) {
        return Uni.createFrom().future(futureStub.searchCorrelatedEvent(request));
    }
}
