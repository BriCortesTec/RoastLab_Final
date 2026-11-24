import { useState } from 'react';
import Icon from '../components/Icon.jsx';
import '../styles/roasts.css';
export default function RoastForm({ data, onSave }) {
  const [error, setError] = useState('');

  const submit = (e) => {
    e.preventDefault();
    setError('');
    const f = new FormData(e.currentTarget);
    const input = Number(f.get('input'));
    const output = Number(f.get('output'));
    const duration = Number(f.get('duration'));
    const maxTemp = Number(f.get('maxTemp'));
    const lotCode = String(f.get('lot')).split(' · ')[0];
    const selectedLot = data.lots.find((lot) => lot.code === lotCode);

    if (!selectedLot) {
      return setError('Selecciona un lote de café.');
    }
    if (input > Number(selectedLot.weight)) {
      return setError(`Este lote solo tiene ${selectedLot.weight} kg disponibles.`);
    }
    if (output > input) {
      return setError('El peso final no puede ser mayor al peso de entrada.');
    }
    if (input <= 0 || duration <= 0 || maxTemp <= 0) {
      return setError('Revisa que peso, duración y temperatura sean mayores a cero.');
    }

    onSave({
      id: Date.now(),
      name: String(f.get('name')).trim(),
      date: f.get('date'),
      lot: f.get('lot'),
      lotCode,
      profile: f.get('profile'),
      duration,
      input,
      output,
      maxTemp,
      status: 'Completado'
    });
  };

  return (
    <form className="panel roast-form" onSubmit={submit}>
      <div className="panel-head">
        <div>
          <h3>Nuevo registro de tueste</h3>
          <p>Captura los datos de la tanda que acabas de tostar.</p>
        </div>
        <span className="form-step">
          01 <i /> 02
        </span>
      </div>

      <div className="roast-section">
        <span className="form-section-title">
          <span>1</span> Información general
        </span>
        <div className="form-grid">
          <label className="field field-wide">
            Nombre del tueste
            <input
              className="input"
              name="name"
              placeholder="Ej. Tueste de la mañana #025"
              required
              minLength={3}
              maxLength={100}
            />
          </label>

          <label className="field">
            Fecha del tueste
            <input
              className="input"
              type="date"
              name="date"
              defaultValue={new Date().toISOString().slice(0, 10)}
              required
            />
          </label>

          <label className="field">
            Lote de café
            <select className="input" name="lot" required defaultValue="">
              <option value="" disabled>
                Selecciona un lote…
              </option>
              {data.lots.map((l) => (
                <option key={l.id} value={`${l.code} · ${l.coffee}`}>
                  {l.code} · {l.coffee} ({l.weight} kg)
                </option>
              ))}
            </select>
          </label>

          <label className="field field-wide">
            Perfil de tueste
            <select className="input" name="profile" defaultValue="">
              <option value="">Sin perfil</option>
              {data.profiles.map((p) => (
                <option key={p.id}>{p.name}</option>
              ))}
            </select>
          </label>
        </div>
      </div>

      <div className="roast-section">
        <span className="form-section-title">
          <span>2</span> Resultado del tueste
        </span>
        <div className="form-grid">
          <label className="field">
            Peso de entrada (kg)
            <input
              className="input"
              name="input"
              type="number"
              min="0.01"
              step="0.01"
              placeholder="8.00"
              required
            />
          </label>

          <label className="field">
            Peso de salida (kg)
            <input
              className="input"
              name="output"
              type="number"
              min="0.01"
              step="0.01"
              placeholder="6.72"
              required
            />
          </label>

          <label className="field">
            Duración (minutos)
            <input
              className="input"
              name="duration"
              type="number"
              min="0.1"
              step="0.1"
              placeholder="11.2"
              required
            />
          </label>

          <label className="field">
            Temperatura máxima (°C)
            <input
              className="input"
              name="maxTemp"
              type="number"
              min="1"
              step="0.1"
              placeholder="205"
              required
            />
          </label>

          <label className="field field-wide">
            Observaciones
            <textarea
              className="input textarea"
              name="observations"
              rows="3"
              maxLength="500"
              placeholder="Aroma, color, notas del proceso…"
            />
          </label>
        </div>
      </div>

      {error && <div className="error-note">{error}</div>}

      <div className="modal-actions">
        <button className="button button-primary">
          <Icon name="flame" size={17} /> Guardar registro
        </button>
      </div>
    </form>
  );
}