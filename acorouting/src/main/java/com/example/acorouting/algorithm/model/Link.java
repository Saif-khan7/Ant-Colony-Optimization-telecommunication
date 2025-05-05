// Link.java
package com.example.acorouting.algorithm.model;

public class Link {
    public final Node u, v;
    public final double latencyMs;
    public final double capacityMbps;
    public double pheromone = 1.0;
    public double utilization = 0.0;

    public Link(Node u, Node v, double latencyMs, double capacityMbps) {
        this.u = u;
        this.v = v;
        this.latencyMs = latencyMs;
        this.capacityMbps = capacityMbps;
    }
    public Node other(Node n) { return n.equals(u) ? v : u; }
}
