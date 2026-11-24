import '../styles/layout.css';
export default function Toast({ message }) {
  if (!message) return null;
  return <div className="toast-message" role="status">✓ &nbsp;{message}</div>;
}
