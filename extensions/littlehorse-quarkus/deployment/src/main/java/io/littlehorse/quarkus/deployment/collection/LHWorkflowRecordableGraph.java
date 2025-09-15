package io.littlehorse.quarkus.deployment.collection;

import io.littlehorse.quarkus.runtime.recordable.LHWorkflowRecordable;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.TopologicalOrderIterator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class LHWorkflowRecordableGraph {

    public static List<LHWorkflowRecordable> sort(List<? extends LHWorkflowRecordable> inputList) {
        if (inputList == null || inputList.isEmpty()) {
            return List.of();
        }

        Graph<String, DefaultEdge> directedGraph = new DefaultDirectedGraph<>(DefaultEdge.class);

        inputList.forEach(recordable -> directedGraph.addVertex(recordable.getName()));
        inputList.stream()
                .filter(recordable -> recordable.getParent() != null)
                .forEach(recordable -> directedGraph.addVertex(recordable.getParent()));

        inputList.stream()
                .filter(recordable -> recordable.getParent() != null)
                .forEach(recordable ->
                        directedGraph.addEdge(recordable.getParent(), recordable.getName()));

        Iterator<String> iterator = new TopologicalOrderIterator<>(directedGraph);
        List<LHWorkflowRecordable> result = new ArrayList<>();
        iterator.forEachRemaining(name -> {
            Optional<? extends LHWorkflowRecordable> first = inputList.stream()
                    .filter(recordable -> name.equals(recordable.getName()))
                    .findFirst();
            first.ifPresent(result::add);
        });
        return result;
    }
}
