import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
});

export interface LinkDTO {
  u: string; v: string;
  latencyMs: number; capacityMbps: number;
}
export interface DemandDTO {
  src: string; dst: string; trafficMbps: number;
}
export interface AcoParams {
  ants: number; iterations: number;
  alpha: number; beta: number; rho: number; Q: number;
  betaCongestion: number;
}
export interface PathResp {
  src: string; dst: string; costMs: number; hops: string[];
}

export const uploadLinks  = (links: LinkDTO[])   => api.post('/topology/links',   links);
export const uploadDemands = (ds: DemandDTO[])   => api.post('/topology/demands', ds);
export const runAco        = (p: AcoParams)      => api.post<PathResp[]>('/aco/run', p);
export const runBaseline = () =>
  api.post<PathResp[]>('/baseline/run', {});