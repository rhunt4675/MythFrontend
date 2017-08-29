package data;

import data.Recording.RecordingChangedEventListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Title {
	private static final Logger LOGGER = Logger.getLogger(Title.class.getName());

	public static List<Title> get_titles() throws IOException {
		Map<String, Title> titles = new TreeMap<>();
		String url = "/Dvr/GetTitleInfoList";
		String result = Source.http_get(url);
		
		try {
			JSONObject obj = new JSONObject(result);
			
			JSONObject root = obj.getJSONObject("TitleInfoList");
			JSONArray list = root.getJSONArray("TitleInfos");
						
			for (int i = 0; i < list.length(); i++) {
				Title title = new Title(list.getJSONObject(i));
				if (titles.containsKey(title.get_title())) {
					titles.get(title.get_title())._inetref.addAll(title._inetref);
					continue;
				}
				
				titles.put(title.get_title(), title);
			}
		} catch (JSONException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
		}
		 
		return new ArrayList<>(titles.values());
	}
	
	public List<Recording> get_recordings(int count, int startIndex) throws IOException {
		List<Recording> result = Recording.get_recordings(_title, count, startIndex);
		
		// Add All Recordings to Tracker Sets
		for (Recording recording : result) {
			if (!recording.is_watched()) 
				_unwatchedRecordings.add(recording);
			_recordings.add(recording);
			
			// Register Listener
			recording.addRecordingChangedEventListener(_recordingChangedEventListener);
		}
		
		return result;
	}
	
	public List<ImageIcon> get_title_artwork(Dimension dimension) throws IOException {
		Map<String, ImageIcon> result = new TreeMap<>();
		
		for (String inetref : _inetref) {
			/* Get Current Season (leave season parameter off) Coverart */
			String currentSeasonURI = "/Content/GetRecordingArtwork?Width=" + dimension.width
				+ "&Height=" + dimension.height + "&Inetref=" + URLEncoder.encode(inetref, "utf-8");
			
			ImageIcon image = ArtworkManager.getArtwork(currentSeasonURI);
			if (image != null && image.getDescription() != null && !result.containsKey(image.getDescription()))
				result.put(image.getDescription(), image);
			
			/* Get Previous Seasons' Coverart */
			int failures = 0;
			for (int season = 1; failures < 5; season++) {
				String newSeasonURL = currentSeasonURI + "&Season=" + season;
				ImageIcon newImage = ArtworkManager.getArtwork(newSeasonURL);
				
				if (newImage != null && !result.containsKey(newImage.getDescription())) {
					result.put(newImage.getDescription(), newImage); failures = 0;
				} else {
					failures++;
				}
			}
		}
		
		// Filler Icon
		List<ImageIcon> coverarts = new ArrayList<>(result.values());
		if (coverarts.size() == 0)
			coverarts.add(new ImageIcon(getClass().getResource("/res/coverart.jpg")));
			
		return coverarts;
	}
	
	@Override
	public String toString() {
		return get_title() + " (" + _unwatchedRecordings.size() + ")";
	}
	
	public String get_title() {
		return _title;
	}
	
	public int get_count() {
		return _count;
	}

	private String _title;
	private List<String> _inetref = new ArrayList<>();
	private int _count;
	
	private Set<Recording> _recordings = new HashSet<>();
	private Set<Recording> _unwatchedRecordings = new HashSet<>();
	
	private Title(JSONObject title) throws JSONException {
		_title = title.getString("Title");
		_count = title.getInt("Count");
		
		String inetRef = title.getString("Inetref");
		if (!inetRef.isEmpty()) _inetref.add(inetRef);
	}
	
	private RecordingChangedEventListener _recordingChangedEventListener = new RecordingChangedEventListener() {
		@Override public void onRecordingWatched(Recording r) {
			_unwatchedRecordings.remove(r);
		}
		
		@Override public void onRecordingUnwatched(Recording r) {
			_unwatchedRecordings.add(r);
		}
		
		@Override public void onRecordingUndeleted(Recording r) {
			_recordings.add(r);
		}
		
		@Override public void onRecordingDeleted(Recording r) {
			_recordings.add(r);
		}
	};
}
