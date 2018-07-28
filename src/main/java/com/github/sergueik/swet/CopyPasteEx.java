package com.github.sergueik.swet;

/**
 * Copyright 2014 - 2017 Serguei Kouzmine
 */

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.RTFTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

//  origin:  http://www.java2s.com/Code/Java/SWT-JFace-Eclipse/CopyandPaste.htm
public class CopyPasteEx {
  Display display = new Display();
  Shell shell = new Shell(display);

  public CopyPasteEx() {
    shell.setLayout(new GridLayout());
    
    ToolBar toolBar = new ToolBar(shell, SWT.FLAT);
    ToolItem itemCopy = new ToolItem(toolBar, SWT.PUSH);
    ToolItem itemPaste = new ToolItem(toolBar, SWT.PUSH);
    itemCopy.setText("Copy");
    itemPaste.setText("Paste");
    
    itemCopy.addListener(SWT.Selection, new Listener() {
      public void handleEvent(Event event) {
        Clipboard clipboard = new Clipboard(display);
        String plainText = "Hello World";
        String rtfText = "{\\rtf1\\b Hello World}";
        TextTransfer textTransfer = TextTransfer.getInstance();
        RTFTransfer rftTransfer = RTFTransfer.getInstance();
        clipboard.setContents(new String[]{plainText, rtfText}, new Transfer[]{textTransfer, rftTransfer});
        clipboard.dispose();
      }
    });
    
    itemPaste.addListener(SWT.Selection, new Listener() {
      public void handleEvent(Event event) {
        Clipboard clipboard = new Clipboard(display);
        
        TransferData[] transferDatas = clipboard.getAvailableTypes();

        for(int i=0; i<transferDatas.length; i++) {
          // Checks whether RTF format is available.
          if(RTFTransfer.getInstance().isSupportedType(transferDatas[i])) {
            System.out.println("Data is available in RTF format");
            break;
          }
        }
        
        String plainText = (String)clipboard.getContents(TextTransfer.getInstance());
        String rtfText = (String)clipboard.getContents(RTFTransfer.getInstance());
        
        System.out.println("PLAIN: " + plainText + "\n" + "RTF: " + rtfText);
        
        clipboard.dispose();
      }
    });

    shell.pack();
    shell.open();
    //textUser.forceFocus();

    // Set up the event loop.
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch()) {
        // If no more entries in event queue
        display.sleep();
      }
    }

    display.dispose();
  }

  private void init() {

  }

  public static void main(String[] args) {
    new CopyPasteEx();
  }
}

