package data;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GuideProgram extends Program {
	
	public static List<GuideProgram> get_guideprogram_list(int chanid, ZonedDateTime start, ZonedDateTime end) throws IOException {
		
		List<GuideProgram> guides = new ArrayList<GuideProgram>();
		String url = Source.get_base_url() + "/Guide/GetProgramList?Details=true&ChanId=" + chanid
				+ "&StartTime=" + start.toString() + "&EndTime=" + end.toString();
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
				guides.add(new GuideProgram(programs.getJSONObject(i)));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return guides;
	}
	
	public static LocalDate get_earliest_program() throws IOException {
		return get_edge_program(true);
	}
	
	public static LocalDate get_latest_program() throws IOException {
		return get_edge_program(false);
	}
	
	private static LocalDate get_edge_program(boolean earliest) throws IOException {
		String url = Source.get_base_url() + "/Guide/GetProgramList?Details=true&Count=1&Descending=" + (earliest ? "false" : "true");
		String result = Source.http_get(url);
		LocalDate date = null;
		
		try {
			JSONObject obj = new JSONObject(result);
			JSONObject list = obj.getJSONObject("ProgramList");
			
			String proto_version = list.getString("ProtoVer");
			// String backend_version = list.getString("Version");
			
			if (!proto_version.equals(Source.get_version()))
				throw new IOException("Proto version mismatch (" + proto_version + " vs. " + Source.get_version() + ").");
			
			JSONArray programs = list.getJSONArray("Programs");
			GuideProgram program = new GuideProgram(programs.getJSONObject(0));
			date = program.get_starttime().atZone(ZoneOffset.UTC).withZoneSameInstant(ZoneId.systemDefault()).toLocalDate();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return date;
	}
	
	@Override
	protected void refresh() throws IOException {
		String url = Source.get_base_url() + "/Guide/GetProgramDetails?ChanId=" 
				+ get_channel().get_chanid() + "&StartTime=" + get_starttime().toString();
		String result = Source.http_get(url);
		
		try {
			JSONObject guide_program = new JSONObject(result);
			updateProgram(this, guide_program);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private GuideProgram(JSONObject guide_program) throws JSONException {
		super(guide_program);
	}
}
