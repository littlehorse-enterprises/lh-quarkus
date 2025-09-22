package io.littlehorse.quarkus.runtime.recordable;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.TopologicalOrderIterator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class LHWorkflowRecordableGraph {

    private final List<? extends LHWorkflowRecordable> inputList;

    public LHWorkflowRecordableGraph(List<? extends LHWorkflowRecordable> inputList) {
        this.inputList = inputList;
    }

    public List<LHWorkflowRecordable> toList() {
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
