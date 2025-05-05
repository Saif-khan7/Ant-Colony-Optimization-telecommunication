import React from 'react';
import { LineChart, Line, CartesianGrid, XAxis, YAxis, Tooltip } from 'recharts';

type Point = { iter: number; best: number };
export const MetricsChart: React.FC<{ data: Point[] }> = ({ data }) => (
  <LineChart width={400} height={250} data={data}>
    <Line type="monotone" dataKey="best" stroke="#8884d8" dot={false}/>
    <CartesianGrid stroke="#ccc"/>
    <XAxis dataKey="iter"/>
    <YAxis label={{ value: 'ms', angle: -90, position: 'insideLeft' }}/>
    <Tooltip/>
  </LineChart>
);
