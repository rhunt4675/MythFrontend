package data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class VideoSource {
	public static List<VideoSource> get_videosources() throws IOException {
		List<VideoSource> sources = new ArrayList<VideoSource>();
		String url = "/Channel/GetVideoSourceList";
		String result = Source.http_get(url);
		
		try {
			JSONObject obj = new JSONObject(result);
			JSONObject list = obj.getJSONObject("VideoSourceList");
			
			//String proto_version = list.getString("ProtoVer");
			// String backend_version = list.getString("Version");
			
			//if (!proto_version.equals(Source.get_version()))
			//	throw new IOException("Proto version mismatch (" + proto_version + " vs. " + Source.get_version() + ").");
			
			JSONArray videosources = list.getJSONArray("VideoSources");
			for (int i = 0; i < videosources.length(); i++) {
				sources.add(new VideoSource(videosources.getJSONObject(i)));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return sources;
	}
	
	public static VideoSource get_videosource(int sourceid) throws IOException {
		VideoSource source = null;
		String url = "/Channel/GetVideoSource?SourceID=" + sourceid;
		String result = Source.http_get(url);
		
		try {
			JSONObject obj = new JSONObject(result);
			JSONObject list = obj.getJSONObject("VideoSource");
			
			source = new VideoSource(list);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return source;
	}
	
	public void delete() throws IOException {
		String url = "/Channel/RemoveVideoSource?SourceID=" + _id;
		Source.http_post(url);
	}
	
	@Override
	public String toString() {
		return "(" + _id + ") " + _name;
	}

	public int get_id() {
		return _id;
	}

	public boolean is_eit() {
		return _eit;
	}

	public String get_name() {
		return _name;
	}

	public String get_grabber() {
		return _grabber;
	}

	public String get_freqtable() {
		return _freqtable;
	}
	
	public List<Channel> get_channels() {
		return _channels;
	}
	
	public void set_channels(List<Channel> channels) {
		_channels = channels;
	}

	private int _id;
	private boolean _eit;
	private String _name;
	private String _grabber;
	private String _freqtable;
	private List<Channel> _channels = null;
		
	private VideoSource(JSONObject source_json) throws JSONException {
		this._id = source_json.getInt("Id");
		this._name = source_json.getString("SourceName");
		this._grabber = source_json.getString("Grabber");
		this._freqtable = source_json.getString("FreqTable");
		this._eit = source_json.getBoolean("UseEIT");
	}
}
