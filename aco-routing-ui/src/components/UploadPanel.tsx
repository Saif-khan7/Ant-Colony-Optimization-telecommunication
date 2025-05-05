import React, { useState } from 'react';
import { uploadLinks, uploadDemands } from '../api/acoApi';

export const UploadPanel: React.FC = () => {
  const [linksText, setLinks] = useState('');
  const [demandsText, setDemands] = useState('');
  const [log, setLog] = useState<string[]>([]);

  const pushLog = (msg: string) => setLog(l => [msg, ...l.slice(0, 4)]);

  const sendLinks = async () => {
    try {
      await uploadLinks(JSON.parse(linksText));
      pushLog('✓ links uploaded');
    } catch (e) {
      pushLog('✗ links JSON invalid');
    }
  };

  const sendDemands = async () => {
    try {
      await uploadDemands(JSON.parse(demandsText));
      pushLog('✓ demands uploaded');
    } catch {
      pushLog('✗ demands JSON invalid');
    }
  };

  return (
    <div style={{ padding: 10, borderBottom: '1px solid #444' }}>
      <h3>Load Topology</h3>
      <textarea rows={6} style={{ width: '100%' }}
                placeholder="paste links JSON…" value={linksText}
                onChange={e => setLinks(e.target.value)}/>
      <button onClick={sendLinks}>Upload Links</button>

      <textarea rows={4} style={{ width: '100%', marginTop: 8 }}
                placeholder="paste demands JSON…" value={demandsText}
                onChange={e => setDemands(e.target.value)}/>
      <button onClick={sendDemands}>Upload Demands</button>

      <ul style={{ fontSize: 12, marginTop: 6 }}>
        {log.map((l,i) => <li key={i}>{l}</li>)}
      </ul>
    </div>
  );
};
