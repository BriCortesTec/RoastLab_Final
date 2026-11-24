import CatalogPage from '../components/CatalogPage.jsx';

export default function Profiles({ items, onSave, notify }) {
  return <CatalogPage kind="profiles" items={items} onSave={onSave} notify={notify} />;
}
