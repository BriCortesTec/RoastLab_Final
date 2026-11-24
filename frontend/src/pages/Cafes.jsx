import CatalogPage from '../components/CatalogPage.jsx';

export default function Cafes({ items, onSave, notify }) {
  return <CatalogPage kind="cafes" items={items} onSave={onSave} notify={notify} />;
}
