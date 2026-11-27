import { useState } from 'react';
import Icon from '../components/Icon.jsx';
import { authService } from '../services/api.js';
import '../styles/auth.css';

export default function Login({ onLogin }) {
  const [error, setError] = useState('');
  const [fields, setFields] = useState({});
  const [busy, setBusy] = useState(false);
  const [form, setForm] = useState({email: '', password: ''});

  async function submit(event) {
    event.preventDefault();
    if (busy) return;
    const invalid = {};
    if (!event.currentTarget.elements.email.validity.valid) invalid.email = 'Escribe un correo válido.';
    if (!form.password) invalid.password = 'Escribe tu contraseña.';
    setFields(invalid);
    setError('');
    if (Object.keys(invalid).length) return;
    setBusy(true);
    try {
      const {token, user} = await authService.login({...form, email: form.email.trim()});
      localStorage.setItem('roastlab_token', token);
      onLogin(user);
    } catch (failure) {
      setFields(failure.fieldErrors || {});
      setError(failure.message);
    } finally {
      setBusy(false);
    }
  }

  return (
    <div className="auth-layout">
      <section className="auth-photo">
        <div className="photo-shade" />
        <div className="brand brand-light">
          <span className="brand-icon">
            <Icon name="bean" size={28} />
          </span>
          <span>
            Roast<span className="brand-highlight">Lab</span>
          </span>
        </div>

        <div className="photo-caption">
          <span className="eyebrow">DEL GRANO A LA TAZA</span>
          <h1>
            Tu arte.
            <br />
            Tueste a tu manera.
          </h1>
          <p>El espacio para crear, perfeccionar y compartir cada tueste.</p>
        </div>

        <span className="photo-credit">
          Cultivado con intención. Tostado con pasión.
        </span>
      </section>

      <section className="auth-panel">
        <div className="auth-card">
          <div className="brand brand-mobile">
            <span className="brand-icon">
              <Icon name="bean" size={26} />
            </span>
            <span>
              Roast<span className="brand-highlight">Lab</span>
            </span>
          </div>

          <span className="eyebrow">BIENVENIDA A ROASTLAB</span>
          <h2>Iniciar sesión</h2>
          <p className="muted">Entra con la cuenta que te asignó tu equipo.</p>

          {error && (
            <div className="error-note" role="alert">
              {error}
            </div>
          )}

          <form className="auth-form" onSubmit={submit} noValidate>
            <label className="field">
              Correo electrónico
              <input
                className="input"
                type="email" name="email"
                autoComplete="email"
                placeholder="tu@cafeteria.com"
                value={form.email}
                onChange={(e) => setForm({ ...form, email: e.target.value })}
                required
              />
              {fields.email && <small role="alert">{fields.email}</small>}
            </label>

            <label className="field">
              Contraseña
              <input
                className="input"
                type="password"
                autoComplete="current-password"
                placeholder="Tu contraseña"
                value={form.password}
                onChange={(e) => setForm({ ...form, password: e.target.value })}
                required
              />
              {fields.password && <small role="alert">{fields.password}</small>}
            </label>

            <button
              className="button button-primary button-full"
              disabled={busy}
            >
              {busy ? 'Entrando…' : 'Iniciar sesión'}
              <Icon name="arrow" size={18} />
            </button>
          </form>

        </div>
      </section>
    </div>
  );
}
