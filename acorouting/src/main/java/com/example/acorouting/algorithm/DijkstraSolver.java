package com.example.acorouting.algorithm;

import com.example.acorouting.algorithm.model.*;

import java.util.*;

/** Baseline: shortest latency, ignores capacity. */
public class DijkstraSolver {

    private final Map<Node, List<Link>> adj;

    public DijkstraSolver(Map<Node, List<Link>> adj) {
        this.adj = adj;
    }

    public Path solve(Demand d) {
        record Q(Node n, double dist) implements Comparable<Q> {
            public int compareTo(Q o) { return Double.compare(dist, o.dist); }
        }
        Map<Node, Double> dist = new HashMap<>();
        Map<Node, Link>   prev = new HashMap<>();
        PriorityQueue<Q> pq = new PriorityQueue<>();

        dist.put(d.src(), 0.0);
        pq.add(new Q(d.src(), 0));

        while (!pq.isEmpty()) {
            Q cur = pq.poll();
            if (cur.n().equals(d.dst())) break;
            if (cur.dist() > dist.getOrDefault(cur.n(), 1e30)) continue;

            for (Link e : adj.getOrDefault(cur.n(), List.of())) {
                Node nb = e.other(cur.n());
                double alt = cur.dist() + e.latencyMs;
                if (alt < dist.getOrDefault(nb, 1e30)) {
                    dist.put(nb, alt);
                    prev.put(nb, e);
                    pq.add(new Q(nb, alt));
                }
            }
        }
        if (!prev.containsKey(d.dst())) return null;         // disconnected

        List<Link> rev = new ArrayList<>();
        for (Node n = d.dst(); !n.equals(d.src()); ) {
            Link e = prev.get(n);
            rev.add(e);
            n = e.other(n);
        }
        Collections.reverse(rev);
        double cost = rev.stream().mapToDouble(l -> l.latencyMs).sum();
        return new Path(rev, cost);
    }
}
