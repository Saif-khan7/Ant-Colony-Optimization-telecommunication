// Heuristic.java
package com.example.acorouting.algorithm;

import com.example.acorouting.algorithm.model.Link;

public class Heuristic {
    private final double betaCongestion;
    public Heuristic(double betaCongestion) { this.betaCongestion = betaCongestion; }

    /** Larger value = more desirable edge. */
    public double value(Link e) {
        double util = e.utilization / (e.capacityMbps + 1e-6);
        double queuing = betaCongestion * util / (1 - util + 1e-6);
        return 1.0 / (e.latencyMs + queuing);
    }
}
