package data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Channel {
	private static final Logger LOGGER = Logger.getLogger(Channel.class.getName());
	
	public static List<Channel> get_channels(int source_id) throws IOException {
		List<Channel> channels = new ArrayList<Channel>();
		String url = "/Channel/GetChannelInfoList?SourceID=" + source_id;
		String result = Source.http_get(url);
		
		try {
			JSONObject obj = new JSONObject(result);
			JSONObject list = obj.getJSONObject("ChannelInfoList");
			
			//String proto_version = list.getString("ProtoVer");
			// String backend_version = list.getString("Version");
			
			//if (!proto_version.equals(Source.get_version()))
			//	throw new IOException("Proto version mismatch (" + proto_version + " vs. " + Source.get_version() + ").");
			
			JSONArray channelinfos = list.getJSONArray("ChannelInfos");
			for (int i = 0; i < channelinfos.length(); i++) {
				Channel channel = new Channel(channelinfos.getJSONObject(i));
				
				channels.add(channel);
				_channelcache.put(channel._chanid, channel);
			}
		} catch (JSONException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
		}
		
		return channels;
	}
	
	public static Channel get_channel(int chan_id) throws IOException {
		if (!_channelcache.containsKey(chan_id)) {
			String url = "/Channel/GetChannelInfo?ChanID=" + chan_id;
			String result = Source.http_get(url);
			
			try {
				JSONObject obj = new JSONObject(result);
				JSONObject list = obj.getJSONObject("ChannelInfo");
				
				// Apparently "ProtoVer" doesn't come down in "GetChannelInfo"
				// String proto_version = list.getString("ProtoVer");
				// String backend_version = list.getString("Version");
				// if (!proto_version.equals(Source.get_version()))
					// throw new IOException("Proto version mismatch (" + proto_version + " vs. " + Source.get_version() + ").");
				
				_channelcache.put(chan_id, new Channel(list));
			} catch (JSONException e) {
				LOGGER.log(Level.SEVERE, e.getMessage() + " for " + url, e);
				throw new IOException("Could not find channel for ChanID=" + chan_id);
			}
		}
		
		return _channelcache.get(chan_id);
	}
	
	public static Channel get_channel(JSONObject channel_json) throws JSONException {
		int chanid = channel_json.getInt("ChanId");
		if (!_channelcache.containsKey(chanid)) {
			_channelcache.put(chanid, new Channel(channel_json));
		}
		
		return _channelcache.get(chanid);
	}
	
	public ImageIcon get_artwork(Dimension d) throws IOException {
		String uri = "/Guide/GetChannelIcon?ChanId=" + _chanid 
				+ "&Width=" + (int) d.getWidth() + "&Height=" + (int) d.getHeight();
		ImageIcon image = ArtworkManager.getArtwork(uri);
			
		// Filler Icon
		if (image == null || image.getImageLoadStatus() == MediaTracker.ERRORED)
			image = new ImageIcon(getClass().getResource("/res/station.jpg"));
		
		return image;
	}
	
	public boolean artwork_downloaded(Dimension d) {
		String uri = "/Guide/GetChannelIcon?ChanId=" + _chanid 
				+ "&Width=" + (int) d.getWidth() + "&Height=" + (int) d.getHeight();
		
		return ArtworkManager.artworkDownloaded(uri);
	}	
	
	public int get_chanid() {
		return _chanid;
	}

	public String get_channum() {
		return _channum;
	}

	public String get_callsign() {
		return _callsign;
	}

	public String get_iconurl() {
		return _iconurl;
	}

	public String get_channame() {
		return _channame;
	}

	private int _chanid;
	private String _channum;
	private String _callsign;
	private String _iconurl;
	private String _channame;
	
	private static Map<Integer, Channel> _channelcache = new HashMap<Integer, Channel>();
	
	private Channel(JSONObject channel_json) throws JSONException {
		_chanid = channel_json.getInt("ChanId");
		_channum = channel_json.getString("ChanNum");
		_callsign = channel_json.getString("CallSign");
		_iconurl = channel_json.getString("IconURL");
		_channame = channel_json.getString("ChannelName");
	}

	@Override
	public String toString() {
		return Integer.toString(_chanid);
	}
}
