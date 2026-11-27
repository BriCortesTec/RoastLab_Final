import { useEffect, useRef } from 'react';

export default function ConfirmDialog({
  isOpen = true, title, message, confirmText = 'Confirmar', cancelText = 'Cancelar',
  variant = 'primary', loading = false, onCancel, onConfirm,
}) {
  const ref = useRef(null);
  useEffect(() => {
    if (!isOpen) return;
    const dialog = ref.current;
    dialog.showModal();
    return () => dialog.close();
  }, [isOpen]);
  if (!isOpen) return null;
  return <dialog className="confirm-dialog" ref={ref} aria-label={title}
    onCancel={event => { event.preventDefault(); if (!loading) onCancel(); }}>
    <h3>{title}</h3>
    {message && <p>{message}</p>}
    <div className="modal-actions">
      <button type="button" disabled={loading} autoFocus className="button button-outline" onClick={onCancel}>{cancelText}</button>
      <button type="button" disabled={loading} className={`button button-primary ${variant === 'danger' ? 'btn-danger' : ''}`} onClick={onConfirm}>
        {loading ? 'Procesando…' : confirmText}
      </button>
    </div>
  </dialog>;
}
