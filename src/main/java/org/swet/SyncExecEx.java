package org.swet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * syncExec Example. 
 */
public class SyncExecEx {

  public static void main(String[] args) {

    final Display display = new Display();
    Shell shell = new Shell(display);
    
    final Runnable print = new Runnable() {
      public void run() {
        System.out.println("Print from thread: \t" + Thread.currentThread().getName());
      }
    };
    
    final Thread applicationThread = new Thread("applicationThread") {
      public void run() {
        System.out.println("Hello from thread: \t" + Thread.currentThread().getName());
        display.syncExec(print);
        System.out.println("Bye from thread: \t" + Thread.currentThread().getName());
      }
    };
    
    shell.setText("syncExec Example");
    shell.setSize(300, 100);
    
    Button button = new Button(shell, SWT.CENTER);
    button.setText("Click to start");
    button.setBounds(shell.getClientArea());
    button.addSelectionListener(new SelectionListener() {
      public void widgetDefaultSelected(SelectionEvent e) {
      }
      public void widgetSelected(SelectionEvent e) {
        applicationThread.start();
      }
    });
    
    shell.open();    
    
    
    while(! shell.isDisposed()) {
      if(! display.readAndDispatch()) {// If no more entries in event queue
        display.sleep();
      }
    }
    
    display.dispose();
  
  }
}


           
       