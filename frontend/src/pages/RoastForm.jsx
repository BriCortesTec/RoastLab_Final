import { useEffect, useState } from 'react';
import Icon from '../components/Icon.jsx';
import TemperatureChart from '../components/TemperatureChart.jsx';
import '../styles/roasts.css';

const emptyRoast = () => ({
  name: '',
  date: new Date().toISOString().slice(0, 10),
  lotCode: '',
  profile: '',
  input: '',
  output: '',
  observations: '',
});

function formatTime(totalSeconds) {
  const minutes = Math.floor(totalSeconds / 60).toString().padStart(2, '0');
  const seconds = (totalSeconds % 60).toString().padStart(2, '0');
  return `${minutes}:${seconds}`;
}

const ROAST_EVENTS = [
  { type: 'charge', short: 'CARGA', label: 'Carga' },
  { type: 'tp', short: 'TP', label: 'Punto de inflexión' },
  { type: 'dry_end', short: 'FIN SECADO', label: 'Fin del secado' },
  { type: 'fc_start', short: 'PRIMER CRACK', label: 'Inicio del primer crack' },
  { type: 'sc_start', short: 'SEGUNDO CRACK', label: 'Inicio del segundo crack' },
  { type: 'drop', short: 'CAÍDA', label: 'Caída / descarga' },
];

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
    const hasTwoRisingReadings = index >= minimumIndex + 2
      && points[index - 1].temperature > minimum.temperature
      && points[index].temperature > minimum.temperature;
    if (hasTwoRisingReadings) return minimum;
  }
  return null;
}

function makeEvent(type, point) {
  const definition = ROAST_EVENTS.find((event) => event.type === type);
  return {
    type,
    short: definition.short,
    label: definition.label,
    seconds: point.seconds,
    temperature: point.temperature,
    capturedAt: point.capturedAt,
  };
}

export default function RoastForm({ data, onSave }) {
  const [form, setForm] = useState(emptyRoast);
  const [startedAt, setStartedAt] = useState(null);
  const [phase, setPhase] = useState('setup');
  const [elapsed, setElapsed] = useState(0);
  const [temperature, setTemperature] = useState('');
  const [readingStartedAt, setReadingStartedAt] = useState(null);
  const [measurements, setMeasurements] = useState([]);
  const [events, setEvents] = useState([]);
  const [error, setError] = useState('');
  const started = phase !== 'setup';

  useEffect(() => {
    if (phase !== 'recording' || startedAt === null) return undefined;
    const intervalId = window.setInterval(() => {
      setElapsed(Math.floor((Date.now() - startedAt) / 1000));
    }, 250);
    return () => window.clearInterval(intervalId);
  }, [phase, startedAt]);

  const selectedLot = data.lots.find((lot) => lot.code === form.lotCode);
  const maximumTemperature = Math.max(0, ...measurements.map((point) => point.temperature));

  const updateField = (event) => {
    setForm({ ...form, [event.target.name]: event.target.value });
    setError('');
  };

  const updateTemperature = (event) => {
    const nextValue = event.target.value;
    if (temperature === '' && nextValue !== '') {
      const typedAt = Date.now();
      setReadingStartedAt(typedAt);
      if (startedAt === null) {
        setStartedAt(typedAt);
        setElapsed(0);
      }
    }
    if (nextValue === '') setReadingStartedAt(null);
    setTemperature(nextValue);
    setError('');
  };

  const beginRoast = () => {
    setError('');
    if (form.name.trim().length < 3) return setError('Escribe un nombre de al menos 3 caracteres.');
    if (!form.date) return setError('Selecciona la fecha del tueste.');
    if (!selectedLot) return setError('Selecciona el lote que vas a tostar.');
    if (Number(form.input) <= 0) return setError('El peso de entrada debe ser mayor a cero.');
    if (Number(form.input) > Number(selectedLot.weight)) return setError(`El lote solo tiene ${selectedLot.weight} kg disponibles.`);
    setMeasurements([]);
    setEvents([]);
    setElapsed(0);
    setStartedAt(null);
    setReadingStartedAt(null);
    setPhase('recording');
  };

  const saveReading = (eventType = null) => {
    const value = Number(temperature);
    if (!Number.isFinite(value) || value < 0 || value > 300) {
      setError('Ingresa una temperatura válida entre 0 y 300 °C.');
      return;
    }

    const capturedAt = readingStartedAt ?? Date.now();
    const firstReading = measurements.length === 0;
    const roastStart = startedAt ?? capturedAt;
    const seconds = firstReading ? 0 : Math.max(0, Math.floor((capturedAt - roastStart) / 1000));
    const point = {
      seconds,
      temperature: value,
      capturedAt: new Date(capturedAt).toISOString(),
      eventType: eventType ?? (firstReading ? 'charge' : null),
    };
    let nextMeasurements = [...measurements, point];
    let nextEvents = [...events];

    if (firstReading) nextEvents.push(makeEvent('charge', point));
    if (eventType) nextEvents.push(makeEvent(eventType, point));

    const turningPoint = events.some((savedEvent) => savedEvent.type === 'tp')
      ? null
      : detectTurningPoint(nextMeasurements);
    if (turningPoint) {
      nextMeasurements = nextMeasurements.map((reading) => reading === turningPoint
        ? { ...reading, eventType: 'tp' }
        : reading);
      nextEvents.push(makeEvent('tp', turningPoint));
    }

    setMeasurements(nextMeasurements);
    setEvents(nextEvents.sort((a, b) => a.seconds - b.seconds));
    if (startedAt === null) setStartedAt(roastStart);
    if (eventType === 'drop') {
      setElapsed(seconds);
      setPhase('finishing');
    }
    setTemperature('');
    setReadingStartedAt(null);
    setError('');
  };

  const registerTemperature = (event) => {
    event.preventDefault();
    saveReading();
  };

  const markEvent = (type) => {
    setError('');
    if (temperature === '') return setError('Escribe la temperatura antes de registrar este hito.');
    if (events.some((savedEvent) => savedEvent.type === type)) return setError('Este hito ya fue registrado.');
    if (type === 'drop' && measurements.length < 2) return setError('Registra al menos dos temperaturas antes de la caída.');
    saveReading(type);
  };

  const canMarkEvent = (type) => {
    if (events.some((event) => event.type === type)) return false;
    if (type === 'dry_end') return events.some((event) => event.type === 'tp');
    if (type === 'fc_start') return events.some((event) => event.type === 'dry_end');
    if (type === 'sc_start' || type === 'drop') return events.some((event) => event.type === 'fc_start');
    return false;
  };

  const finishRoast = () => {
    setError('');
    if (measurements.length < 2) return setError('Registra al menos dos temperaturas para guardar la curva.');
    const inputWeight = Number(form.input);
    const outputWeight = Number(form.output);
    if (outputWeight <= 0) return setError('Escribe el peso de salida.');
    if (outputWeight > inputWeight) return setError('El peso de salida no puede superar el peso de entrada.');
    const requiredEvents = ['charge', 'tp', 'dry_end', 'fc_start', 'drop'];
    if (requiredEvents.some((type) => !events.some((event) => event.type === type))) return setError('Marca Charge, TP, Dry End, FCs y Drop antes de guardar.');

    const durationSeconds = events.find((event) => event.type === 'drop')?.seconds ?? elapsed;
    const roast = {
      id: Date.now(),
      name: form.name.trim(),
      date: form.date,
      lot: `${selectedLot.code} · ${selectedLot.coffee}`,
      lotCode: selectedLot.code,
      profile: form.profile,
      duration: Number((durationSeconds / 60).toFixed(2)),
      input: inputWeight,
      output: outputWeight,
      maxTemp: maximumTemperature,
      observations: form.observations.trim(),
      measurements,
      events,
      status: 'Completado',
    };
    onSave(roast);
  };

  const discardRoast = () => {
    if (!window.confirm('¿Descartar este registro y sus temperaturas?')) return;
    setForm(emptyRoast());
    setStartedAt(null);
    setPhase('setup');
    setElapsed(0);
    setTemperature('');
    setReadingStartedAt(null);
    setMeasurements([]);
    setEvents([]);
    setError('');
  };

  return <section className="panel roast-form">
    <div className="panel-head">
      <div><h3>{phase === 'setup' ? 'Preparar nuevo tueste' : phase === 'recording' ? 'Tueste en curso' : 'Guardar resultado del tueste'}</h3><p>{phase === 'setup' ? 'Completa los datos y comienza a capturar la curva.' : phase === 'recording' ? 'Registra las temperaturas conforme avanza la tanda.' : 'Confirma el peso final y agrega observaciones.'}</p></div>
      
    </div>

    <div className="roast-section">
      <span className="form-section-title"><span>1</span> Datos del tueste</span>
      <div className="form-grid">
        <label className="field field-wide">Nombre del tueste<input className="input" name="name" value={form.name} onChange={updateField} placeholder="Ej. Tueste de la mañana #025" minLength="3" maxLength="100" disabled={started} required /></label>
        <label className="field">Fecha del tueste<input className="input" type="date" name="date" value={form.date} onChange={updateField} disabled={started} required /></label>
        <label className="field">Lote de café<select className="input" name="lotCode" value={form.lotCode} onChange={updateField} disabled={started} required><option value="">Selecciona un lote…</option>{data.lots.map((lot) => <option key={lot.id} value={lot.code} disabled={Number(lot.weight) <= 0}>{lot.code} · {lot.coffee} ({lot.weight} kg)</option>)}</select></label>
        <label className="field">Perfil de tueste<select className="input" name="profile" value={form.profile} onChange={updateField} disabled={started}><option value="">Sin perfil</option>{data.profiles.map((profile) => <option key={profile.id} value={profile.name}>{profile.name}</option>)}</select></label>
        <label className="field">Peso de entrada (kg)<input className="input" type="number" name="input" value={form.input} onChange={updateField} min="0.01" step="0.01" placeholder="8.00" disabled={started} required /></label>
        {selectedLot && <span className="lot-available">Disponible en este lote: <b>{selectedLot.weight} kg</b></span>}
      </div>
      {!started && <button className="button button-primary start-roast-button" type="button" onClick={beginRoast}><Icon name="flame" size={18} /> Preparar registro de tueste</button>}
    </div>

    {started && <>
      <div className="roast-section live-temperature-section">
        <div className={`roast-timer ${startedAt !== null && phase === 'recording' ? 'roast-timer-active' : ''}`}><Icon name="clock" size={18} /><strong>{formatTime(elapsed)}</strong><span>{phase === 'setup' ? 'LISTO' : phase === 'finishing' ? 'DETENIDO' : startedAt === null ? 'ESPERANDO' : 'EN CURSO'}</span></div><br></br>
        <div className="live-section-heading"><span className="form-section-title"><span>2</span> Temperatura en vivo</span><span className="live-indicator"><i /> {startedAt === null ? 'Esperando primera lectura' : phase === 'recording' ? 'Captura activa' : 'Captura pausada'}</span></div>
        <TemperatureChart points={measurements} events={events} elapsed={elapsed} />
        {phase === 'recording' && <form className="temperature-entry" onSubmit={registerTemperature}>
          <label className="field">Temperatura actual (°C)<input className="input" type="number" min="0" max="300" step="0.1" value={temperature} onChange={updateTemperature} placeholder="Por ejemplo: 185.5" autoFocus /></label>
          <button className="button button-primary" type="submit" disabled={!temperature}><Icon name="plus" size={17} /> Registrar temperatura</button>
          <div className="temperature-readings"><b>{measurements.length}</b><span>lecturas</span><small>Máxima: {maximumTemperature ? `${maximumTemperature} °C` : '—'}</small></div>
        </form>}
        {measurements.length > 0 && <div className="measurement-list">{measurements.map((point, index) => <div className={`measurement-row ${point.eventType ? `measurement-row-${point.eventType}` : ''}`} key={`${point.seconds}-${index}`}><span>{point.eventType ? ROAST_EVENTS.find((event) => event.type === point.eventType)?.label : `Lectura ${index + 1}`}</span><b>{point.temperature} °C</b><small>{formatTime(point.seconds)}</small></div>)}</div>}
        {phase === 'recording' && events.some((event) => event.type === 'charge') && <div className="roast-event-panel"><h4>Hitos del tueste</h4><p>Cada botón guarda la temperatura escrita y el momento en que empezaste a anotarla.</p><div className="automatic-event-note">{events.some((event) => event.type === 'tp') ? <>Punto de inflexión detectado: <b>{formatTime(events.find((event) => event.type === 'tp').seconds)} · {events.find((event) => event.type === 'tp').temperature} °C</b></> : 'El sistema marcará el punto de inflexión cuando confirme que la temperatura comenzó a subir.'}</div><div className="roast-event-buttons">{ROAST_EVENTS.filter((event) => !['charge', 'tp', 'drop'].includes(event.type)).map((event) => {
          const markedEvent = events.find((savedEvent) => savedEvent.type === event.type);
          const disabled = !canMarkEvent(event.type) || temperature === '';
          return <button className={`roast-event-button roast-event-${event.type} ${markedEvent ? 'roast-event-marked' : ''}`} data-event={event.type} key={event.type} type="button" disabled={disabled} onClick={() => markEvent(event.type)} title={event.label}>
            <b>{markedEvent ? '✓' : '+'} {event.short}</b><span>{markedEvent ? formatTime(markedEvent.seconds) : event.label}</span>
          </button>;
        })}</div><div className="roast-event-note">El segundo crack es opcional; regístralo solo si ocurre.</div></div>}
        {events.length > 0 && <div className="saved-event-list">{events.map((event) => <span className={`saved-event-chip saved-event-chip-${event.type}`} key={event.type}><b>{event.short || event.type.toUpperCase()}</b> {formatTime(event.seconds)}{event.temperature !== null ? ` · ${event.temperature} °C` : ''}</span>)}</div>}
        {error && phase === 'recording' && <div className="error-note" role="alert">{error}</div>}
      </div>

      {phase === 'recording' && <div className="roast-actions"><button className="button button-outline" type="button" onClick={discardRoast}>Descartar tueste</button><div className="drop-action"><button className="button button-drop" type="button" disabled={!canMarkEvent('drop') || temperature === '' || measurements.length < 2} onClick={() => markEvent('drop')}>Registrar caída y detener</button><span className="drop-help">Escribe la temperatura de descarga antes de registrar la caída.</span></div></div>}

      {phase === 'finishing' && <>
        <div className="roast-section">
          <span className="form-section-title"><span>3</span> Resultado final</span>
          <div className="form-grid">
            <label className="field">Peso final del café (kg)<input className="input" type="number" name="output" value={form.output} onChange={updateField} min="0.01" max={form.input || undefined} step="0.01" placeholder="6.72" required /></label>
            <label className="field field-wide">Observaciones<textarea className="input textarea" name="observations" value={form.observations} onChange={updateField} rows="3" maxLength="500" placeholder="Aroma, color, notas del proceso…" /></label>
          </div>
          <div className="roast-finish-summary"><span>Duración <b>{(elapsed / 60).toFixed(1)} min</b></span><span>Lecturas <b>{measurements.length}</b></span><span>Temperatura máxima <b>{maximumTemperature || '—'}{maximumTemperature ? ' °C' : ''}</b></span></div>
        </div>
        {error && <div className="error-note" role="alert">{error}</div>}
        <div className="roast-actions"><button className="button button-outline" type="button" onClick={discardRoast}>Descartar tueste</button><button className="button button-primary" type="button" onClick={finishRoast}><Icon name="check" size={17} /> Confirmar guardado</button></div>
      </>}
    </>}

    {!started && error && <div className="error-note" role="alert">{error}</div>}
  </section>;
}
