package data;

import org.w3c.dom.Node;

public class StatusProgram extends Program {

	protected StatusProgram(Node node) {
		super(node);
	}

	@Override
	protected void refresh() {
		throw new UnsupportedOperationException("StatusProgram cannot be refreshed atomically.");
	}

}
