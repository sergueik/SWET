package com.github.sergueik.swet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Selected test scenarios for Selenium WebDriver
 * @author: Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

// based on:
// https://qna.habr.com/q/704745

public class BatchPipeRun {

	public static void main(String[] args)
			throws IOException, InterruptedException {
		LocalDate futureDate = LocalDate.now().plusMonths(12);
		String formattedDate = futureDate
				.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		String serialNumber = "foo";
		String keyName = "bar";
		String command = "c:\\Users\\Serguei\\echoargs.cmd " + serialNumber + " "
				+ keyName + " " + formattedDate + " 2>&1";
		System.err.println("Exit code:" + runWithPipes(command));
	}

	public static String runWithPipes(String command)
			throws IOException, InterruptedException {
		String[] shell = { "cmd.exe", };
		Process process = Runtime.getRuntime().exec(shell);
		// this code allows seeing the child process on the console
		new Thread(new SyncPipe(process.getErrorStream(), System.err)).start();
		new Thread(new SyncPipe(process.getInputStream(), System.out)).start();

		PrintWriter stdinPrintWriter = new PrintWriter(process.getOutputStream());
		stdinPrintWriter.println(command);
		stdinPrintWriter.close();

		BufferedReader stdoutBufferedReader = new BufferedReader(
				new InputStreamReader(process.getInputStream()));

		BufferedReader stderrBufferedReader = new BufferedReader(
				new InputStreamReader(process.getErrorStream()));
		String line = null;

		StringBuffer processOutput = new StringBuffer();
		while ((line = stdoutBufferedReader.readLine()) != null) {
			processOutput.append(line);
			// add a platform-independent newline
			processOutput.append(System.getProperty("line.separator"));
		}
		StringBuffer processError = new StringBuffer();
		while ((line = stderrBufferedReader.readLine()) != null) {
			processError.append(line);
			processError.append(String.format("%n"));
		}
		System.err.println("OUTPUT:" + processOutput.toString());
		System.err.println("ERROR	:" + processError.toString());
		String exitCode = Integer.toString(process.waitFor());
		return exitCode;
	}

	private static class SyncPipe implements Runnable {
		private final OutputStream outputStream;
		private final InputStream intputStream;

		public SyncPipe(InputStream intputStream, OutputStream outputStream) {
			this.intputStream = intputStream;
			this.outputStream = outputStream;

		}

		public void run() {
			try {
				final byte[] buffer = new byte[1024];
				for (int length = 0; (length = this.intputStream.read(buffer)) != -1;) {
					this.outputStream.write(buffer, 0, length);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}