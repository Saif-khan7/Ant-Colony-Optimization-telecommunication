package com.example.acorouting.algorithm;

import com.example.acorouting.algorithm.model.*;

import java.util.*;

/**
 * Ant-Colony meta-heuristic for single-demand path optimisation
 * in a capacitated, latency-weighted network.
 */
public class AntColonyEngine {

    /* ---------------- configuration ---------------- */

    private final Map<Node, List<Link>> adj;
    private final Heuristic heuristic;

    private final int    ants;
    private final int    iterations;
    private final double alpha;      // pheromone exponent
    private final double beta;       // heuristic exponent
    private final double rho;        // evaporation rate
    private final double Q;          // pheromone deposit constant

    public AntColonyEngine(Map<Node, List<Link>> adj,
                           int ants, int iterations,
                           double alpha, double beta, double rho, double Q,
                           double betaCongestion) {
        this.adj        = adj;
        this.ants       = ants;
        this.iterations = iterations;
        this.alpha      = alpha;
        this.beta       = beta;
        this.rho        = rho;
        this.Q          = Q;
        this.heuristic  = new Heuristic(betaCongestion);
    }

    /* ---------------- public API ---------------- */

    /** Returns the best (lowest-cost) path for a single demand. */
    public Path solve(Demand d) {
        Path globalBest = null;

        for (int it = 0; it < iterations; it++) {
            List<Path> iterationPaths = new ArrayList<>();

            for (int k = 0; k < ants; k++) {
                Path p = constructPath(d);
                if (p != null) iterationPaths.add(p);
                if (globalBest == null || (p != null && p.costMs() < globalBest.costMs())) {
                    globalBest = p;
                }
            }
            evaporate();
            deposit(iterationPaths);
        }
        return globalBest;   // may be null if no feasible path
    }

    /* ---------------- internal helpers ---------------- */

    /** Builds a single ant path respecting capacity constraints. */
    private Path constructPath(Demand d) {
        Node current = d.src();
        final double flow = d.trafficMbps();

        List<Link> path = new ArrayList<>();
        Set<Node>  visited = new HashSet<>();
        Random rnd = new Random();

        while (!current.equals(d.dst())) {
            visited.add(current);

            /* ----- effectively-final snapshot for lambdas ----- */
            final Node cur = current;

            // Feasible outward edges
            List<Link> options = adj.getOrDefault(cur, List.of()).stream()
                .filter(l -> !visited.contains(l.other(cur)))
                .filter(l -> l.utilization + flow <= l.capacityMbps)
                .toList();

            if (options.isEmpty()) return null;   // dead end

            /* ----- roulette-wheel selection ----- */
            double sumProb = 0;
            Map<Link, Double> prob = new HashMap<>();

            for (Link e : options) {
                double tau = Math.pow(e.pheromone, alpha);
                double eta = Math.pow(heuristic.value(e), beta);
                double val = tau * eta;
                prob.put(e, val);
                sumProb += val;
            }
            double r = rnd.nextDouble() * sumProb;
            for (Link e : options) {
                r -= prob.get(e);
                if (r <= 0) {
                    path.add(e);
                    current = e.other(cur);   // mutate AFTER stream pipeline
                    break;
                }
            }
        }
        double cost = path.stream().mapToDouble(l -> l.latencyMs).sum();
        return new Path(path, cost);
    }

    /* Evaporate pheromone on all edges. */
    private void evaporate() {
        adj.values().stream()
           .flatMap(List::stream)
           .forEach(l -> l.pheromone *= (1 - rho));
    }

    /* Deposit pheromone on edges contained in the iteration’s paths. */
    private void deposit(List<Path> paths) {
        for (Path p : paths) {
            double delta = Q / p.costMs();          // higher deposit for shorter path
            for (Link e : p.links()) e.pheromone += delta;
        }
    }
    public Path solveAndReserve(Demand d) {
        Path p = solve(d);              // existing call
        if (p == null) return null;
        // reserve capacity
        p.links().forEach(l -> l.utilization += d.trafficMbps());
        return p;
    }
}

