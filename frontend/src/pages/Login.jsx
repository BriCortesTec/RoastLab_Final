import { useState } from 'react';
import Icon from '../components/Icon.jsx';
import { authService } from '../services/api.js';
import '../styles/auth.css';

export default function Login({ onLogin }) {
  const [error, setError] = useState('');
  const [fields, setFields] = useState({});
  const [busy, setBusy] = useState(false);
  const [recover, setRecover] = useState(false);
  const [notice, setNotice] = useState('');
  const [canResend, setCanResend] = useState(false);
  const [form, setForm] = useState({email: '', password: ''});

  async function submit(event) {
    event.preventDefault();
    if (busy) return;
    const invalid = {};
    if (!event.currentTarget.elements.email.validity.valid) invalid.email = 'Escribe un correo válido.';
    if (!recover && !form.password) invalid.password = 'Escribe tu contraseña.';
    setFields(invalid);
    setError('');
    setNotice('');
    if (Object.keys(invalid).length) return;
    setBusy(true);
    try {
      if (recover) {
        const response = await authService.forgotPassword(form.email.trim());
        setNotice(response.message);
        return;
      }
      const {token, user} = await authService.login({...form, email: form.email.trim()});
      localStorage.setItem('roastlab_token', token);
      onLogin(user);
    } catch (failure) {
      setFields(failure.fieldErrors || {});
      setError(failure.message);
      setCanResend(failure.message.includes('Verifica tu correo'));
    } finally {
      setBusy(false);
    }
  }

  async function resendVerification() {
    setBusy(true);
    setError('');
    setNotice('');
    try {
      const response = await authService.resendVerification(form.email.trim());
      setNotice(response.message);
      setCanResend(false);
    } catch (failure) {
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
          <p className="muted">{recover ? 'Te enviaremos un enlace si el correo está registrado.' : 'Entra con la cuenta que te asignó tu equipo.'}</p>

          {notice && <div className="success-note" role="status">{notice}</div>}
          {error && (
            <div className="error-note" role="alert">
              {error}
            </div>
          )}
          {canResend && <button type="button" className="auth-link" disabled={busy} onClick={resendVerification}>Reenviar verificación de correo</button>}

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

            {!recover && <label className="field">
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
            </label>}

            <button
              className="button button-primary button-full"
              disabled={busy}
            >
              {busy ? (recover ? 'Enviando…' : 'Entrando…') : (recover ? 'Enviar enlace' : 'Iniciar sesión')}
              <Icon name="arrow" size={18} />
            </button>
          </form>
          <button type="button" className="auth-link" onClick={() => {setRecover(value => !value); setError(''); setNotice(''); setFields({}); setCanResend(false);}}>
            {recover ? 'Volver a iniciar sesión' : '¿Olvidaste tu contraseña?'}
          </button>

        </div>
      </section>
    </div>
  );
}
