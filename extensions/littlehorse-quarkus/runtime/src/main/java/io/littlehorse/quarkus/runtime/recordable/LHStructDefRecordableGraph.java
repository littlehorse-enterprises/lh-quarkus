package io.littlehorse.quarkus.runtime.recordable;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.TopologicalOrderIterator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class LHStructDefRecordableGraph {

    private final List<LHStructDefRecordable> inputList;

    public LHStructDefRecordableGraph(List<LHStructDefRecordable> inputList) {
        this.inputList = inputList;
    }

    public List<LHStructDefRecordable> toList() {
        if (inputList == null || inputList.isEmpty()) {
            return List.of();
        }

        Graph<String, DefaultEdge> directedGraph = new DefaultDirectedGraph<>(DefaultEdge.class);

        inputList.forEach(recordable -> directedGraph.addVertex(recordable.getName()));
        inputList.stream()
                .flatMap(recordable -> recordable.calculateDependencies().stream())
                .forEach(directedGraph::addVertex);

        inputList.forEach(recordable -> recordable
                .calculateDependencies()
                .forEach(dependency -> directedGraph.addEdge(dependency, recordable.getName())));

        Iterator<String> iterator = new TopologicalOrderIterator<>(directedGraph);
        List<LHStructDefRecordable> result = new ArrayList<>();
        iterator.forEachRemaining(name -> {
            Optional<LHStructDefRecordable> first = inputList.stream()
                    .filter(recordable -> name.equals(recordable.getName()))
                    .findFirst();
            first.ifPresent(result::add);
        });
        return result;
    }
}
