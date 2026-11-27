import useFormErrors from '../hooks/useFormErrors.js';
import { useEffect, useState } from 'react';
import Icon from './Icon.jsx';
import TemperatureChart from './TemperatureChart.jsx';
import '../styles/roasts.css';

const EVENTS = [
  { type: 'charge', short: 'CARGA', label: 'Carga' },
  { type: 'tp', short: 'TP', label: 'Punto de inflexión' },
  { type: 'dry_end', short: 'FIN SECADO', label: 'Fin del secado' },
  { type: 'fc_start', short: 'PRIMER CRACK', label: 'Inicio del primer crack' },
  { type: 'sc_start', short: 'SEGUNDO CRACK', label: 'Inicio del segundo crack' },
  { type: 'drop', short: 'CAÍDA', label: 'Caída / descarga' },
];
const CONTROLS = [
  ['heat', 'Calor / quemador', '%'],
  ['airflow', 'Flujo de aire', '%'],
  ['drum', 'Velocidad del tambor', 'rpm'],
  ['other', 'Otro ajuste', ''],
];
const pad = (value) => String(value).padStart(2, '0');
const formatTime = (seconds) => `${pad(Math.floor(seconds / 60))}:${pad(seconds % 60)}`;
const parseTime = (value) => {
  const match = /^(\d{1,3}):([0-5]\d)$/.exec(value.trim());
  return match ? Number(match[1]) * 60 + Number(match[2]) : null;
};
function detectTurningPoint(points) {
  if (points.length < 4) return null;
  let minimum = points[1];
  let minimumIndex = 1;
  for (let index = 2; index < points.length; index += 1) {
    if (points[index].temperature < minimum.temperature) {
      minimum = points[index];
      minimumIndex = index;
      continue;
    }
    const twoRisingReadings = index >= minimumIndex + 2
      && points[index - 1].temperature > minimum.temperature
      && points[index].temperature > minimum.temperature;
    if (twoRisingReadings) return minimum;
  }
  return null;
}
function orderAndDetectTurningPoint(points) {
  const ordered = points
    .map((point) => point.eventType === 'tp' ? { ...point, eventType: null } : point)
    .sort((a, b) => a.seconds - b.seconds);
  const turningPoint = detectTurningPoint(ordered);
  return turningPoint
    ? ordered.map((point) => point === turningPoint ? { ...point, eventType: 'tp' } : point)
    : ordered;
}
function eventSequenceIsValid(points) {
  const at = (type) => points.find((point) => point.eventType === type)?.seconds;
  const tp = at('tp');
  const dryEnd = at('dry_end');
  const firstCrack = at('fc_start');
  const secondCrack = at('sc_start');
  return (!dryEnd || (tp !== undefined && dryEnd > tp))
    && (!firstCrack || (dryEnd !== undefined && firstCrack > dryEnd))
    && (!secondCrack || (firstCrack !== undefined && secondCrack > firstCrack));
}
function canMark(type, events) {
  if (events.some((event) => event.type === type)) return false;
  if (type === 'dry_end') return events.some((event) => event.type === 'tp');
  if (type === 'fc_start') return events.some((event) => event.type === 'dry_end');
  if (type === 'sc_start') return events.some((event) => event.type === 'fc_start');
  return false;
}

export default function ProfileEditor({ editing, lots = [], saving = false, onSave, onClose }) {
  const [metadata, setMetadata] = useState({
    name: editing?.name || '', level: editing?.level || 'Medio',
    description: editing?.description || '', flavorNotes: editing?.flavorNotes || '',
    referenceLotId: String(editing?.referenceLotId ?? editing?.lotId ?? ''),
    referenceInputKg: editing?.referenceInputKg ?? editing?.inputWeight ?? editing?.input ?? '',
    referenceOutputKg: editing?.referenceOutputKg ?? editing?.outputWeight ?? editing?.output ?? '',
  });
  const [captureMode, setCaptureMode] = useState('manual');
  const [startedAt, setStartedAt] = useState(null);
  const [readingStartedAt, setReadingStartedAt] = useState(null);
  const [elapsed, setElapsed] = useState(0);
  const [temperature, setTemperature] = useState('');
  const [time, setTime] = useState('00:00');
  const [measurements, setMeasurements] = useState(() => orderAndDetectTurningPoint(editing?.measurements || []));
  const [editingMeasurementIndex, setEditingMeasurementIndex] = useState(null);
  const [adjustments, setAdjustments] = useState(() => [...(editing?.adjustments || [])].sort((a, b) => a.seconds - b.seconds));
  const [editingAdjustmentIndex, setEditingAdjustmentIndex] = useState(null);
  const [adjustmentType, setAdjustmentType] = useState('heat');
  const [adjustmentValue, setAdjustmentValue] = useState('');
  const [adjustmentUnit, setAdjustmentUnit] = useState('%');
  const [adjustmentTime, setAdjustmentTime] = useState('');
  const [adjustmentTemperature, setAdjustmentTemperature] = useState('');
  const {error, fields, setError, fromServer} = useFormErrors();
  const events = measurements.filter((point) => point.eventType).map((point) => {
    const definition = EVENTS.find((event) => event.type === point.eventType);
    return { ...definition, seconds: point.seconds, temperature: point.temperature };
  });
  const turningPoint = events.find((event) => event.type === 'tp');
  const latestRor = measurements.length < 2 ? null : (() => {
    const last = measurements.at(-1);
    const previous = [...measurements].reverse().slice(1).find((point) => point.seconds < last.seconds);
    return previous ? ((last.temperature - previous.temperature) / ((last.seconds - previous.seconds) / 60)).toFixed(1) : null;
  })();
  const maximumTemperature = Math.max(0, ...measurements.map((point) => point.temperature));
  const selectedLot = lots.find((lot) => String(lot.id) === String(metadata.referenceLotId))
    || lots.find((lot) => lot.code && lot.code === (editing?.referenceLotCode || editing?.lotCode));

  useEffect(() => {
    if (captureMode !== 'live' || startedAt === null) return undefined;
    const interval = window.setInterval(() => setElapsed(Math.floor((Date.now() - startedAt) / 1000)), 250);
    return () => window.clearInterval(interval);
  }, [captureMode, startedAt]);

  const setField = (event) => {
    setMetadata((current) => ({ ...current, [event.target.name]: event.target.value }));
    setError('');
  };
  const updateTemperature = (event) => {
    const value = event.target.value;
    if (captureMode === 'live' && temperature === '' && value !== '') {
      const typedAt = Date.now();
      setReadingStartedAt(typedAt);
      if (startedAt === null) { setStartedAt(typedAt); setElapsed(0); }
    }
    if (value === '') setReadingStartedAt(null);
    setTemperature(value);
    setError('');
  };
  const selectMeasurement = (index) => {
    const point = measurements[index];
    if (!point) return;
    setEditingMeasurementIndex(index);
    setEditingAdjustmentIndex(null);
    setTemperature(String(point.temperature));
    setTime(formatTime(point.eventType === 'charge' ? 0 : point.seconds));
    setError('');
  };
  const cancelMeasurementEdit = () => {
    setEditingMeasurementIndex(null);
    setTemperature('');
    setReadingStartedAt(null);
    setTime(formatTime(measurements.at(-1)?.seconds ?? 0));
    setError('');
  };
  const updateReading = () => {
    const index = editingMeasurementIndex;
    const current = measurements[index];
    const value = Number(temperature);
    const seconds = current?.eventType === 'charge' ? 0 : parseTime(time);
    if (!current) { setEditingMeasurementIndex(null); return; }
    if (temperature === '' || !Number.isFinite(value) || value < 0 || value > 300) {
      setError('Ingresa una temperatura válida entre 0 y 300 °C.', 'temperature'); return;
    }
    if (seconds === null) { setError('Escribe el tiempo como mm:ss, por ejemplo 03:25.', 'time'); return; }
    if (measurements.some((point, pointIndex) => pointIndex !== index && point.seconds === seconds)) {
      setError('Ya existe una lectura en ese tiempo.', 'time'); return;
    }
    const updated = {
      ...current,
      seconds,
      temperature: value,
      // El TP lo vuelve a detectar el sistema tras ordenar la curva.
      eventType: current.eventType === 'tp' ? null : current.eventType,
    };
    const next = orderAndDetectTurningPoint(measurements.map((point, pointIndex) => pointIndex === index ? updated : point));
    if (!eventSequenceIsValid(next)) { setError('El orden de los hitos debe ser: TP, fin del secado, primer crack y segundo crack.'); return; }
    setMeasurements(next);
    setEditingMeasurementIndex(null);
    setTemperature('');
    setReadingStartedAt(null);
    setTime(formatTime(next.at(-1)?.seconds ?? 0));
    setError('');
  };
  const selectEvent = (type) => {
    const index = measurements.findIndex((point) => point.eventType === type);
    if (index >= 0) selectMeasurement(index);
  };
  const addReading = (eventType = null) => {
    if (editingMeasurementIndex !== null) { updateReading(); return; }
    const value = Number(temperature);
    if (temperature === '' || !Number.isFinite(value) || value < 0 || value > 300) {
      setError('Ingresa una temperatura válida entre 0 y 300 °C.', 'temperature'); return;
    }
    if (measurements.length === 0 && eventType) {
      setError('La primera lectura siempre es la carga. Regístrala con el botón de temperatura.'); return;
    }
    const capturedAt = readingStartedAt ?? Date.now();
    const roastStart = startedAt ?? capturedAt;
    const seconds = measurements.length === 0 ? 0 : captureMode === 'live'
      ? Math.max(0, Math.floor((capturedAt - roastStart) / 1000))
      : parseTime(time);
    if (seconds === null) { setError('Escribe el tiempo como mm:ss, por ejemplo 03:25.', 'time'); return; }
    if (measurements.length && seconds <= measurements.at(-1).seconds) {
      setError('El tiempo debe ser posterior a la última lectura.', 'time'); return;
    }
    if (eventType && !canMark(eventType, events)) { setError('Registra primero los hitos anteriores.'); return; }
    const point = { seconds, temperature: value, eventType: eventType || (measurements.length === 0 ? 'charge' : null) };
    let next = [...measurements, point];
    if (measurements.length === 0) {
      setTime('00:00');
    }
    next = orderAndDetectTurningPoint(next);
    setMeasurements(next);
    if (captureMode === 'live') { setStartedAt(roastStart); setElapsed(seconds); }
    setTemperature('');
    setReadingStartedAt(null);
    setError('');
  };
  const selectAdjustment = (index) => {
    const item = adjustments[index];
    if (!item) return;
    setEditingAdjustmentIndex(index);
    setEditingMeasurementIndex(null);
    setTemperature('');
    setAdjustmentType(item.type);
    setAdjustmentValue(String(item.value));
    setAdjustmentUnit(item.unit || '');
    setAdjustmentTime(formatTime(item.seconds));
    setAdjustmentTemperature(item.temperature == null ? '' : String(item.temperature));
    setMetadata((current) => ({ ...current, adjustmentLabel: item.type === 'other' ? item.label : current.adjustmentLabel }));
    setError('');
  };
  const cancelAdjustmentEdit = () => {
    setEditingAdjustmentIndex(null);
    setAdjustmentValue('');
    setAdjustmentTime('');
    setAdjustmentTemperature('');
    setMetadata((current) => ({ ...current, adjustmentLabel: '' }));
    setError('');
  };
  const recordAdjustment = () => {
    const isEditing = editingAdjustmentIndex !== null;
    const seconds = captureMode === 'live'
      ? (isEditing ? parseTime(adjustmentTime) : startedAt === null ? null : Math.max(0, Math.floor((Date.now() - startedAt) / 1000)))
      : (isEditing ? parseTime(adjustmentTime) : parseTime(time));
    if (!events.some((event) => event.type === 'charge')) { setError('Registra primero la temperatura de carga.'); return; }
    if (seconds === null || seconds < 0 || (!isEditing && seconds < measurements.at(-1).seconds)) { setError('El tiempo del ajuste debe ser igual o posterior a la última lectura.', isEditing ? 'adjustmentTime' : 'time'); return; }
    if (adjustmentType === 'other' && !metadata.adjustmentLabel?.trim()) { setError('Escribe el nombre del otro ajuste.', 'adjustmentLabel'); return; }
    if (adjustmentValue.trim() === '' || !Number.isFinite(Number(adjustmentValue))) { setError('Escribe un valor numérico para el ajuste.', 'adjustmentValue'); return; }
    const tempValue = isEditing
      ? (adjustmentTemperature === '' ? null : Number(adjustmentTemperature))
      : (temperature === '' ? measurements.at(-1)?.temperature ?? null : Number(temperature));
    if (tempValue !== null && (!Number.isFinite(tempValue) || tempValue < 0 || tempValue > 300)) { setError('La temperatura del ajuste debe estar entre 0 y 300 °C.', isEditing ? 'adjustmentTemperature' : 'temperature'); return; }
    const control = CONTROLS.find(([type]) => type === adjustmentType);
    const item = {
      ...(isEditing ? adjustments[editingAdjustmentIndex] : { id: Date.now() }),
      type: adjustmentType,
      label: adjustmentType === 'other' ? metadata.adjustmentLabel.trim() : control[1],
      value: Number(adjustmentValue), unit: adjustmentUnit.trim(), seconds,
      temperature: tempValue,
    };
    const next = isEditing
      ? adjustments.map((current, index) => index === editingAdjustmentIndex ? item : current)
      : [...adjustments, item];
    setAdjustments(next.sort((a, b) => a.seconds - b.seconds));
    if (isEditing) setEditingAdjustmentIndex(null);
    setAdjustmentValue('');
    setAdjustmentTime('');
    setAdjustmentTemperature('');
    setMetadata((current) => ({ ...current, adjustmentLabel: '' }));
    setError('');
  };
  const saveProfile = async (event) => {
    event.preventDefault();
    setError('');
    if (metadata.name.trim().length < 3) { setError('El nombre debe tener al menos 3 caracteres.', 'name'); return; }
    const inputKg = Number(metadata.referenceInputKg);
    const outputKg = Number(metadata.referenceOutputKg);
    if (!Number.isFinite(inputKg) || inputKg <= 0) { setError('El peso inicial debe ser mayor a cero.', 'referenceInputKg'); return; }
    if (!Number.isFinite(outputKg) || outputKg <= 0 || outputKg > inputKg) { setError('El peso final debe ser mayor a cero y no superar el inicial.', 'referenceOutputKg'); return; }
    if (measurements.length < 4) { setError('Registra al menos cuatro temperaturas para guardar la curva y detectar el TP.'); return; }
    if (!turningPoint) { setError('El sistema aún no detecta el punto de inflexión. Registra dos lecturas ascendentes después del mínimo.'); return; }
    for (const required of ['dry_end', 'fc_start']) {
      if (!events.some((item) => item.type === required)) { setError('Marca Fin del secado e Inicio del primer crack antes de guardar.'); return; }
    }
    const last = measurements.at(-1);
    if (last.eventType && last.eventType !== 'drop') { setError('Agrega una lectura final después del último hito; al guardar se marcará como caída/descarga.'); return; }
    const orderedMeasurements = orderAndDetectTurningPoint(measurements);
    if (!eventSequenceIsValid(orderedMeasurements)) { setError('Revisa el orden de los hitos antes de guardar.'); return; }
    const savedMeasurements = orderedMeasurements.map((point, index) => ({
      ...point,
      eventType: index === orderedMeasurements.length - 1 ? 'drop' : point.eventType === 'drop' ? null : point.eventType,
    }));
    const first = savedMeasurements[0];
    const final = savedMeasurements.at(-1);
    const { adjustmentLabel: _adjustmentLabel, ...profileMetadata } = metadata;
    const payload = {
      ...(editing || {}), ...profileMetadata,
      createdAt: editing?.createdAt || new Date().toISOString(),
      referenceLotId: selectedLot?.id ?? null,
      referenceLotCode: selectedLot?.code ?? null,
      referenceInputKg: inputKg,
      referenceOutputKg: outputKg,
      initialTemp: first.temperature,
      finalTemp: final.temperature,
      duration: Number((final.seconds / 60).toFixed(2)),
      measurements: savedMeasurements,
      adjustments,
    };
    try { await onSave(payload); }
    catch (saveError) { fromServer(saveError, ['name','level','description','flavorNotes','referenceLotId','referenceInputKg','referenceOutputKg']); }
  };

  return <div className="modal-overlay profile-builder-overlay" onMouseDown={(event) => { if (event.target === event.currentTarget && !saving) onClose(); }}>
    <form className="modal-card profile-builder-modal" onSubmit={saveProfile} noValidate>
      <div className="modal-header"><div><span className="eyebrow">CURVA DE REFERENCIA</span><h3>{editing ? 'Editar perfil de tueste' : 'Crear perfil de tueste'}</h3><p>Registra los tiempos y temperaturas para construir la curva que quieres repetir.</p></div><button type="button" className="icon-button" aria-label="Cerrar" disabled={saving} onClick={onClose}><Icon name="close" /></button></div>
      <div className="profile-builder-content roast-form">
        <section className="roast-section profile-metadata-section"><span className="form-section-title"><span>1</span> Datos del perfil</span><div className="form-grid">
          <label className="field field-wide">Nombre del perfil<input className="input" name="name" value={metadata.name} onChange={setField} minLength="3" maxLength="100" placeholder="Ej. Balanceado para espresso" required />{fields.name && <small role="alert">{fields.name}</small>}</label>
          <label className="field">Nivel de tueste<select className="input" name="level" value={metadata.level} onChange={setField}><option>Claro</option><option>Medio</option><option>Oscuro</option></select>{fields.level && <small role="alert">{fields.level}</small>}</label>
          <label className="field">Descripción<input className="input" name="description" value={metadata.description} onChange={setField} maxLength="500" placeholder="Qué caracteriza este perfil" />{fields.description && <small role="alert">{fields.description}</small>}</label>
          <label className="field field-wide">Notas de sabor<textarea className="input textarea" name="flavorNotes" value={metadata.flavorNotes} onChange={setField} rows="2" maxLength="500" placeholder="Aroma, dulzor, acidez, cuerpo…" />{fields.flavorNotes && <small role="alert">{fields.flavorNotes}</small>}</label>
          <label className="field field-wide">Lote de referencia (opcional)<select className="input" value={selectedLot?.id ?? metadata.referenceLotId} onChange={(event) => setMetadata((current) => ({ ...current, referenceLotId: event.target.value }))}><option value="">Sin lote de referencia</option>{lots.map((lot) => <option key={lot.id} value={lot.id}>{lot.code} · {lot.coffee}</option>)}</select>{fields.referenceLotId && <small role="alert">{fields.referenceLotId}</small>}</label>
          <label className="field">Peso inicial (kg)<input className="input" type="number" min="0.01" step="0.01" value={metadata.referenceInputKg} onChange={(event) => setMetadata((current) => ({ ...current, referenceInputKg: event.target.value }))} placeholder="8.00" required />{fields.referenceInputKg && <small role="alert">{fields.referenceInputKg}</small>}</label>
          <label className="field">Peso final (kg)<input className="input" type="number" min="0.01" step="0.01" max={metadata.referenceInputKg || undefined} value={metadata.referenceOutputKg} onChange={(event) => setMetadata((current) => ({ ...current, referenceOutputKg: event.target.value }))} placeholder="6.72" required />{fields.referenceOutputKg && <small role="alert">{fields.referenceOutputKg}</small>}</label>
        </div></section>
        <section className="roast-section live-temperature-section profile-curve-section">
          <div className="live-section-heading"><span className="form-section-title"><span>2</span> Construcción de la curva</span>{captureMode === 'live' && <div className={`roast-timer ${startedAt !== null ? 'roast-timer-active' : ''}`}><Icon name="clock" size={16} /><strong>{formatTime(elapsed)}</strong><span>{startedAt === null ? 'ESPERANDO PRIMERA TEMPERATURA' : 'EN CURSO'}</span></div>}</div>
          <div className="profile-capture-modes"><button type="button" className={`button ${captureMode === 'manual' ? 'button-primary' : 'button-outline'}`} disabled={measurements.length > 0} onClick={() => { setCaptureMode('manual'); setElapsed(0); setStartedAt(null); }}>Construir manualmente</button><button type="button" className={`button ${captureMode === 'live' ? 'button-primary' : 'button-outline'}`} disabled={measurements.length > 0} onClick={() => { setCaptureMode('live'); setTime('00:00'); setError(''); }}>Crear curva en tiempo real</button></div>
          <TemperatureChart points={measurements} events={events} elapsed={captureMode === 'live' ? elapsed : measurements.at(-1)?.seconds || 0} emptyLabel="Registra la primera temperatura para iniciar la curva." />
          <div className="temperature-entry profile-temperature-entry">
            <label className="field">Temperatura (°C)<input className="input" type="number" min="0" max="300" step="0.1" value={temperature} onChange={updateTemperature} placeholder="Ej. 185.5" required />{fields.temperature && <small role="alert">{fields.temperature}</small>}</label>
            {(captureMode === 'manual' || editingMeasurementIndex !== null) && <label className="field">Tiempo (mm:ss)<input className="input" type="text" inputMode="numeric" pattern="\d{1,3}:[0-5]\d" value={editingMeasurementIndex !== null ? time : measurements.length === 0 ? '00:00' : time} onChange={(event) => setTime(event.target.value)} disabled={measurements.length === 0 || measurements[editingMeasurementIndex]?.eventType === 'charge'} placeholder="Ej. 03:25" />{fields.time && <small role="alert">{fields.time}</small>}</label>}
            <div className="measurement-form-actions"><button className="button button-primary" type="button" disabled={!temperature} onClick={() => addReading()}>{editingMeasurementIndex !== null ? 'Editar lectura' : <><Icon name="plus" size={17} /> Registrar temperatura</>}</button>{editingMeasurementIndex !== null && <button className="button button-outline" type="button" onClick={cancelMeasurementEdit}>Cancelar edición</button>}</div>
            <div className="temperature-readings"><b>{measurements.length}</b><span>lecturas</span><small>Máxima: {maximumTemperature ? `${maximumTemperature} °C` : '—'}</small><small>ROR: {latestRor === null ? '—' : `${latestRor} °C/min`}</small></div>
          </div>
          {measurements.length > 0 && <div className="measurement-list" aria-label="Lecturas guardadas">{measurements.map((point, index) => { const event = EVENTS.find((item) => item.type === point.eventType); return <button className={`measurement-row ${point.eventType ? `measurement-row-${point.eventType}` : ''} ${editingMeasurementIndex === index ? 'measurement-row-selected' : ''}`} key={`${point.seconds}-${index}`} type="button" aria-label={`Editar ${event?.label || `lectura ${index + 1}`}, ${point.temperature} grados, ${formatTime(point.seconds)}`} onClick={() => selectMeasurement(index)}><span>{event?.label || `Lectura ${index + 1}`}</span><b>{point.temperature} °C</b><small>{formatTime(point.seconds)}</small></button>; })}</div>}
          {events.some((event) => event.type === 'charge') && <div className="roast-event-panel"><h4>Hitos del tueste</h4><p>Selecciona un hito guardado para editar su tiempo y temperatura arriba.</p><div className="automatic-event-note">{turningPoint ? <>Punto de inflexión detectado: <b>{formatTime(turningPoint.seconds)} · {turningPoint.temperature} °C</b></> : 'El sistema marcará el punto de inflexión cuando confirme que la temperatura comenzó a subir.'}</div><div className="roast-event-buttons">{EVENTS.filter((item) => !['charge', 'tp', 'drop'].includes(item.type)).map((item) => { const marked = events.find((event) => event.type === item.type); const markedIndex = measurements.findIndex((point) => point.eventType === item.type); return <button className={`roast-event-button roast-event-${item.type} ${marked ? 'roast-event-marked' : ''} ${editingMeasurementIndex === markedIndex && marked ? 'roast-event-selected' : ''}`} key={item.type} type="button" disabled={marked ? false : editingMeasurementIndex !== null || !temperature || !canMark(item.type, events)} onClick={() => marked ? selectMeasurement(markedIndex) : addReading(item.type)} title={marked ? 'Seleccionar este hito para editarlo' : undefined}><b>{marked ? '✓' : '+'} {item.short}</b><span>{marked ? `${formatTime(marked.seconds)} · ${marked.temperature} °C · Editar` : item.label}</span></button>; })}</div><div className="roast-event-note">El segundo crack es opcional. La caída se registra automáticamente al guardar el perfil.</div></div>}
          {events.length > 0 && <div className="saved-event-list">{events.map((event) => { const index = measurements.findIndex((point) => point.eventType === event.type); return <button className={`saved-event-chip saved-event-chip-${event.type} ${editingMeasurementIndex === index ? 'saved-event-chip-selected' : ''}`} type="button" key={event.type} onClick={() => selectMeasurement(index)}><b>{event.short}</b> {formatTime(event.seconds)} · {event.temperature} °C · Editar</button>; })}</div>}
          {measurements.length > 0 && <section className="machine-adjustment-panel"><div><h4>Ajustes de la máquina</h4><p>{editingAdjustmentIndex === null ? 'Selecciona un ajuste guardado para modificarlo en este módulo.' : 'Edita aquí los datos del ajuste seleccionado.'}</p></div>
            <div className={`machine-adjustment-entry ${editingAdjustmentIndex !== null ? 'is-editing' : ''}`}><label className="field">Control<select className="input" value={adjustmentType} onChange={(event) => { const next = event.target.value; setAdjustmentType(next); setAdjustmentUnit(CONTROLS.find(([type]) => type === next)?.[2] || ''); }} >{CONTROLS.map(([type, label]) => <option key={type} value={type}>{label}</option>)}</select>{fields.adjustmentType && <small role="alert">{fields.adjustmentType}</small>}</label><label className="field">{editingAdjustmentIndex === null ? 'Nuevo valor' : 'Valor'}<input className="input" type="number" step="any" value={adjustmentValue} onChange={(event) => setAdjustmentValue(event.target.value)} placeholder="Ej. 65" />{fields.adjustmentValue && <small role="alert">{fields.adjustmentValue}</small>}</label><label className="field">Unidad<input className="input" value={adjustmentUnit} onChange={(event) => setAdjustmentUnit(event.target.value)} placeholder="%, rpm…" />{fields.adjustmentUnit && <small role="alert">{fields.adjustmentUnit}</small>}</label>{editingAdjustmentIndex !== null && <><label className="field">Tiempo (mm:ss)<input className="input" type="text" inputMode="numeric" pattern="\d{1,3}:[0-5]\d" value={adjustmentTime} onChange={(event) => setAdjustmentTime(event.target.value)} />{fields.adjustmentTime && <small role="alert">{fields.adjustmentTime}</small>}</label><label className="field">Temperatura (°C)<input className="input" type="number" min="0" max="300" step="0.1" value={adjustmentTemperature} onChange={(event) => setAdjustmentTemperature(event.target.value)} placeholder="Opcional" />{fields.adjustmentTemperature && <small role="alert">{fields.adjustmentTemperature}</small>}</label></>}<div className="adjustment-form-actions"><button className="button button-outline" type="button" onClick={recordAdjustment}>{editingAdjustmentIndex === null ? 'Registrar ajuste' : 'Guardar cambios'}</button>{editingAdjustmentIndex !== null && <button className="button button-outline" type="button" onClick={cancelAdjustmentEdit}>Cancelar edición</button>}</div></div>
            {adjustmentType === 'other' && <label className="field">Nombre del otro ajuste<input className="input" value={metadata.adjustmentLabel || ''} onChange={(event) => setMetadata((current) => ({ ...current, adjustmentLabel: event.target.value }))} placeholder="Ej. Presión" />{fields.adjustmentLabel && <small role="alert">{fields.adjustmentLabel}</small>}</label>}
            {adjustments.length ? <div className="machine-adjustment-list">{adjustments.map((item, index) => <button className={`machine-adjustment-row ${editingAdjustmentIndex === index ? 'machine-adjustment-row-selected' : ''}`} type="button" key={item.id || `${item.seconds}-${index}`} onClick={() => selectAdjustment(index)}><b>{formatTime(item.seconds)}</b><span>{item.label}: {item.value}{item.unit ? ` ${item.unit}` : ''}</span>{item.temperature != null && <small>{item.temperature} °C</small>}</button>)}</div> : <span className="adjustment-empty">Aún no se registran cambios de máquina.</span>}
          </section>}
          {error && <div className="error-note" role="alert">{error}</div>}
          <div className="roast-actions"><button className="button button-outline" type="button" disabled={saving} onClick={onClose}>Cancelar</button><button className="button button-primary" type="submit" disabled={saving}>{saving ? 'Guardando…' : <><Icon name="check" size={17} /> Guardar perfil</>}</button></div>
        </section>
      </div>
    </form>
  </div>;
}
