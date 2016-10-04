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

public class Title {
	
	public static List<Title> get_titles() throws IOException {
		List<Title> titles = new ArrayList<Title>();
		String url = "/Dvr/GetTitleInfoList";
		String result = Source.http_get(url);
		
		try {
			JSONObject obj = new JSONObject(result);
			
			JSONObject root = obj.getJSONObject("TitleInfoList");
			JSONArray list = root.getJSONArray("TitleInfos");
			
			List<String> names = new ArrayList<String>();
			
			for (int i = 0; i < list.length(); i++) {
				Title title = new Title(list.getJSONObject(i));
				if (names.contains(title.get_title())) continue;
				else names.add(title.get_title());
				
				titles.add(title);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return titles;
	}
	
	public synchronized ImageIcon get_title_artwork(Dimension dimension) throws IOException {
		String url = null;
		
		for (String inetref : _inetref) {
			url = "/Content/GetRecordingArtwork?Width=" + dimension.width
				+ "&Height=" + dimension.height + "&Inetref=" + URLEncoder.encode(inetref, "utf-8");
			
			if (!_artworkcache.containsKey(url)) {
				ImageIcon image = Source.image_get(url);
				
				// Filler Icon
				if (image.getImageLoadStatus() == MediaTracker.ERRORED) {
					_artworkcache.put(url, new ImageIcon(getClass().getResource("/res/coverart.jpg")));
				} else {
					_artworkcache.put(url, image);
					break;
				}
			}
		}

		return _artworkcache.get(url);		
	}
	
	@Override
	public String toString() {
		return get_title();
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
	
	private Title(JSONObject title) throws JSONException {
		_title = title.getString("Title");
		_inetref.add(title.getString("Inetref"));
		_count = title.getInt("Count");
	}
}
