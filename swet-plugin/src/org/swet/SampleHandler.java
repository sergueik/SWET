package org.swet;

import java.util.Locale;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class SampleHandler extends AbstractHandler {
	/**
	 * The constructor.
	 */
	public SampleHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil
				.getActiveWorkbenchWindowChecked(event);
		final Display display = Display.getDefault();
		final Shell shell = window.getShell();
		Locale.setDefault(Locale.ENGLISH);
		final TipDayEx tipDayEx = new TipDayEx();
		for (String tipMessage : new String[] { "This is the first tip",
				"This is the second tip", "This is the third tip",
				"This is the forth tip", "This is the fifth tip" }) {
			tipDayEx.addTip(String.format(
					"<h4>%s</h4>" + "<b>%s</b> " + "<u>%s</u> " + "<i>%s</i> " + "%s "
							+ "%s<br/>" + "<p color=\"#A00000\">%s</p>",
					tipMessage, tipMessage, tipMessage, tipMessage, tipMessage,
					tipMessage, tipMessage));

		}
		tipDayEx.open(shell, display);
		return null;
	}
}