import { useEffect, useState } from 'react';
import { authService } from '../services/api.js';
import Icon from '../components/Icon.jsx';
import '../styles/auth.css';

export default function AuthAction({ mode, token }) {
  const verifying = mode === 'verify-email';
  const [busy, setBusy] = useState(false);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const [password, setPassword] = useState('');
  const [confirmation, setConfirmation] = useState('');

  useEffect(() => {
    // Conservamos el token en memoria, pero lo quitamos del historial del navegador.
    // Un escáner de enlaces puede abrir la página; no debe activar la cuenta.
    if (token) window.history.replaceState(null, '', window.location.pathname);
  }, [token]);

  async function confirmEmail() {
    setError('');
    setMessage('');
    if (!token) return setError('El enlace no contiene un token de verificación.');
    setBusy(true);
    try {
      const result = await authService.verifyEmail(token);
      setMessage(result.message);
    } catch (failure) {
      setError(failure.message);
    } finally {
      setBusy(false);
    }
  }

  async function submit(event) {
    event.preventDefault();
    setError('');
    setMessage('');
    if (!token) return setError('El enlace no contiene un token para restablecer la contraseña.');
    if (!/^(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z0-9]).{8,}$/.test(password)) {
      return setError('Usa al menos 8 caracteres, una mayúscula, un número y un símbolo.');
    }
    if (password !== confirmation) return setError('Las contraseñas no coinciden.');
    setBusy(true);
    try {
      const result = await authService.resetPassword(token, password);
      setMessage(result.message);
    } catch (failure) {
      setError(failure.message);
    } finally {
      setBusy(false);
    }
  }

  return <div className="auth-layout auth-action-layout">
    <section className="auth-panel"><div className="auth-card">
      <div className="brand brand-mobile"><span className="brand-icon"><Icon name="bean" size={26} /></span><span>Roast<span className="brand-highlight">Lab</span></span></div>
      <span className="eyebrow">CUENTA ROASTLAB</span>
      <h2>{verifying ? 'Verificar correo' : 'Crear contraseña nueva'}</h2>
      {verifying ? <>
        <p className="muted">Confirma tu dirección de correo para activar tu cuenta de RoastLab.</p>
        {message && <div className="success-note" role="status">{message}</div>}
        {error && <div className="error-note" role="alert">{error}</div>}
        {!message && <button type="button" className="button button-primary button-full" disabled={busy || !token} onClick={confirmEmail}>
          {busy ? 'Verificando…' : 'Confirmar mi correo'}
        </button>}
      </> : <>
        <p className="muted">El enlace de recuperación vence a los 30 minutos y solo puede usarse una vez.</p>
        {message && <div className="success-note" role="status">{message}</div>}
        {error && <div className="error-note" role="alert">{error}</div>}
        {!message && <form className="auth-form" onSubmit={submit}>
          <label className="field">Nueva contraseña<input className="input" type="password" autoComplete="new-password" value={password} onChange={event => setPassword(event.target.value)} required /></label>
          <label className="field">Confirmar contraseña<input className="input" type="password" autoComplete="new-password" value={confirmation} onChange={event => setConfirmation(event.target.value)} required /></label>
          <button className="button button-primary button-full" disabled={busy}>{busy ? 'Guardando…' : 'Guardar contraseña'}</button>
        </form>}
      </>}
      <a className="auth-link" href="/">Volver a iniciar sesión</a>
    </div></section>
  </div>;
}
