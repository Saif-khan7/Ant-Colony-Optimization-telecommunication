// TopologyStore.java
package com.example.acorouting.store;

import com.example.acorouting.algorithm.model.*;

import java.util.*;

public class TopologyStore {
    private final Map<Node, List<Link>> adj = new HashMap<>();
    private final List<Demand> demands = new ArrayList<>();

    public void clear() { adj.clear(); demands.clear(); }

    /** undirected */
    public void addLink(Link l) {
        adj.computeIfAbsent(l.u, k -> new ArrayList<>()).add(l);
        adj.computeIfAbsent(l.v, k -> new ArrayList<>()).add(l);
    }
    public void setDemands(List<Demand> ds) {
        demands.clear(); demands.addAll(ds);
    }
    public Map<Node, List<Link>> graph() { return adj; }
    public List<Demand> demandList()    { return demands; }
}
