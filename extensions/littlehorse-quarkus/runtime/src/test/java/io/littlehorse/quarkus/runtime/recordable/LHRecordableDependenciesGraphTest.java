package io.littlehorse.quarkus.runtime.recordable;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

class LHRecordableDependenciesGraphTest {

    private static class DummyRecordable extends LHRecordable {

        private final Set<String> deps;

        DummyRecordable(String name, String... dependencies) {
            super(null, name);
            this.deps = new LinkedHashSet<>(Arrays.asList(dependencies));
        }

        @Override
        public Set<String> dependencies() {
            return deps;
        }
    }

    private static DummyRecordable newRecordable(String name, String... dependencies) {
        return new DummyRecordable(name, dependencies);
    }

    @Test
    void shouldSortChildAndParent() {
        DummyRecordable child = newRecordable("my-child", "my-parent");
        DummyRecordable parent = newRecordable("my-parent");

        LHRecordableDependenciesGraph<DummyRecordable> graph =
                new LHRecordableDependenciesGraph<>(List.of(child, parent));
        assertThat(graph.toOrderedList()).containsExactly(parent, child);
    }

    @Test
    void shouldSortMultipleParent() {
        DummyRecordable child = newRecordable("my-child", "my-parent1");
        DummyRecordable parent1 = newRecordable("my-parent1", "my-parent2");
        DummyRecordable parent2 = newRecordable("my-parent2");

        LHRecordableDependenciesGraph<DummyRecordable> graph =
                new LHRecordableDependenciesGraph<>(List.of(child, parent1, parent2));
        assertThat(graph.toOrderedList()).containsExactly(parent2, parent1, child);
    }

    @Test
    void shouldSortNotParent() {
        DummyRecordable other = newRecordable("other");
        DummyRecordable child = newRecordable("my-child", "my-parent1");
        DummyRecordable parent1 = newRecordable("my-parent1", "my-parent2");
        DummyRecordable parent2 = newRecordable("my-parent2");

        LHRecordableDependenciesGraph<DummyRecordable> graph =
                new LHRecordableDependenciesGraph<>(List.of(child, parent1, parent2, other));
        assertThat(graph.toOrderedList()).containsExactly(parent2, other, parent1, child);
    }

    @Test
    void shouldSortWithMultipleChild() {
        DummyRecordable wf1 = newRecordable("wf1", "wf2");
        DummyRecordable wf2 = newRecordable("wf2", "wf3");
        DummyRecordable wf3 = newRecordable("wf3");
        DummyRecordable wf4 = newRecordable("wf4");
        DummyRecordable wf5 = newRecordable("wf5", "wf3");

        LHRecordableDependenciesGraph<DummyRecordable> graph =
                new LHRecordableDependenciesGraph<>(List.of(wf4, wf5, wf3, wf1, wf2));
        assertThat(graph.toOrderedList()).containsExactly(wf4, wf3, wf5, wf2, wf1);
    }

    @Test
    void shouldSortMissingParent() {
        DummyRecordable wf1 = newRecordable("wf1", "wf2");
        DummyRecordable wf2 = newRecordable("wf2", "wf3");
        DummyRecordable wf4 = newRecordable("wf4");
        DummyRecordable wf5 = newRecordable("wf5", "wf3");

        LHRecordableDependenciesGraph<DummyRecordable> graph =
                new LHRecordableDependenciesGraph<>(List.of(wf4, wf5, wf1, wf2));
        assertThat(graph.toOrderedList()).containsExactly(wf4, wf5, wf2, wf1);
    }
}
