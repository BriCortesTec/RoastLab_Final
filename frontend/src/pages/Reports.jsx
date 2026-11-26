import HistoryReports from './HistoryReports.jsx';

export default function Reports({ data, notify }) {
  return <HistoryReports data={data} page="reports" notify={notify} />;
}
