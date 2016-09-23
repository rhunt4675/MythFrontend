package data;

import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.List;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Upcoming extends Program {
	
	private Rule _rule;
	
	public static List<Upcoming> get_upcoming() throws IOException {
		List<Upcoming> upcomings = new ArrayList<Upcoming>();
		String url = "/Dvr/GetUpcomingList?ShowAll=true";
		String result = Source.http_get(url);
		
		try {
			JSONObject obj = new JSONObject(result);
			JSONObject list = obj.getJSONObject("ProgramList");
			
			String proto_version = list.getString("ProtoVer");
			// String backend_version = list.getString("Version");
			
			if (!proto_version.equals(Source.get_version()))
				throw new IOException("Proto version mismatch (" + proto_version + " vs. " + Source.get_version() + ").");
			
			JSONArray programs = list.getJSONArray("Programs");
			for (int i = 0; i < programs.length(); i++) {
				upcomings.add(new Upcoming(programs.getJSONObject(i)));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return upcomings;
	}
	
	@Override
	protected void refresh() throws IOException {
		String url = "/Dvr/GetUpcomingList?ShowAll=true&RecordId=" + get_recordid();
		String result = Source.http_get(url);
		
		try {
			JSONObject obj = new JSONObject(result);
			JSONObject list = obj.getJSONObject("ProgramList");
			
			String proto_version = list.getString("ProtoVer");
			// String backend_version = list.getString("Version");
			
			if (!proto_version.equals(Source.get_version()))
				throw new IOException("Proto version mismatch (" + proto_version + " vs. " + Source.get_version() + ").");
			
			JSONArray programs = list.getJSONArray("Programs");
			JSONObject matchingschedule = null;
			
			for (int i = 0; i < programs.length(); i++) {
				JSONObject program = programs.getJSONObject(i);
				LocalDateTime starttime = LocalDateTime.parse(program.getString("StartTime").replaceFirst(".$", ""));
				int chanid = program.getJSONObject("Channel").getInt("ChanId");
				
				if (get_starttime().equals(starttime) && get_channel().get_chanid() == chanid) {
					matchingschedule = program; break;
				}
			}
			
			if (matchingschedule == null)
				throw new IOException("Could not find match for RecordId=" + get_recordid() + ", ChanId=" 
						+ get_channel().get_chanid() + ", StartTime=" + get_starttime() + " during refresh.");
			
			updateProgram(this, matchingschedule);
			_rule = Rule.get_rule(get_recordid());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void enable() throws IOException {
		remove_overriding_rule();
	}
	
	public void disable() throws IOException {
		add_overriding_rule(Rule.RecordingType.DontRecord);
	}
	
	public void add_override() throws IOException {
		add_overriding_rule(Rule.RecordingType.OverrideRecord);
	}
	
	public void remove_override() throws IOException {
		remove_overriding_rule();
	}
	
	private void add_overriding_rule(Rule.RecordingType type) throws IOException {
		String add = "/Dvr/AddRecordSchedule?Type=" + URLEncoder.encode(type.getText(), "UTF-8") + "&ParentId=" + get_recordid()
				+ "&Title=" + URLEncoder.encode(get_title(), "UTF-8") + "&StartTime=" + get_starttime() + "&EndTime=" + get_endtime() 
				+ "&ChanId=" + get_channel().get_chanid() + "&Station=" + get_channel().get_callsign() + "&FindDay=0&FindTime=00:00:00Z"
				+ "&Subtitle=" + URLEncoder.encode(get_subtitle(), "UTF-8") + "&Description=" + URLEncoder.encode(get_description(), "UTF-8") 
				+ "&Category=" + URLEncoder.encode(get_category(), "UTF-8") + "&SeriesId=" + get_seriesid() + "&ProgramId=" + get_programid();
		String check = "/Dvr/GetRecordSchedule?RecordId=";
		String result = Source.http_post(add);
		
		try {
			set_recordid((new JSONObject(result)).getInt("uint"));			
			
			while (true) {
				try {
					Thread.sleep(100);
					result = Source.http_get(check + get_recordid());
					break;
				} catch (IOException e) {
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			refresh();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void remove_overriding_rule() throws IOException {
		String del = "/Dvr/RemoveRecordSchedule?RecordId=" + get_recordid();
		String check = "/Dvr/GetRecordSchedule?ChanId=" + get_channel().get_chanid() + 
				"&StartTime=" + get_starttime() + "&RecordId=" + _rule.get_parentid();
		Source.http_post(del);
		
		while (true) {
			try {
				Thread.sleep(100);
				Source.http_get(check);
				break;
			} catch (IOException e) {
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		
		set_recordid(_rule.get_parentid());
		refresh();
	}
	
	public Rule get_rule() {
		return _rule;
	}
	
	private Upcoming(JSONObject upcoming_json) throws JSONException, IOException {
		super(upcoming_json);
		
		_rule = Rule.get_rule(get_recordid());
	}
}