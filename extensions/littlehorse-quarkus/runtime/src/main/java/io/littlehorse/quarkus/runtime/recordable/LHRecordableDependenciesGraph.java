package io.littlehorse.quarkus.runtime.recordable;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.TopologicalOrderIterator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class LHRecordableDependenciesGraph<G extends LHRecordable> {

    private final List<G> inputList;

    public LHRecordableDependenciesGraph(List<G> inputList) {
        this.inputList = inputList;
    }

    public List<G> toList() {
        if (inputList == null || inputList.isEmpty()) {
            return List.of();
        }

        Graph<String, DefaultEdge> directedGraph = new DefaultDirectedGraph<>(DefaultEdge.class);

        inputList.forEach(recordable -> directedGraph.addVertex(recordable.getName()));
        inputList.stream()
                .flatMap(recordable -> recordable.dependencies().stream())
                .forEach(directedGraph::addVertex);

        inputList.forEach(recordable -> recordable
                .dependencies()
                .forEach(dependency -> directedGraph.addEdge(dependency, recordable.getName())));

        Iterator<String> iterator = new TopologicalOrderIterator<>(directedGraph);
        List<G> result = new ArrayList<>();
        iterator.forEachRemaining(name -> {
            Optional<G> first = inputList.stream()
                    .filter(recordable -> name.equals(recordable.getName()))
                    .findFirst();
            first.ifPresent(result::add);
        });
        return result;
    }
}
