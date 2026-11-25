import '../styles/temperatures.css';

const CHART = { width: 800, height: 320, left: 58, right: 20, top: 22, bottom: 44 };

export default function TemperatureChart({ points = [], events = [], elapsed = 0, referencePoints = [], referenceLabel = 'Perfil objetivo', emptyLabel = 'Registra una temperatura para iniciar la curva.' }) {
  const plotWidth = CHART.width - CHART.left - CHART.right;
  const plotHeight = CHART.height - CHART.top - CHART.bottom;
  const lastSecond = Math.max(points.at(-1)?.seconds || 0, referencePoints.at(-1)?.seconds || 0, ...events.map((event) => event.seconds || 0));
  const maxSeconds = Math.max(15 * 60, Math.ceil(Math.max(elapsed, lastSecond) / 300) * 300);
  const x = (seconds) => CHART.left + (seconds / maxSeconds) * plotWidth;
  const y = (temperature) => CHART.top + ((250 - temperature) / 150) * plotHeight;
  const temperatures = [100, 130, 160, 190, 220, 250];
  const minuteStep = maxSeconds > 1800 ? 10 : 3;
  const timeTicks = Array.from({ length: Math.floor(maxSeconds / (minuteStep * 60)) + 1 }, (_, i) => i * minuteStep * 60);
  const line = points.map((point) => `${x(point.seconds)},${y(point.temperature)}`).join(' ');
  const referenceLine = referencePoints.map((point) => `${x(point.seconds)},${y(point.temperature)}`).join(' ');

  return <div className="temperature-chart" role="img" aria-label="Curva de temperatura del tueste">
    <svg viewBox={`0 0 ${CHART.width} ${CHART.height}`} preserveAspectRatio="none">
      {temperatures.map((temperature) => <g key={temperature}>
        <line className="chart-gridline" x1={CHART.left} y1={y(temperature)} x2={CHART.width - CHART.right} y2={y(temperature)} />
        <text className="chart-axis-label" x={CHART.left - 10} y={y(temperature) + 4} textAnchor="end">{temperature}°</text>
      </g>)}
      {timeTicks.map((seconds) => <g key={seconds}>
        <line className="chart-gridline chart-gridline-vertical" x1={x(seconds)} y1={CHART.top} x2={x(seconds)} y2={CHART.height - CHART.bottom} />
        <text className="chart-axis-label" x={x(seconds)} y={CHART.height - 17} textAnchor="middle">{Math.floor(seconds / 60)} min</text>
      </g>)}
      <text className="chart-axis-title" x={15} y={CHART.height / 2} textAnchor="middle" transform={`rotate(-90 15 ${CHART.height / 2})`}>Temperatura °C</text>
      <text className="chart-axis-title" x={CHART.width / 2} y={CHART.height - 1} textAnchor="middle">Tiempo transcurrido</text>
      {referencePoints.length > 1 && <polyline className="profile-reference-line" points={referenceLine} />}
      {points.length > 1 && <polyline className="temperature-line" points={line} />}
      {points.map((point, index) => {
        // También reconoce tuestes guardados antes de que las lecturas incluyeran eventType.
        const roastEvent = events.find((event) => event.type === point.eventType)
          || events.find((event) => event.seconds === point.seconds && event.temperature === point.temperature);
        const eventType = roastEvent?.type;
        const eventName = roastEvent?.label || roastEvent?.short;
        const tooltip = `${Math.floor(point.seconds / 60).toString().padStart(2, '0')}:${(point.seconds % 60).toString().padStart(2, '0')} · ${point.temperature} °C${eventName ? ` · ${eventName}` : ''}`;
        return <g className="temperature-point-group" key={`${point.seconds}-${index}`}>
          <title>{tooltip}</title>
          <circle className={`temperature-point-halo ${eventType ? `temperature-point-${eventType}` : ''}`} cx={x(point.seconds)} cy={y(point.temperature)} r="7" />
          <circle className={`temperature-point ${eventType ? `temperature-point-${eventType}` : ''}`} cx={x(point.seconds)} cy={y(point.temperature)} r="4" />
        </g>;
      })}
      {events.map((event, index) => {
        const eventX = x(event.seconds);
        const anchor = eventX > CHART.width - 100 ? 'end' : 'start';
        const labelX = anchor === 'end' ? eventX - 5 : eventX + 5;
        return <g key={`${event.type}-${event.seconds}`}>
          <line className={`chart-event-line chart-event-${event.type}`} x1={eventX} y1={CHART.top} x2={eventX} y2={CHART.height - CHART.bottom} />
          <text className={`chart-event-label chart-event-${event.type}`} x={labelX} y={CHART.top + 12 + (index % 3) * 15} textAnchor={anchor}>{event.short || event.type.toUpperCase()}</text>
        </g>;
      })}
    </svg>
    {referencePoints.length > 0 && <div className="temperature-chart-legend"><span><i className="legend-profile" />{referenceLabel}</span><span><i className="legend-current" />Tueste actual</span></div>}
    {points.length === 0 && <div className="chart-empty">{emptyLabel}</div>}
  </div>;
}
