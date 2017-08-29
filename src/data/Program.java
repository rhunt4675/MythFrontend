package data;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Program {
	private static final Logger LOGGER = Logger.getLogger(Program.class.getName());
	protected abstract void refresh() throws IOException; 
	
	protected Program(JSONObject program_json) throws JSONException {
		updateProgram(this, program_json);
	}
	
	protected Program(Node program_xml) {
		updateProgramXML(this, program_xml);
	}
	
	public ZonedDateTime get_starttime() {
		return _starttime;
	}

	public ZonedDateTime get_endtime() {
		return _endtime;
	}

	public String get_title() {
		return _title;
	}

	public String get_subtitle() {
		return _subtitle;
	}
	
	public String get_category() {
		return _category;
	}

	public String get_seriesid() {
		return _seriesid;
	}

	public String get_programid() {
		return _programid;
	}

	public String get_inetref() {
		return _inetref;
	}

	public LocalDate get_airdate() {
		return _airdate;
	}

	public String get_description() {
		return _description;
	}

	public String get_filesize() {
		return _filesize;
	}

	public int get_season() {
		return _season;
	}

	public int get_episode() {
		return _episode;
	}

	public RecordingStatus get_status() {
		return _status;
	}

	public int get_recordid() {
		return _recordid;
	}

	protected void set_recordid(int recordid) {
		_recordid = recordid;
	}
	
	public int get_recordedid() {
		return _recordedid;
	}
	
	public boolean is_watched() {
		return _watched;
	}
	
	public Channel get_channel() {
		return _channel;
	}
	
	public enum RecordingStatus {
		PENDING           (-15),
		FAILING           (-14),
		OTHERRECORDING    (-13),
		OTHERTUNING       (-12),
		MISSEDFUTURE      (-11),
		TUNING            (-10),
		FAILED            (-9),
		TUNERBUSY         (-8),
		LOWDISKSPACE      (-7),
		CANCELLED         (-6),
		MISSED            (-5),
		ABORTED           (-4),
		RECORDED          (-3),
		RECORDING         (-2),
		WILLRECORD        (-1),
		UNKNOWN           (0),
		DONTRECORD        (1),
		PREVIOUSRECORDING (2),
		CURRENTRECORDING  (3),
		EARLIERSHOWING    (4),
		TOOMANYRECORDINGS (5),
		NOTLISTED         (6),
		CONFLICT          (7),
		LATERSHOWING      (8),
		REPEAT            (9),
		INACTIVE          (10),
		NEVERRECORD       (11),
		OFFLINE           (12),
		OTHERSHOWING      (13);
		
	    private int status;
	    RecordingStatus(int status) {
	    	this.status = status;
	    }
	    
	    public int getStatusInt() {
	    	return status;
	    }
	    
	    @Override
	    public String toString() {
	    	return name();
	    }
	    
	    public static RecordingStatus fromInt(int status) {
	    	for (RecordingStatus r : RecordingStatus.values()) {
	    		if (r.status == status) return r;
	    	}
	    	return null;
	    }
	}

	private String _title;
	private String _subtitle;
	private String _category;
	private String _seriesid;
	private String _programid;
	private String _inetref;
	private String _description;
	private String _filesize;
	private int _season;
	private int _episode;
	private int _recordid;
	private int _recordedid;
	private boolean _watched;
	private Channel _channel;
	private ZonedDateTime _starttime;
	private ZonedDateTime _endtime;
	private LocalDate _airdate;
	private RecordingStatus _status;
	
	private static int PROGRAM_WATCHED = 0x00000200;
	
	protected static void updateProgram(Program program, JSONObject program_json) throws JSONException {
		program._channel = Channel.get_channel(program_json.getJSONObject("Channel"));
		program._title = program_json.getString("Title");
		program._subtitle = program_json.getString("SubTitle");
		program._category = program_json.getString("Category");
		program._seriesid = program_json.getString("SeriesId");
		program._programid = program_json.getString("ProgramId");
		program._inetref = program_json.getString("Inetref");
		program._description = program_json.getString("Description");
		program._filesize = program_json.getString("FileSize");
		program._season = program_json.getInt("Season");
		program._episode = program_json.getInt("Episode");
		program._recordid = program_json.getJSONObject("Recording").getInt("RecordId");
		program._recordedid = program_json.getJSONObject("Recording").getInt("RecordedId");
		program._watched = ((program_json.getInt("ProgramFlags") & PROGRAM_WATCHED) != 0);
		program._status = RecordingStatus.fromInt(program_json.getJSONObject("Recording").getInt("Status"));
		
		try {
			program._starttime = LocalDateTime.parse(program_json.getString("StartTime").replaceFirst(".$", "")).atZone(ZoneOffset.UTC);
			program._endtime = LocalDateTime.parse(program_json.getString("EndTime").replaceFirst(".$",  "")).atZone(ZoneOffset.UTC);
			program._airdate = LocalDate.parse(program_json.getString("Airdate"), DateTimeFormatter.ISO_LOCAL_DATE);
		} catch (DateTimeException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
		}
	}
	
	protected static void updateProgramXML(Program program, Node program_xml) {
		NamedNodeMap nnm = program_xml.getAttributes();
		NodeList nl = program_xml.getChildNodes();
		
		// Iterate Attributes
		if (nnm != null) {
			program._title = nnm.getNamedItem("title").getNodeValue();
			program._subtitle = nnm.getNamedItem("subTitle").getNodeValue();
			program._category = nnm.getNamedItem("category").getNodeValue();
			program._seriesid = nnm.getNamedItem("seriesId").getNodeValue();
			program._programid = nnm.getNamedItem("programId").getNodeValue();
			program._filesize = nnm.getNamedItem("fileSize").getNodeValue();
			program._starttime = LocalDateTime.parse(nnm.getNamedItem("startTime").getNodeValue()
					.replaceFirst(".$",  "")).atZone(ZoneOffset.UTC);
			program._endtime = LocalDateTime.parse(nnm.getNamedItem("endTime").getNodeValue()
					.replaceFirst(".$",  "")).atZone(ZoneOffset.UTC);
		}
		
		// Iterate Children
		for (int i = 0; i < nl.getLength(); i++) {
			Node child = nl.item(i);
			
			// <Channel>
			if (child.getNodeName().equals("Channel")) {
				NamedNodeMap channelNamedNodeMap = child.getAttributes();
				
				try {
					program._channel = Channel.get_channel(Integer.parseInt(
							channelNamedNodeMap.getNamedItem("chanId").getNodeValue()));
				} catch (IOException e) {
					LOGGER.log(Level.SEVERE, e.toString(), e);
				}
			}
			
			// <Recording>
			if (child.getNodeName().equals("Recording")) {
				NamedNodeMap recordingNamedNodeMap = child.getAttributes();
				program._recordid = Integer.parseInt(
						recordingNamedNodeMap.getNamedItem("recordId").getNodeValue());
				program._status = RecordingStatus.fromInt(Integer.parseInt(
						recordingNamedNodeMap.getNamedItem("recStatus").getNodeValue()));
			}
		}
		
		program._description = program_xml.getTextContent();
	}
}
