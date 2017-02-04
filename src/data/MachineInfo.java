package data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MachineInfo {
	@SuppressWarnings("unused")
	private LocalDateTime _guideUpdateStart, _guideUpdateEnd, _guideDataThru;
	@SuppressWarnings("unused")
	private int _guideDays;
	@SuppressWarnings("unused")
	private String _guideStatus;
	
	@SuppressWarnings("unused")
	private StorageGroup _total;
	private List<StorageGroup> _stores = new ArrayList<StorageGroup>();
	private float[] _machineload = new float[3];

	// Construct MachineInfo from XML
	public MachineInfo(Node node) {
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			
			// Ignore Text Nodes
			if (child.getNodeType() != Node.ELEMENT_NODE) continue;
			
			// Load Node
			if (child.getNodeName().equals("Load")) {
				NamedNodeMap nnm = child.getAttributes();
				if (nnm != null) {
					if (nnm.getNamedItem("avg1") != null)
						_machineload[0] = Float.parseFloat(nnm.getNamedItem("avg1").getNodeValue());
					if (nnm.getNamedItem("avg2") != null)
						_machineload[1] = Float.parseFloat(nnm.getNamedItem("avg2").getNodeValue());
					if (nnm.getNamedItem("avg3") != null)
						_machineload[2] = Float.parseFloat(nnm.getNamedItem("avg3").getNodeValue());
				}
			}
			
			// Guide Node
			if (child.getNodeName().equals("Guide")) {
				NamedNodeMap nnm = child.getAttributes();
				if (nnm != null) {
					if (nnm.getNamedItem("start") != null)
						_guideUpdateStart = LocalDateTime.parse(nnm.getNamedItem("start").getNodeValue(), 
								DateTimeFormatter.ofPattern("EEE MMM d uuuu, h:mm a"));
					if (nnm.getNamedItem("end") != null)
						_guideUpdateEnd = LocalDateTime.parse(nnm.getNamedItem("end").getNodeValue(),
								DateTimeFormatter.ofPattern("EEE MMM d uuuu, h:mm a"));
					if (nnm.getNamedItem("guideThru") != null)
						_guideDataThru = LocalDateTime.parse(nnm.getNamedItem("guideThru")
								.getNodeValue().replaceFirst(".$",  ""));
					if (nnm.getNamedItem("guideDays") != null)
						_guideDays = Integer.parseInt(nnm.getNamedItem("guideDays").getNodeValue());
					if (nnm.getNamedItem("status") != null)
						_guideStatus = nnm.getNamedItem("status").getNodeValue();
				}
			}
			
			// Storage Node
			if (child.getNodeName().equals("Storage")) {
				NodeList groups = child.getChildNodes();
				
				for (int j = 0; j < groups.getLength(); j++) {
					// Ignore Text Nodes
					if (groups.item(j).getNodeType() != Node.ELEMENT_NODE) continue;
					
					// Create a StorageGroup from XML
					StorageGroup sg = new StorageGroup(groups.item(j));
					if (sg.get_id().equals("total")) _total = sg;
					_stores.add(sg);
				}
			}
		}
	}
	
	/* Inner Class to Model Storage */
	private class StorageGroup {
		@SuppressWarnings("unused")
		private long _total, _expirable, _used, _free;
		@SuppressWarnings("unused")
		private String _id, _dir;
		@SuppressWarnings("unused")
		private boolean _livetv, _deleted;
		
		// Construct StorageGroup from XML
		public StorageGroup(Node node) {

			// Space Availability
			NamedNodeMap nnm = node.getAttributes();
			if (nnm.getNamedItem("total") != null)
				_total = Long.parseLong(nnm.getNamedItem("total").getNodeValue());
			if (nnm.getNamedItem("expirable") != null)
				_expirable = Long.parseLong(nnm.getNamedItem("expirable").getNodeValue());
			if (nnm.getNamedItem("used") != null)
				_used = Long.parseLong(nnm.getNamedItem("used").getNodeValue());
			if (nnm.getNamedItem("free") != null)
				_free = Long.parseLong(nnm.getNamedItem("free").getNodeValue());
			
			// Group Identification
			if (nnm.getNamedItem("id") != null) _id = nnm.getNamedItem("id").getNodeValue();
			if (nnm.getNamedItem("dir") != null) _dir = nnm.getNamedItem("dir").getNodeValue();
			
			// Other Flags
			if (nnm.getNamedItem("livetv") != null)
				_livetv = nnm.getNamedItem("livetv").getNodeValue().equals("1");
			if (nnm.getNamedItem("deleted") != null)
				_deleted = nnm.getNamedItem("deleted").getNodeValue().equals("1");
		}

		public String get_id() {
			return _id;
		}
	}
}
