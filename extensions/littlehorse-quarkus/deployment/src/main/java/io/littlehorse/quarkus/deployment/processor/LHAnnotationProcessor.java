package io.littlehorse.quarkus.deployment.processor;

import io.littlehorse.quarkus.config.ConfigEvaluator;
import io.littlehorse.quarkus.task.LHTask;
import io.littlehorse.quarkus.task.LHUserTaskForm;
import io.littlehorse.quarkus.workflow.LHWorkflow;
import io.littlehorse.sdk.worker.LHStructDef;
import io.quarkus.arc.deployment.BeanArchiveIndexBuildItem;
import io.quarkus.arc.deployment.BeanDefiningAnnotationBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.BytecodeTransformerBuildItem;

import jakarta.inject.Singleton;

import org.eclipse.microprofile.config.ConfigProvider;
import org.jboss.jandex.AnnotationTarget.Kind;
import org.jboss.jandex.DotName;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

public class LHAnnotationProcessor {

    private static final DotName SINGLETON_ANNOTATION = DotName.createSimple(Singleton.class);
    private static final String STRUCT_DEF_DESCRIPTOR = "Lio/littlehorse/sdk/worker/LHStructDef;";

    @BuildStep
    BeanDefiningAnnotationBuildItem produceLHTask() {
        return new BeanDefiningAnnotationBuildItem(
                DotName.createSimple(LHTask.class), SINGLETON_ANNOTATION);
    }

    @BuildStep
    BeanDefiningAnnotationBuildItem produceLHWorkflow() {
        return new BeanDefiningAnnotationBuildItem(
                DotName.createSimple(LHWorkflow.class), SINGLETON_ANNOTATION);
    }

    @BuildStep
    BeanDefiningAnnotationBuildItem produceLHStructDef() {
        return new BeanDefiningAnnotationBuildItem(
                DotName.createSimple(LHStructDef.class), SINGLETON_ANNOTATION);
    }

    @BuildStep
    BeanDefiningAnnotationBuildItem produceLHUserTaskForm() {
        return new BeanDefiningAnnotationBuildItem(
                DotName.createSimple(LHUserTaskForm.class), SINGLETON_ANNOTATION);
    }

    @BuildStep
    void resolveStructDefConfigExpressions(
            BeanArchiveIndexBuildItem indexContainer,
            BuildProducer<BytecodeTransformerBuildItem> transformers) {
        ConfigEvaluator configEvaluator = new ConfigEvaluator(ConfigProvider.getConfig());

        indexContainer.getIndex().getAnnotations(LHStructDef.class).stream()
                .filter(annotation -> annotation.target().kind() == Kind.CLASS)
                .filter(annotation -> {
                    String value = annotation.value().asString();
                    return value != null && value.contains("${");
                })
                .forEach(annotation -> {
                    String rawValue = annotation.value().asString();
                    String resolvedValue = configEvaluator.expand(rawValue).asString();
                    String className = annotation.target().asClass().name().toString();
                    transformers.produce(new BytecodeTransformerBuildItem(
                            className,
                            (name, classVisitor) -> new StructDefAnnotationTransformer(
                                    classVisitor, resolvedValue)));
                });
    }

    private static class StructDefAnnotationTransformer extends ClassVisitor {

        private final String resolvedValue;

        StructDefAnnotationTransformer(ClassVisitor classVisitor, String resolvedValue) {
            super(Opcodes.ASM9, classVisitor);
            this.resolvedValue = resolvedValue;
        }

        @Override
        public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
            AnnotationVisitor av = super.visitAnnotation(descriptor, visible);
            if (STRUCT_DEF_DESCRIPTOR.equals(descriptor)) {
                return new AnnotationVisitor(Opcodes.ASM9, av) {
                    @Override
                    public void visit(String name, Object value) {
                        if ("value".equals(name)) {
                            super.visit(name, resolvedValue);
                        } else {
                            super.visit(name, value);
                        }
                    }
                };
            }
            return av;
        }
    }
}
