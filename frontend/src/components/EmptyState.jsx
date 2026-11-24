export default function EmptyState({ title, detail }) {
  return <div className="empty-state">
    <span>☕</span>
    <b>{title}</b>
    <small>{detail}</small>
  </div>;
}
