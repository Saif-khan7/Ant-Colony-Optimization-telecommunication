// RoutingController.java
package com.example.acorouting.controller;

import com.example.acorouting.algorithm.*;
import com.example.acorouting.algorithm.model.*;
import com.example.acorouting.store.TopologyStore;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class RoutingController {

    private final TopologyStore store = new TopologyStore();

    /* ---------- TOPOLOGY ---------- */
    @PostMapping("/topology/links")
    public String uploadLinks(@RequestBody List<LinkDTO> links) {
        links.forEach(dto -> store.addLink(dto.toLink()));
        return "Links loaded: " + links.size();
    }

    @PostMapping("/topology/demands")
    public String uploadDemands(@RequestBody List<DemandDTO> ds) {
        store.setDemands(ds.stream().map(DemandDTO::toDemand).toList());
        return "Demands loaded: " + ds.size();
    }

    /* ---------- RUN ACO ---------- */
    @PostMapping("/aco/run")
    public List<PathResponse> runAco(@RequestBody AcoParams p) {

        // reset utilisation before each global run
        store.graph().values().stream().flatMap(List::stream)
            .forEach(l -> l.utilization = 0);

        AntColonyEngine engine = new AntColonyEngine(
                store.graph(),
                p.ants(), p.iterations(),
                p.alpha(), p.beta(), p.rho(), p.Q(),
                p.betaCongestion());

        List<PathResponse> resp = new ArrayList<>();
        for (Demand d : store.demandList()) {
            Path best = engine.solveAndReserve(d);       // reserve here
            if (best != null) resp.add(PathResponse.from(best, d));
        }
        return resp;
    }

    @PostMapping("/baseline/run")
        public List<PathResponse> runBaseline() {
        DijkstraSolver dj = new DijkstraSolver(store.graph());
        List<PathResponse> resp = new ArrayList<>();
        for (Demand d : store.demandList()) {
            Path p = dj.solve(d);
            if (p != null) resp.add(PathResponse.from(p, d));
        }
        return resp;
    }

    /* ---------- DTOs ---------- */
    record LinkDTO(String u, String v, double latencyMs, double capacityMbps) {
        Link toLink() { return new Link(new Node(u), new Node(v), latencyMs, capacityMbps); }
    }
    record DemandDTO(String src, String dst, double trafficMbps) {
        Demand toDemand() { return new Demand(new Node(src), new Node(dst), trafficMbps); }
    }
    record AcoParams(int ants, int iterations,
                     double alpha, double beta, double rho, double Q,
                     double betaCongestion) { }
    record PathResponse(String src, String dst, double costMs, List<String> hops) {
        static PathResponse from(Path p, Demand d) {
            List<String> h = p.links().stream()
                .map(l -> l.u.id() + "->" + l.v.id())
                .toList();
            return new PathResponse(d.src().id(), d.dst().id(), p.costMs(), h);
        }
    }
}
