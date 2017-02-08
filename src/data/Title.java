package data;

import java.awt.Dimension;
import java.awt.MediaTracker;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.ImageIcon;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
	
	public ImageIcon get_title_artwork(Dimension dimension) throws IOException {
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
		_count = title.getInt("Count");
		
		String inetRef = title.getString("Inetref");
		if (!inetRef.isEmpty()) _inetref.add(inetRef);
	}
}
