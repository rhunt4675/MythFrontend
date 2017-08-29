package data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import trakt.TraktSource;
import ui.ExternalPlayer;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Recording extends Program {
	private static final Logger LOGGER = Logger.getLogger(Recording.class.getName());
	public enum Artwork {FANART, COVERART, BANNER, PREVIEW};
	
	private List<RecordingChangedEventListener> _recordingListeners = new ArrayList<RecordingChangedEventListener>();
	private ExternalPlayer _player;
	private Long _traktId;
	
	private ZonedDateTime _startts;
	private ZonedDateTime _endts;
	
	public static List<Recording> get_recordings() throws IOException {
		return get_recordings("", 0, 0);
	}
	
	public static List<Recording> get_recordings(String title_regex, int count, int startindex) throws IOException {
		List<Recording> recordings = new ArrayList<Recording>();
		
		/* ESCAPE MYTHTV's REGEX CHARACTERS MANUALLY */
		String param = URLEncoder.encode(title_regex, "UTF-8");
		param = param.replaceAll("%28", "%5C%28"); // Escape '('
		param = param.replaceAll("%29", "%5C%29"); // Escape ')'
		String url = "/Dvr/GetRecordedList?Descending=true&TitleRegEx=" + param 
				+ "&Count=" + count + "&StartIndex=" + startindex;
		String result = Source.http_get(url);
		
		try {
			JSONObject obj = new JSONObject(result);
			JSONObject list = obj.getJSONObject("ProgramList");
			
			//String proto_version = list.getString("ProtoVer");
			// String backend_version = list.getString("Version");
			
			//if (!proto_version.equals(Source.get_version()))
			//	throw new IOException("Proto version mismatch (" + proto_version + " vs. " + Source.get_version() + ").");
			
			JSONArray programs = list.getJSONArray("Programs");
			for (int i = 0; i < programs.length(); i++) {
				recordings.add(new Recording(programs.getJSONObject(i)));
			}
		} catch (JSONException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
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
			LOGGER.log(Level.SEVERE, e.toString(), e);
		}
	}
	
	public void play() throws IOException {
		// Only One Stream at any Instance
		if (_player != null && _player.isActive()) {
			JOptionPane.showMessageDialog(null, "The selected recording is already playing.", 
					"Duplicate Stream", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		// Start Playback
		if (_player == null)
			_player = new ExternalPlayer();
		_player.play(this);
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
			
			// Success!
			for (RecordingChangedEventListener listener : _recordingListeners) {
				listener.onRecordingWatched(this);
				listener.onRecordingDeleted(this);
			}
		} catch (JSONException | InterruptedException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
		}
	}
	
	public void undelete() throws IOException {
		String url = "/Dvr/UnDeleteRecording?RecordedId=" + get_recordedid();
		Source.http_get(url);
		
		// Success!
		for (RecordingChangedEventListener listener : _recordingListeners)
			listener.onRecordingUndeleted(this);
	}
	
	public void mark_watched(boolean flag) throws IOException {
		String url = "/Dvr/UpdateRecordedWatchedStatus?RecordedId=" + get_recordedid() + "&Watched=" + flag;
		Source.http_post(url);
		
		// Success!
		for (RecordingChangedEventListener listener : _recordingListeners) {
			if (flag) listener.onRecordingWatched(this);
			else listener.onRecordingUnwatched(this);
		}
		
		refresh();
	}
	
	public void toggle_watched() throws IOException {
		mark_watched(!is_watched());
	}
	
	public String get_playback_url() {
		return Source.playback_url(String.valueOf(get_recordedid()));
	}
	
	public Long get_trakt_episodeId(String authCode) {
		// Check Cache
		if (_traktId != null)
			return _traktId;
		
		// Recording Must be Identifiable
		if (get_season() != 0 && get_episode() != 0) {
			try {
				// Lookup Show ID
				String show = TraktSource.doGet("search/show?query=" + URLEncoder.encode(get_title(), "UTF-8"), authCode);
				Long showId = ((JSONObject) (new JSONArray(show)).get(0)).getJSONObject("show")
						.getJSONObject("ids").getLong("trakt");
				
				// Lookup Episode ID
				String episode = TraktSource.doGet("shows/" + showId + "/seasons/" + get_season() +
						"/episodes/" + get_episode(), authCode);
				_traktId = (new JSONObject(episode)).getJSONObject("ids").getLong("trakt");
			} catch (IOException | JSONException e) {
				LOGGER.log(Level.SEVERE, e.toString(), e);
			}
		}
		return _traktId;
	}
	
	public void addRecordingChangedEventListener(RecordingChangedEventListener listener) {
		_recordingListeners.add(listener);
	}
	
	public ImageIcon get_artwork(Artwork type, Dimension d) throws IOException {
		String uri = get_artwork_uri(type, d.width, d.height);
		
		ImageIcon image = ArtworkManager.getArtwork(uri);
		
		// Filler Icon
		if (image == null) {
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
		return image;
	}
	
	private String get_artwork_uri(Artwork type, int width, int height) {
		return (type == Artwork.PREVIEW 
				? "/Content/GetPreviewImage?RecordedId=" + get_recordedid() 
				: ("/Content/GetRecordingArtwork?Inetref=" + get_inetref() + "&Season=" + get_season() + "&Type="
						+ (type == Artwork.BANNER ? "banner" : (type == Artwork.COVERART 
						? "coverart" : (type == Artwork.FANART ? "fanart" : "")))))
				+ "&Width=" + width + "&Height=" + height;
	}
	
	public ZonedDateTime get_startts() {
		return _startts;
	}
	
	public ZonedDateTime get_endts() {
		return _endts;
	}
		
	private Recording(JSONObject recording_json) throws JSONException {
		super(recording_json);
		
		_startts = LocalDateTime.parse(recording_json.getJSONObject("Recording").getString("StartTs").replaceFirst(".$", "")).atZone(ZoneOffset.UTC);
		_endts = LocalDateTime.parse(recording_json.getJSONObject("Recording").getString("EndTs").replaceFirst(".$", "")).atZone(ZoneOffset.UTC);
	}
	
	public interface RecordingChangedEventListener {
		void onRecordingDeleted(Recording r);
		void onRecordingUndeleted(Recording r);
		void onRecordingWatched(Recording r);
		void onRecordingUnwatched(Recording r);
	}
}
