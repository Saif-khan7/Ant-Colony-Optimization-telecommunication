import React, { useEffect, useMemo, useRef } from 'react';
import CytoscapeComponent from 'react-cytoscapejs';
import cytoscape from 'cytoscape';

export interface GraphEdge {
  id: string;        // undirected key
  source: string;
  target: string;
  pheromone: number; // visit‑count (≥1)
}

type Props = { edges: GraphEdge[] };

export const GraphCanvas: React.FC<Props> = ({ edges }) => {
  const cyRef = useRef<cytoscape.Core | null>(null);

  /* 1 — build elements & normalise */
  const elements = useMemo(() => {
    const raw = edges.flatMap(e => [
      { data: { id: e.source, label: e.source } },
      { data: { id: e.target, label: e.target } },
      {
        data: {
          id: e.id,
          source: e.source,
          target: e.target,
          pher: e.pheromone
        }
      }
    ]);
    return CytoscapeComponent.normalizeElements(raw);
  }, [edges]);

  /* 2 — colour + thickness when pheromone changes */
  useEffect(() => {
    if (!cyRef.current) return;
    const max = Math.max(1, ...edges.map(e => e.pheromone));

    edges.forEach(e => {
      const ratio = e.pheromone / max;          // 0..1
      const hue = 240 - ratio * 240;            // blue → red
      cyRef.current!
        .edges(`#${e.id}`)
        .style({
          width: 2 + ratio * 6,
          'line-color': `hsl(${hue},100%,50%)`
        });
    });
  }, [edges]);

  /* 3 — layout whenever element set changes */
  useEffect(() => {
    if (!cyRef.current) return;
    cyRef.current.layout({ name: 'cose', animate: true, padding: 30 }).run();
    cyRef.current.fit(undefined, 30);
  }, [elements]);

  /* 4 — render */
  return (
    <CytoscapeComponent
      elements={elements}
      cy={cy => {
        cyRef.current = cy;
        cy.layout({ name: 'cose', padding: 30 }).run();
        cy.fit(undefined, 30);
      }}
      style={{ width: '100%', height: '100%' }}
      layout={{ name: 'preset' }}
      stylesheet={[
        {
          selector: 'node',
          style: {
            label: 'data(label)',
            'text-valign': 'center',
            color: '#fff',
            'background-color': '#0288d1',
            width: 24,
            height: 24,
            'font-size': 12
          }
        },
        { selector: 'edge', style: { 'curve-style': 'bezier' } }
      ]}
    />
  );
};
