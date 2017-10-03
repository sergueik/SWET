package cop.swt.widgets.interfaces;

import java.io.IOException;

public interface IAppendable {
	boolean isEmpty();

	StringBuilder append(StringBuilder buf) throws IOException;
}
