import { useEffect, useState } from 'react';
import { authService, resourceService } from '../services/api.js';
import { ROLE_NAMES } from '../data/navigation.js';
import ConfirmDialog from '../components/ConfirmDialog.jsx';
import '../styles/catalogs.css';

const emptyAccount = {name: '', surname: '', email: '', phone: '', password: '', role: 'tostador', active: true};
const fields = [
  ['name', 'Nombre', 'text', 80], ['surname', 'Apellido', 'text', 80],
  ['email', 'Correo electrónico', 'email', 120], ['phone', 'Teléfono', 'tel', 16],
  ['password', 'Contraseña', 'password'],
];

export default function Accounts({ personal, user, onProfile, notify }) {
  const admin = user.role === 'admin';
  const list = admin && !personal;
  const [form, setForm] = useState(personal ? {...user, phone: user.phone || ''} : emptyAccount);
  const [errors, setErrors] = useState({});
  const [failure, setFailure] = useState('');
  const [busy, setBusy] = useState(false);
  const [result, setResult] = useState(null);
  const [listError, setListError] = useState('');
  const [search, setSearch] = useState('');
  const [page, setPage] = useState(0);
  const [revision, setRevision] = useState(0);
  const [removing, setRemoving] = useState(null);

  useEffect(() => {
    if (!list) return;
    const controller = new AbortController();
    setResult(null);
    setListError('');
    resourceService.list('usuarios', {search, page, signal: controller.signal})
      .then(data => { if (!controller.signal.aborted) setResult(data); })
      .catch(error => { if (!controller.signal.aborted) setListError(error.message); });
    return () => controller.abort();
  }, [list, search, page, revision]);

  function edit(account) {
    setForm({...emptyAccount, ...account, phone: account?.phone || '', password: ''});
    setErrors({});
    setFailure('');
  }

  async function save(event) {
    event.preventDefault();
    if (busy) return;
    setBusy(true);
    setFailure('');
    setErrors({});
    try {
      const details = {name: form.name.trim(), surname: form.surname.trim(), phone: form.phone.trim() || null};
      if (personal) {
        onProfile(await authService.updateProfile(details));
      } else {
        const payload = {...details, role: admin ? form.role : 'tostador', password: form.password || null};
        if (form.id) {
          const saved = await resourceService.update('usuarios', form.id, {...payload, active: form.active});
          if (saved.id === user.id) onProfile(saved);
        } else {
          await resourceService.create('usuarios', {...payload, email: form.email.trim()});
        }
        edit(null);
        setRevision(value => value + 1);
      }
      notify(personal ? 'Perfil actualizado.' : form.id ? 'Usuario actualizado.' : 'Cuenta creada.');
    } catch (error) {
      setErrors(error.fieldErrors || {});
      if (!Object.keys(error.fieldErrors || {}).length) setFailure(error.message);
    } finally { setBusy(false); }
  }

  async function deactivate() {
    const account = removing;
    setRemoving(null);
    try {
      await resourceService.remove('usuarios', account.id);
      if (account.id === form.id) edit(null);
      setRevision(value => value + 1);
      notify('Cuenta desactivada.');
    } catch (error) { setFailure(error.message); }
  }

  return <section className="panel data-panel">
    <h3>{personal ? 'Datos de mi cuenta' : form.id ? 'Editar usuario' : admin ? 'Crear usuario' : 'Crear tostador'}</h3>
    <form className="modal-fields" noValidate onSubmit={save}>
      {fields.filter(([key]) => !personal || key !== 'password').map(([key, label, type, max]) =>
        <label className="field" key={key}>
          {label}{key === 'password' && form.id ? ' nueva (opcional)' : ''}
          <input className="input" type={type} maxLength={max} autoComplete={key === 'password' ? 'new-password' : undefined}
            readOnly={key === 'email' && (personal || Boolean(form.id))}
            value={form[key] || ''} aria-invalid={Boolean(errors[key])}
            onChange={e => setForm({...form, [key]: e.target.value})}/>
          {errors[key] && <small role="alert">{errors[key]}</small>}
        </label>)}
      <label className="field">Rol
        <select className="input" value={form.role} disabled={personal || !admin}
          onChange={event => setForm({...form, role: event.target.value})}>
          {Object.entries(ROLE_NAMES).filter(([role]) => admin || personal || role === 'tostador').map(([role, label]) =>
            <option key={role} value={role}>{label}</option>)}
        </select>
        {errors.role && <small role="alert">{errors.role}</small>}
      </label>
      {!personal && form.id && <label><input type="checkbox" checked={form.active}
        onChange={e => setForm({...form, active: e.target.checked})}/> Cuenta activa</label>}
      {failure && <p role="alert" className="error-note">{failure}</p>}
      <div className="modal-actions field-wide">
        {!personal && form.id && <button type="button" className="button button-outline" onClick={() => edit(null)}>Cancelar</button>}
        <button disabled={busy} className="button button-primary">{busy ? 'Guardando…' : personal || form.id ? 'Guardar cambios' : 'Crear cuenta'}</button>
      </div>
    </form>
    {list && <>
      <label className="field">Buscar usuarios<input className="input" value={search} onChange={e => { setSearch(e.target.value); setPage(0); }}/></label>
      {listError && <p role="alert">{listError}</p>}
      {!result && !listError && <p role="status">Cargando usuarios…</p>}
      {result && <><div className="table-scroll"><table><thead><tr>
        {['Nombre', 'Correo', 'Rol', 'Estado', 'Acciones'].map(title => <th key={title}>{title}</th>)}
      </tr></thead><tbody>{result.content.map(row => <tr key={row.id}>
        <td>{row.name} {row.surname}</td><td>{row.email}</td><td>{ROLE_NAMES[row.role]}</td><td>{row.active ? 'Activa' : 'Inactiva'}</td>
        <td><div className="row-actions"><button onClick={() => edit(row)}>Editar</button>
          {row.active && row.id !== user.id && <button onClick={() => setRemoving(row)}>Desactivar</button>}</div></td>
      </tr>)}</tbody></table></div>
      {!result.content.length && <p>Sin resultados.</p>}
      <div className="table-footer"><button disabled={page === 0} onClick={() => setPage(page - 1)}>Anterior</button>
        Página {page + 1} de {Math.max(1,result.totalPages)}
        <button disabled={page + 1 >= result.totalPages} onClick={() => setPage(page + 1)}>Siguiente</button></div></>}
    </>}
    {removing && <ConfirmDialog title={`¿Desactivar la cuenta de ${removing.email}?`} confirmText="Desactivar" onCancel={() => setRemoving(null)} onConfirm={deactivate}/>}
  </section>;
}
