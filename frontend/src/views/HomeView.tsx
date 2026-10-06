import { useHomeViewModel } from '../viewmodels/useHomeViewModel.ts';
import FlightTable from './components/FlightTable.tsx';
import FlightGlobe from './components/FlightGlobe.tsx';

export default function HomeView() {
  const { flights, loading, error } = useHomeViewModel();

  return (
    <>
      <header>
        <a className="brand" href="#">
          Airline Simulation
        </a>
      </header>
      <main>
        <section className="panel timetable">
          <div className="panel-heading">
            <h2>
              Flight timetable <span className="count">{flights.length}</span>
            </h2>
            <span>All times in UTC</span>
          </div>
          <p id="message" role="status">
            {error}
          </p>
          <FlightTable
            flights={flights}
            emptyMessage={
              loading
                ? 'Loading flights…'
                : error
                  ? 'Flight data unavailable.'
                  : 'No flights scheduled.'
            }
          />
        </section>
        <FlightGlobe flights={flights} />
      </main>
    </>
  );
}
