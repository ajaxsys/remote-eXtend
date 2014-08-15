package exec.shell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import exec.shell.util.CmdUtils;

public class CmdExecutor {

	boolean isStop = false;
	boolean isStopByKilled = false;
	// TODO Read from configuration files
	final public static int WAIT_MILLIS = 5000;

	// Support all mode(pipe, and, or...)
	public String execShell(String cmdAndParams) throws IOException {
		// String[] shellCmd = cmd.split("[ \t]+");
		List<String> shellCmd = CmdUtils.createShellCmd(cmdAndParams);

		// Execute command
		ProcessBuilder pb = new ProcessBuilder(shellCmd);

		pb.redirectErrorStream(true); // equivalent of 2>&1
		final Process p = pb.start();

		final BufferedReader in = new BufferedReader(new InputStreamReader(
				p.getInputStream()));

		final StringBuilder resp = new StringBuilder();

		// Get response Thread
		new ResponseMonitor(in, resp).start();

		// Monitor Thread
		new ProcessKillerMonitor(resp).start();

		waitProcessStop(p, resp, cmdAndParams);

		return resp.toString();
	}

	private void waitProcessStop(final Process p, final StringBuilder resp,
			String cmdAndParams) {
		while (!isStop) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
		try {
			// FIXME when `tail -f foo.txt`, tail.exe NOT be killed when destroy()!
			p.destroy();
			p.waitFor();

			if (CmdUtils.isRunning(p)) {
				if (CmdUtils.isRunning(p)) {
					resp.append("\n\n[FATAL]process is still alive"
							+ p.toString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (isStopByKilled) {
			resp.append("\n\n[WARN] Stop because no response after ")
					.append(WAIT_MILLIS)
					.append(" ms.\n")
					.append("[WARN] You MUST manually CHECK if this process is alive then KILL it.\n")
					.append("[WARN] Original command: ").append(cmdAndParams);
		}
	}

	// synchronized private static void setStop() {
	// isStop = true;
	// }

	@Deprecated
	public String execShell(Cmd cmd) throws IOException {
		List<String> shellCmd = CmdUtils.createShellCmd(cmd.getCmd(),
				cmd.getParams());

		// Execute command
		ProcessBuilder pb = new ProcessBuilder(shellCmd);

		pb.redirectErrorStream(true); // equivalent of 2>&1
		Process p = pb.start();

		BufferedReader in = new BufferedReader(new InputStreamReader(
				p.getInputStream()));

		String line = null;
		StringBuilder responseData = new StringBuilder();
		while ((line = in.readLine()) != null) {
			responseData.append(line).append("\n");
		}
		isStop = true;
		return responseData.toString();
	}

	// // And mode
	// @Deprecated
	// public String execShell(List<Cmd> cmds) throws IOException {
	// // TODO windows list/ Linux list / common list
	// StringBuilder responseData = new StringBuilder();
	// for (Cmd cmd : cmds) {
	// responseData.append(execShell(cmd));
	// }
	//
	// return responseData.toString();
	// }

	class ResponseMonitor extends Thread {
		private BufferedReader in;
		private StringBuilder resp;

		public ResponseMonitor(BufferedReader in, StringBuilder resp) {
			this.in = in;
			this.resp = resp;
		}

		public void run() {
			String line = null;
			// Output Thread
			try {
				while ((line = in.readLine()) != null) {
					resp.append(line).append("\n");
				}
				isStop = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	class ProcessKillerMonitor extends Thread {
		private int responseDataSize = 0;
		private StringBuilder responseData;

		/**
		 * Stop process like `tail -f` which will no died in status less http
		 * protocal<br>
		 * Check if response data size be changed in WAIT_MILLIS<br>
		 * If NO change, set the process stop flag to true<br>
		 *
		 * @param responseData
		 *            For size check, read only.
		 */
		public ProcessKillerMonitor(StringBuilder responseData) {
			this.responseData = responseData;
		}

		public void run() {
			while (true) {
				// // Wait N second
				try {
					// NOTICE: `sleep / 2` because while block will enter 2
					// times
					Thread.sleep(WAIT_MILLIS / 2);
				} catch (InterruptedException e) {
				}

				// If no response changed stop process
				// System.out.println(responseData.length());
				if (responseData.length() > responseDataSize) {
					responseDataSize = responseData.length();
				} else {
					isStop = true;
					isStopByKilled = true;
					// p.destroy();
					// System.out.println(CmdUtils.isRunning(p));
					// responseData
					// .append("\n\n[WARN]Stop because no response after "
					// + WAIT_MILLIS + " ms");
					break;
				}
			}
		}
	}
}
