package data;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Encoder {
	public int get_id() {
		return _id;
	}

	public int get_state() {
		return _state;
	}

	public int get_sleepstatus() {
		return _sleepstatus;
	}
	public boolean is_connected() {
		return _connected;
	}

	public boolean is_local() {
		return _local;
	}

	public String get_hostname() {
		return _hostname;
	}

	public String get_devlabel() {
		return _devlabel;
	}

	private int _id;
	private int _state;
	private int _sleepstatus;
	private boolean _connected;
	private boolean _local;
	private String _hostname;
	private String _devlabel;
	private List<StatusProgram> _inProgressPrograms = new ArrayList<StatusProgram>();
	
	// Build Encoder from XML
	public Encoder(Node node) {
		NamedNodeMap nnm = node.getAttributes();
		if (nnm != null) {
			_id = Integer.parseInt(nnm.getNamedItem("id").getNodeValue());
			_state = Integer.parseInt(nnm.getNamedItem("state").getNodeValue());
			_sleepstatus = Integer.parseInt(nnm.getNamedItem("sleepstatus").getNodeValue());
			_connected = nnm.getNamedItem("connected").getNodeValue().equals("1");
			_local = nnm.getNamedItem("local").getNodeValue().equals("1");
			_hostname = nnm.getNamedItem("hostname").getNodeValue();
			_devlabel = nnm.getNamedItem("devlabel").getNodeValue();
		}
		
		NodeList nl = node.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++)
			if (nl.item(i).getNodeType() == Node.ELEMENT_NODE)
				_inProgressPrograms.add(new StatusProgram(nl.item(i)));
	}
}
