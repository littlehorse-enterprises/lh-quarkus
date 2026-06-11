package io.littlehorse.quarkus.gradle;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Comparator;
import java.util.List;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.Classpath;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

/**
 * Generates {@code io.littlehorse.quarkus.reactive.LittleHorseReactiveStub} by
 * reflecting over {@code LittleHorseFutureStub}, exposing every gRPC call as a
 * Mutiny {@code Uni}. This replaces the previously hand-maintained class.
 *
 * <p>
 * The task lives in {@code buildSrc} so additional generators can be added in
 * the same package in the future.
 */
@CacheableTask
public abstract class GenerateReactiveStubTask extends DefaultTask {

  private static final String STUB_CLASS_NAME = "io.littlehorse.sdk.common.proto.LittleHorseGrpc$LittleHorseFutureStub";
  private static final String LISTENABLE_FUTURE_CLASS_NAME = "com.google.common.util.concurrent.ListenableFuture";
  private static final String GENERATED_FILE_PATH = "io/littlehorse/quarkus/reactive/AbstractLittleHorseReactiveStub.java";

  public GenerateReactiveStubTask() {
    setGroup("build");
    setDescription("Generates LittleHorseReactiveStub by reflecting over LittleHorseFutureStub.");
  }

  /** Classpath used to load {@code LittleHorseFutureStub} via reflection. */
  @Classpath
  public abstract ConfigurableFileCollection getStubClasspath();

  /** Directory the generated Java source is written to. */
  @OutputDirectory
  public abstract DirectoryProperty getOutputDir();

  @TaskAction
  public void generate() {
    URL[] urls = getStubClasspath().getFiles().stream()
        .map(GenerateReactiveStubTask::toUrl)
        .toArray(URL[]::new);

    StringBuilder methods = new StringBuilder();
    try (URLClassLoader loader = new URLClassLoader(urls, ClassLoader.getPlatformClassLoader())) {
      Class<?> stubClass = Class.forName(STUB_CLASS_NAME, false, loader);
      Class<?> listenableFuture = Class.forName(LISTENABLE_FUTURE_CLASS_NAME, false, loader);

      List<Method> rpcMethods = java.util.Arrays.stream(stubClass.getDeclaredMethods())
          .filter(m -> Modifier.isPublic(m.getModifiers()))
          .filter(m -> m.getReturnType() == listenableFuture)
          .filter(m -> m.getParameterCount() == 1)
          .sorted(Comparator.comparing(Method::getName))
          .toList();

      for (Method m : rpcMethods) {
        String responseType = ((ParameterizedType) m.getGenericReturnType())
            .getActualTypeArguments()[0]
            .getTypeName()
            .replace('$', '.');
        String requestType = m.getParameterTypes()[0].getName().replace('$', '.');
        String name = m.getName();
        methods.append("\n");
        methods.append("    public Uni<").append(responseType).append("> ").append(name)
            .append("(").append(requestType).append(" request) {\n");
        methods.append("        return Uni.createFrom().future(futureStub.").append(name)
            .append("(request));\n");
        methods.append("    }\n");
      }
    } catch (ClassNotFoundException | IOException e) {
      throw new IllegalStateException("Failed to generate LittleHorseReactiveStub", e);
    }

    File target = getOutputDir().get().file(GENERATED_FILE_PATH).getAsFile();
    try {
      Files.createDirectories(target.getParentFile().toPath());
      Files.writeString(target.toPath(), HEADER + methods + "}\n", StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new UncheckedIOException("Failed to write " + target, e);
    }
  }

  private static URL toUrl(File file) {
    try {
      return file.toURI().toURL();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private static final String HEADER = """
      package io.littlehorse.quarkus.reactive;

      import io.littlehorse.sdk.common.proto.LittleHorseGrpc.LittleHorseFutureStub;
      import io.smallrye.mutiny.Uni;

      /**
       * Generated base class for {@link LittleHorseReactiveStub} that exposes every
       * gRPC call on {@link LittleHorseFutureStub} as a Mutiny {@link Uni}.
       *
       * <p>This class is generated at build time by the {@code generateReactiveStub}
       * Gradle task ({@code io.littlehorse.quarkus.gradle.GenerateReactiveStubTask}),
       * which reflects over {@link LittleHorseFutureStub}. Do not edit it by hand. To
       * add manual methods (for example new {@code withX} helpers), edit
       * {@link LittleHorseReactiveStub} instead.
       */
      public abstract class AbstractLittleHorseReactiveStub {
          protected final LittleHorseFutureStub futureStub;

          /**
           * No-args constructor required so that CDI can create a client proxy for the
           * normal-scoped {@link LittleHorseReactiveStub} subclass.
           */
          protected AbstractLittleHorseReactiveStub() {
              this.futureStub = null;
          }

          protected AbstractLittleHorseReactiveStub(LittleHorseFutureStub futureStub) {
              this.futureStub = futureStub;
          }
      """;
}
