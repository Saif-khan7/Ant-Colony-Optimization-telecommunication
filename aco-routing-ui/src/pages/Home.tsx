import React, { useState } from 'react';
import { UploadPanel } from '../components/UploadPanel';
import { ControlPanel } from '../components/ControlPanel';
import { GraphCanvas, GraphEdge } from '../components/GraphCanvas';
import { PathResp } from '../api/acoApi';

/* undirected key, so A_B == B_A */
const idOf = (u: string, v: string) => [u, v].sort().join('_');

/* convert a PathResp array → unique edges with visit‑count in pheromone */
const buildEdges = (paths: PathResp[]): GraphEdge[] => {
  const m = new Map<string, GraphEdge>();
  paths.forEach(p =>
    p.hops.forEach(h => {
      const [s, t] = h.split('->');
      const id = idOf(s, t);
      const e = m.get(id) ?? { id, source: s, target: t, pheromone: 0 };
      e.pheromone += 1;
      m.set(id, e);
    })
  );
  return [...m.values()];
};

export const Home: React.FC = () => {
  const [acoEdges, setAco] = useState<GraphEdge[]>([]);
  const [baseEdges, setBase] = useState<GraphEdge[]>([]);

  /* receives both result arrays from ControlPanel */
  const handleResult = (aco: PathResp[], baseline: PathResp[]) => {
    setAco(buildEdges(aco));
    setBase(buildEdges(baseline));
  };

  return (
    <div style={{ display: 'flex', height: '100%' }}>
      {/* sidebar */}
      <div style={{ width: 300, borderRight: '1px solid #444', overflow: 'auto' }}>
        <UploadPanel />
        <ControlPanel onResult={handleResult} />
      </div>

      {/* two graphs side‑by‑side */}
      <div style={{ flex: 1, display: 'flex', minHeight: 600 }}>
        <div style={{ flex: 1, borderRight: '1px solid #444' }}>
          <h4 style={{ textAlign: 'center', margin: 4 }}>ACO</h4>
          <GraphCanvas edges={acoEdges} />
        </div>
        <div style={{ flex: 1 }}>
          <h4 style={{ textAlign: 'center', margin: 4 }}>Baseline</h4>
          <GraphCanvas edges={baseEdges} />
        </div>
      </div>
    </div>
  );
};
