package data;

import java.awt.Dimension;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.ImageIcon;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import data.Recording.RecordingChangedEventListener;

public class Title {
	
	public static List<Title> get_titles() throws IOException {
		Map<String, Title> titles = new TreeMap<String, Title>();
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
			e.printStackTrace();
		}
		 
		return new ArrayList<Title>(titles.values());
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
		Map<String, ImageIcon> result = new TreeMap<String, ImageIcon>();
		
		for (String inetref : _inetref) {
			/* Get Current Season (leave season parameter off) Coverart */
			String currentSeasonURL = "/Content/GetRecordingArtwork?Width=" + dimension.width
				+ "&Height=" + dimension.height + "&Inetref=" + URLEncoder.encode(inetref, "utf-8");
			
			if (!_artworkcache.containsKey(currentSeasonURL))
				_artworkcache.put(currentSeasonURL, Source.image_get(currentSeasonURL));
			
			ImageIcon image = _artworkcache.get(currentSeasonURL);
			if (image != null && !result.containsKey(image.getDescription()))
				result.put(image.getDescription(), image);
			
			/* Get Previous Seasons' Coverart */
			int failures = 0;
			for (int season = 1; failures < 5; season++) {
				String newSeasonURL = currentSeasonURL + "&Season=" + season;
				
				if (!_artworkcache.containsKey(newSeasonURL))
					_artworkcache.put(newSeasonURL, Source.image_get(newSeasonURL));		
				
				ImageIcon newImage = _artworkcache.get(newSeasonURL);
				if (newImage != null && !result.containsKey(newImage.getDescription())) {
					result.put(newImage.getDescription(), newImage); failures = 0;
				} else {
					failures++;
				}
			}
		}
		
		// Filler Icon
		List<ImageIcon> coverarts = new ArrayList<ImageIcon>(result.values());
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
	private List<String> _inetref = new ArrayList<String>();
	private int _count;
	private Map<String, ImageIcon> _artworkcache = new HashMap<String, ImageIcon>();
	
	private Set<Recording> _recordings = new HashSet<Recording>();
	private Set<Recording> _unwatchedRecordings = new HashSet<Recording>();
	
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
