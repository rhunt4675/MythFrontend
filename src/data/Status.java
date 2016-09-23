package data;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

public class Status {
	public static Status get_status() throws IOException {
		Status system_status = null;
		String url = "/Status/xml";
		String result = Source.http_get(url);
		
		try {
			JSONObject root = XML.toJSONObject(result);
			JSONObject status = root.getJSONObject("Status");
			
			String proto_version = Integer.toString(status.getInt("protoVer"));
			// String backend_version = status.getString("version");
			
			if (!proto_version.equals(Source.get_version()))
				throw new IOException("Proto version mismatch (" + proto_version + " vs. " + Source.get_version() + ").");
			
			system_status = new Status(status);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return system_status;
	}
	
	public JSONArray get_encoders() {
		return _encoders;
	}

	public JSONArray get_schedules() {
		return _schedules;
	}

	public JSONObject get_jobs() {
		return _jobs;
	}

	public JSONObject get_machineinfo() {
		return _machineinfo;
	}

	private JSONArray _encoders;
	private JSONArray _schedules;
	private JSONObject _jobs;
	private JSONObject _machineinfo;
	
	private Status(JSONObject status) throws JSONException {
		_encoders = status.getJSONObject("Encoders").getJSONArray("Encoder");
		_schedules = status.getJSONObject("Scheduled").getJSONArray("Program");
		_jobs = status.getJSONObject("JobQueue");
		_machineinfo = status.getJSONObject("MachineInfo");
	}
}
