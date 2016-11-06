package data;

import java.awt.Dimension;
import java.awt.MediaTracker;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import utils.AppProperties;

public class Recording extends Program {
	
	public enum Artwork {FANART, COVERART, BANNER, PREVIEW};
	
	public static List<Recording> get_recordings() throws IOException {
		return get_recordings("");
	}
	
	public static List<Recording> get_recordings(String title_regex) throws IOException {
		List<Recording> recordings = new ArrayList<Recording>();
		
		/* ESCAPE MYTHTV's REGEX CHARACTERS MANUALLY */
		String param = URLEncoder.encode(title_regex, "UTF-8");
		param = param.replaceAll("%28", "%5C%28"); // Escape '('
		param = param.replaceAll("%29", "%5C%29"); // Escape ')'
		String url = "/Dvr/GetRecordedList?Descending=true&TitleRegEx=" + param;
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
				recordings.add(new Recording(programs.getJSONObject(i)));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return recordings;
	}
	
	@Override
	protected void refresh() throws IOException {
		String url = "/Dvr/GetRecorded?RecordedId=" + get_recordedid();
		String result = Source.http_get(url);
		
		try {
			JSONObject obj = new JSONObject(result);
			JSONObject program = obj.getJSONObject("Program");
			
			updateProgram(this, program);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void play() throws IOException {
		String url = Source.playback_url("/Content/GetRecording?RecordedId=" + get_recordedid());
		new ProcessBuilder(AppProperties.getPlayer(), url).start();
	}
	
	public void delete(boolean allow_rerecord) throws IOException {
		String url = "/Dvr/DeleteRecording?RecordedId=" + get_recordedid() + "&AllowRerecord=" + (allow_rerecord ? "true" : "false");
		
		try {
			while (true) {
				String status = Source.http_post(url);
				if ((new JSONObject(status)).getBoolean("bool") == false) 
					break;
				
				Thread.sleep(100);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void undelete() throws IOException {
		String url = "/Dvr/UnDeleteRecording?RecordedId=" + get_recordedid();
		Source.http_get(url);
	}
	
	public void mark_watched(boolean flag) throws IOException {
		String url = "/Dvr/UpdateRecordedWatchedStatus?RecordedId=" + get_recordedid() + "&Watched=" + flag;
		Source.http_post(url);
		
		refresh();
	}
	
	public void toggle_watched() throws IOException {
		mark_watched(!is_watched());
	}
	
	public ImageIcon get_artwork(Artwork type, Dimension d) throws IOException {
		String url = get_artwork_url(type, d.width, d.height);
		
		if (!_artworkcache.containsKey(url)) {
			ImageIcon image = Source.image_get(url);
			
			// Filler Icon
			if (image.getImageLoadStatus() == MediaTracker.ERRORED) {
				switch (type) {
				case BANNER:
					image = new ImageIcon(getClass().getResource("/res/mythtv.jpg")); break;
				case COVERART:
				case FANART:
					break;
				case PREVIEW:
					image = new ImageIcon(getClass().getResource("/res/notfound.jpg")); break;
				default: break;
				}
			}

			_artworkcache.put(url, image);
		}

		return _artworkcache.get(url);
	}
	
	public boolean artwork_downloaded(Artwork type, Dimension d) {
		String url = get_artwork_url(type, d.width, d.height);
		
		return _artworkcache.containsKey(url);
	}
	
	private String get_artwork_url(Artwork type, int width, int height) {
		return (type == Artwork.PREVIEW 
				? "/Content/GetPreviewImage?RecordedId=" + get_recordedid() 
				: ("/Content/GetRecordingArtwork?Inetref=" + get_inetref() + "&Season=" + get_season() + "&Type="
						+ (type == Artwork.BANNER ? "banner" : (type == Artwork.COVERART 
						? "coverart" : (type == Artwork.FANART ? "fanart" : "")))))
				+ "&Width=" + width + "&Height=" + height;
	}
	
	private static Map<String, ImageIcon> _artworkcache = new HashMap<String, ImageIcon>();
	
	private Recording(JSONObject recording_json) throws JSONException {
		super(recording_json);
	}
}
