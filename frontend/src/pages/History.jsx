import HistoryReports from './HistoryReports.jsx';

export default function History({ data, onSaveProfile }) {
  return <HistoryReports data={data} page="history" onSaveProfile={onSaveProfile} />;
}
