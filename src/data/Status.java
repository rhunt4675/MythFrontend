package data;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Status {
	public static Status get_status() throws IOException, SAXException, ParserConfigurationException {
		Status system_status = null;
		String url = "/Status/xml";
		String result = Source.http_get(url);
		
		// Parse XML String
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document status = builder.parse(new InputSource(new StringReader(result)));
		Element root = status.getDocumentElement();
		
		system_status = new Status(root);
		return system_status;
	}
	
	public List<Encoder> get_encoders() {
		return _encoders;
	}
	
	public List<StatusProgram> get_schedules() {
		return _scheduled;
	}

	public MachineInfo get_machineinfo() {
		return _machineInfo;
	}
	
	public String get_protoVersion() {
		return _protoVersion;
	}
	
	public String get_mythVersion() {
		return _mythVersion;
	}

	private String _protoVersion;
	private String _mythVersion;	
	
	private List<Encoder> _encoders = new ArrayList<Encoder>();
	private List<StatusProgram> _scheduled = new ArrayList<StatusProgram>();
	private MachineInfo _machineInfo;
	
	private Status(Element root) {
		NamedNodeMap nnm = root.getAttributes();
		if (nnm != null) {
			_protoVersion = nnm.getNamedItem("protoVer").getNodeValue();
			_mythVersion = nnm.getNamedItem("version").getNodeValue();
		}
		
		// Iterate Through Children
		NodeList children = root.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			
			// Parse Encoders
			if (child.getNodeName().equals("Encoders")) {
				
				NodeList encoders = child.getChildNodes();
				for (int j = 0; j < encoders.getLength(); j++)
					// Ignore Whitespace
					if (encoders.item(j).getNodeType() == Node.ELEMENT_NODE)
						_encoders.add(new Encoder(encoders.item(j)));
			}
			
			// Parse Scheduled Recordings
			if (child.getNodeName().equals("Scheduled")) {
				
				NodeList programs = child.getChildNodes();
				for (int j = 0; j < programs.getLength(); j++)
					// Ignore Whitespace
					if (programs.item(j).getNodeType() == Node.ELEMENT_NODE)
						_scheduled.add(new StatusProgram(programs.item(j)));
			}
			
			// Parse Machine Information
			if (child.getNodeName().equals("MachineInfo"))
				_machineInfo = new MachineInfo(child);
		}	
	}
}
