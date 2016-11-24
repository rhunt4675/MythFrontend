package data;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class UpcomingList {
	private List<Upcoming> _upcomings;
	private LocalDate _date;
	private int _active = 0, _inactive = 0, _conflicted = 0, _errored = 0;
	
	public static List<UpcomingList> get_upcoming_by_day() throws IOException {
		// Get all upcoming recordings
		List<Upcoming> allUpcomings = Upcoming.get_upcoming();
		List<UpcomingList> result = new ArrayList<UpcomingList>();
		Map<LocalDate, List<Upcoming>> buckets = new TreeMap<LocalDate, List<Upcoming>>();
		
		// Distribute (O(n))
		for (Upcoming u : allUpcomings) {
			LocalDate date = u.get_starttime().withZoneSameInstant(ZoneId.systemDefault()).toLocalDate();
			
			if (!buckets.containsKey(date))
				buckets.put(date, new ArrayList<Upcoming>());
			buckets.get(date).add(u);
		}
		
		// Generate UpcomingList objects
		for (LocalDate d : buckets.keySet())
			result.add(new UpcomingList(d, buckets.get(d)));
		return result;
	}
	
	private UpcomingList(LocalDate date, List<Upcoming> upcomings) {
		_upcomings = upcomings;
		_date = date;
		
		// Compute Daily Statistics
		for (Upcoming u : upcomings) {
			switch(u.get_status()) {
				case WILLRECORD:
				case RECORDING:
				case RECORDED:
				case TUNING:
				case PENDING:
					_active++; break;
				case ABORTED:
				case MISSED:
				case CANCELLED:
				case LOWDISKSPACE:
				case TUNERBUSY:
				case FAILED:
				case MISSEDFUTURE:
				case OTHERTUNING:
				case OTHERRECORDING:
				case FAILING:
					_errored++; break;
				case CONFLICT:
					_conflicted++; break;
				default:
					_inactive++; break;				
			}
		}
	}
	
	public LocalDate get_date() {
		return _date;
	}
	
	public List<Upcoming> get_upcoming() {
		return _upcomings;
	}
	
	public int get_active() {
		return _active;
	}
	
	public int get_errored() {
		return _errored;
	}
	
	public int get_conflicted() {
		return _conflicted;
	}
	
	public int get_inactive() {
		return _inactive;
	}
}