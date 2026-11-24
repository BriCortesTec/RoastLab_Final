import CatalogPage from '../components/CatalogPage.jsx';

export default function Lots({ items, cafes, onSave, notify }) {
  return <CatalogPage kind="lots" items={items} relatedCafes={cafes} onSave={onSave} notify={notify} />;
}
