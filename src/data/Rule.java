package data;

import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Rule {
	
	public static List<Rule> get_rules() throws IOException {
		List<Rule> rules = new ArrayList<Rule>();
		String url = "/Dvr/GetRecordScheduleList";
		String result = Source.http_get(url);
		
		try {
			JSONObject obj = new JSONObject(result);
			JSONObject list = obj.getJSONObject("RecRuleList");
			
			String proto_version = list.getString("ProtoVer");
			// String backend_version = list.getString("Version");
			
			if (!proto_version.equals(Source.get_version()))
				throw new IOException("Proto version mismatch (" + proto_version + " vs. " + Source.get_version() + ").");
			
			JSONArray recrules = list.getJSONArray("RecRules");
			for (int i = 0; i < recrules.length(); i++) {
				rules.add(new Rule(recrules.getJSONObject(i)));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return rules;
	}
	
	public static Rule get_rule(int recordid) throws IOException {
		Rule rule = null;
		String url = "/Dvr/GetRecordSchedule?RecordId=" + recordid;
		String result = Source.http_get(url);
		
		try {
			JSONObject obj = new JSONObject(result);
			JSONObject list = obj.getJSONObject("RecRule");
			
			rule = new Rule(list);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return rule;
	}
	
	public static Rule get_default() throws IOException {
		Rule rule = null;
		String url = "/Dvr/GetRecordSchedule?Template=Default";
		String result = Source.http_get(url);
		
		try {
			JSONObject obj = new JSONObject(result);
			JSONObject list = obj.getJSONObject("RecRule");
			
			rule = new Rule(list);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		rule._id = -1;
		return rule;
	}
	
	public void load_from_guideprogram(GuideProgram program) {
		_starttime = program.get_starttime();
		_endtime = program.get_endtime();
		_title = program.get_title();
		_channel = program.get_channel();
		
		_findday = program.get_starttime().getDayOfWeek().getValue() % 7;
		_findtime = program.get_starttime().toLocalTime();
	}
	
	public void commit() throws IOException {
		String url = "/Dvr/"
			+ (_id == -1 
				? "AddRecordSchedule?ChanId=" + _channel.get_chanid() + "&Station=" 
					+ URLEncoder.encode(_channel.get_callsign(), "utf-8") + "&" 
				: "UpdateRecordSchedule?RecordId=" + _id + "&")
			+ "ParentId=" + _parentid + "&StartOffset=" + _startoffset + "&EndOffset=" + _endoffset
			+ "&Inactive=" + (_inactive ? "true" : "false")
			+ "&AutoCommflag=" + (_commflag ? "true" : "false") 
			+ "&AutoTranscode=" + (_transcode ? "true" : "false")
			+ "&FindDay=" + _findday + "&FindTime=" + _findtime.toString()
			+ "&StartTime=" + _starttime.toString() + "&EndTime=" + _endtime.toString()
			+ "&Title=" + URLEncoder.encode(_title, "utf-8")
			+ "&Type=" + URLEncoder.encode(_type.getText(), "utf-8")
			+ "&DupMethod=" + URLEncoder.encode(_dupmethod.getText(), "utf-8")
			+ "&DupIn=" + URLEncoder.encode(_dupin.getText(), "utf-8")
			+ "&MaxEpisodes=" + _maxepisodes;
		String check = "/Dvr/GetRecordSchedule?RecordId=";
		String result = Source.http_post(url);

		try {
			if (_id == -1) _id = ((new JSONObject(result)).getInt("uint"));			
			
			while (true) {
				try {
					Thread.sleep(100);
					result = Source.http_get(check + _id);
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
	
	public void enable(boolean flag) throws IOException {
		String url = "/Dvr/" + (flag ? "Enable" : "Disable") + "RecordSchedule?RecordId=" + _id;
		Source.http_post(url);
		
		refresh();
	}
	
	public void toggle_active() throws IOException {
		enable(this._inactive);
	}
	
	public boolean is_override() {
		return _type == RecordingType.OverrideRecord;
	}
	
	public boolean is_disabled() {
		return _type == RecordingType.DontRecord;
	}
	
	public void delete() throws IOException {
		String url = "/Dvr/RemoveRecordSchedule?RecordId=" + _id;
		Source.http_post(url);
	}
	
	protected void refresh() throws IOException {
		String url = "/Dvr/GetRecordSchedule?RecordId=" + _id;
		String result = Source.http_get(url);
		
		try {
			JSONObject obj = new JSONObject(result);
			JSONObject list = obj.getJSONObject("RecRule");
			
			update_rule(this, list);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public int get_id() {
		return _id;
	}
	
	public int get_parentid() {
		return _parentid;
	}
	
	public void set_startoffset(int offset) {
		_startoffset = offset;
	}

	public int get_startoffset() {
		return _startoffset;
	}
	
	public void set_endoffset(int offset) {
		_endoffset = offset;
	}

	public int get_endoffset() {
		return _endoffset;
	}
	
	public void set_inactive(boolean inactive) {
		_inactive = inactive;
	}

	public boolean is_inactive() {
		return _inactive;
	}
	
	public void set_auto_commflag(boolean commflag) {
		_commflag = commflag;
	}

	public boolean get_auto_commflag() {
		return _commflag;
	}
	
	public void set_auto_transcode(boolean transcode) {
		_transcode = transcode;
	}

	public boolean get_auto_transcode() {
		return _transcode;
	}

	public String get_title() {
		return _title;
	}
	
	public void set_type(RecordingType type) {
		_type = type;
	}

	public RecordingType get_type() {
		return _type;
	}
	
	public void set_dupmethod(RecordingDupMethodType type) {
		_dupmethod = type;
	}

	public RecordingDupMethodType get_dupmethod() {
		return _dupmethod;
	}
	
	public void set_dupin(RecordingDupInType type) {
		_dupin = type;
	}
	
	public RecordingDupInType get_dupin() {
		return _dupin;
	}
	
	public void set_maxepisodes(int max) {
		_maxepisodes = max;
	}
	
	public int get_maxepisodes() {
		return _maxepisodes;
	}

	private int _id;
	private int _parentid;
	private int _startoffset;
	private int _endoffset;
	private int _findday;
	private int _maxepisodes;
	private boolean _inactive;
	private boolean _commflag;
	private boolean _transcode;
	private String _title;
	private Channel _channel;
	private LocalTime _findtime;
	private ZonedDateTime _starttime;
	private ZonedDateTime _endtime;
	private RecordingType _type;
	private RecordingDupMethodType _dupmethod;
	private RecordingDupInType _dupin;
	
	// From https://github.com/MythTV/mythtv/blob/4d24d54ef43164f1325fcbe914450b7dea37e28f/mythtv/libs/libmyth/recordingtypes.cpp
	public enum RecordingType {
	    NotRecord("Not Recording"),
	    DontRecord("Do not Record"),
	    OneRecord("Record One"),
		SingleRecord("Single Record"),
		DailyRecord("Record Daily"),
		WeeklyRecord("Record Weekly"),
		AllRecord("Record All"),
		OverrideRecord("Override Recording"),
		TemplateRecord("Recording Template");
	    
	    private String text;
	    RecordingType(String text) {
	    	this.text = text;
	    }
	    
	    public String getText() {
	    	return text;
	    }
	    
	    @Override
	    public String toString() {
	    	return getText();
	    }
	    
	    public static RecordingType fromString(String text) {
	    	for (RecordingType s : RecordingType.values()) {
	    		if ((s.text).equals(text)) return s;
	    	}
	    	return null;
	    }
	}
	
	// From https://github.com/MythTV/mythtv/blob/b113943461bb26dd411bfe625082fab44b1390ed/mythtv/html/tv/js/constants.js
	public enum RecordingDupMethodType {
	    None("None"),
	    Subtitle("Subtitle"),
	    Description("Description"),
		SubtitleAndDescription("Subtitle and Description"),
		SubtitleThenDescription("Subtitle then Description");
	    
	    private String text;
	    RecordingDupMethodType(String text) {
	    	this.text = text;
	    }
	    
	    public String getText() {
	    	return text;
	    }
	    
	    @Override
	    public String toString() {
	    	return getText();
	    }
	    
	    public static RecordingDupMethodType fromString(String text) {
	    	for (RecordingDupMethodType t : RecordingDupMethodType.values()) {
	    		if ((t.text).equals(text)) return t;
	    	}
	    	return null;
	    }
	}
	
	public enum RecordingDupInType {
		CurrentRecordings("Current Recordings"),
		PreviousRecordings("Previous Recordings"),
		AllRecordings("All Recordings"),
		NewEpisodesOnly("New Episodes Only");
	    
	    private String text;
	    RecordingDupInType(String text) {
	    	this.text = text;
	    }
	    
	    public String getText() {
	    	return text;
	    }
	    
	    @Override
	    public String toString() {
	    	return getText();
	    }
	    
	    public static RecordingDupInType fromString(String text) {
	    	for (RecordingDupInType t : RecordingDupInType.values()) {
	    		if ((t.text).equals(text)) return t;
	    	}
	    	return null;
	    }
	}
	
	
	private Rule(JSONObject rule_json) throws JSONException {
		update_rule(this, rule_json);
	}
	
	private static void update_rule(Rule rule, JSONObject rule_json) throws JSONException {
		rule._id = rule_json.getInt("Id");
		rule._parentid = rule_json.getInt("ParentId");
		rule._startoffset = rule_json.getInt("StartOffset");
		rule._endoffset = rule_json.getInt("EndOffset");
		rule._maxepisodes = rule_json.getInt("MaxEpisodes");
		rule._inactive = rule_json.getBoolean("Inactive");
		rule._commflag = rule_json.getBoolean("AutoCommflag");
		rule._transcode = rule_json.getBoolean("AutoTranscode");
		rule._title = rule_json.getString("Title");
		rule._findday = rule_json.getInt("FindDay");
		rule._findtime = LocalTime.parse(rule_json.getString("FindTime"));
		rule._starttime = LocalDateTime.parse(rule_json.getString("StartTime").replaceFirst(".$", "")).atZone(ZoneOffset.UTC);
		rule._endtime = LocalDateTime.parse(rule_json.getString("EndTime").replaceFirst(".$", "")).atZone(ZoneOffset.UTC);
		rule._type = RecordingType.fromString(rule_json.getString("Type"));
		rule._dupmethod = RecordingDupMethodType.fromString(rule_json.getString("DupMethod"));
		rule._dupin = RecordingDupInType.fromString(rule_json.getString("DupIn"));
		
		try {
			rule._channel = rule_json.getInt("ChanId") == 0 ? null : Channel.get_channel(rule_json.getInt("ChanId"));
		} catch (IOException e) {
			e.getMessage();
		}
	}
}
