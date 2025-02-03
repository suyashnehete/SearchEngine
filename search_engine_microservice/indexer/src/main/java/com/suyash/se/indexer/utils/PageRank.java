package com.suyash.se.indexer.utils;

import java.util.*;

public class PageRank {
    private static final double DAMPING_FACTOR = 0.85;
    private static final int MAX_ITERATIONS = 100;
    private static final double CONVERGENCE_THRESHOLD = 0.001;

    public static Map<Integer, Double> calculate(Map<Integer, List<Integer>> adjacencyList) {
        int numNodes = adjacencyList.size();
        Map<Integer, Double> pageRank = new HashMap<>();
        Map<Integer, List<Integer>> outDegreeMap = new HashMap<>();

        // Initialize PageRank scores and calculate out-degrees
        for (int node : adjacencyList.keySet()) {
            pageRank.put(node, 1.0 / numNodes);
            outDegreeMap.put(node, adjacencyList.getOrDefault(node, Collections.emptyList()));
        }

        // Iteratively update PageRank scores
        for (int iteration = 0; iteration < MAX_ITERATIONS; iteration++) {
            Map<Integer, Double> newPageRank = new HashMap<>();
            double totalDiff = 0.0;

            for (int node : adjacencyList.keySet()) {
                double score = (1 - DAMPING_FACTOR) / numNodes;

                for (int incomingNode : adjacencyList.getOrDefault(node, Collections.emptyList())) {
                    int outDegree = outDegreeMap.get(incomingNode).size();
                    if (outDegree > 0) {
                        score += DAMPING_FACTOR * (pageRank.get(incomingNode) / outDegree);
                    }
                }

                newPageRank.put(node, score);
                totalDiff += Math.abs(score - pageRank.get(node));
            }

            pageRank = newPageRank;

            // Check for convergence
            if (totalDiff < CONVERGENCE_THRESHOLD) {
                break;
            }
        }

        return pageRank;
    }
}
