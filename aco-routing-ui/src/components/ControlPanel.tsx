import React, { useState } from 'react';
import { AcoParams, runAco, runBaseline, PathResp } from '../api/acoApi';

/** parent receives both arrays (baseline may be empty) */
type Props = {
  onResult: (aco: PathResp[], baseline: PathResp[]) => void;
};

export const ControlPanel: React.FC<Props> = ({ onResult }) => {
  const [p, setP] = useState<AcoParams>({
    ants: 40,
    iterations: 50,
    alpha: 1,
    beta: 2,
    rho: 0.4,
    Q: 100,
    betaCongestion: 5
  });

  /* helpers */
  const runAcoOnly = async () => {
    const aco = await runAco(p);
    onResult(aco.data, []);
  };

  const runBoth = async () => {
    const [aco, base] = await Promise.all([runAco(p), runBaseline()]);
    onResult(aco.data, base.data);
  };

  return (
    <div style={{ padding: 10 }}>
      <h3>ACO Parameters</h3>

      {(
        ['ants', 'iterations', 'alpha', 'beta', 'rho', 'Q', 'betaCongestion'] as const
      ).map(k => (
        <label key={k} style={{ display: 'block' }}>
          {k}:{' '}
          <input
            type="number"
            value={p[k]}
            onChange={e => setP({ ...p, [k]: +e.target.value })}
            step="0.1"
            style={{ width: 90 }}
          />
        </label>
      ))}

      <div style={{ marginTop: 8 }}>
        <button onClick={runAcoOnly} style={{ marginRight: 6 }}>
          Run ACO
        </button>
        <button onClick={runBoth}>Run ACO + Baseline</button>
      </div>
    </div>
  );
};
