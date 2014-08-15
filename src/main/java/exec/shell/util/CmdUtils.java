package exec.shell.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import exec.shell.Cmd;

public class CmdUtils {
	public static boolean isEmpty(String cmd) {
		return cmd == null || "".equals(cmd.trim());
	}

	/**
	 * http://www.linuxnix.com/2012/07/23-awesome-less-known-linuxunix-command-
	 * chaining-examples.html <br/>
	 * <br/>
	 * [Char — Is support, Comment]<br/>
	 * & —×, Sends process background (so we can run multiple process parallel) <br/>
	 * ; —×, Run multiple commands in one run, sequentially. <br/>
	 * \ —×, To type larger command in multiple lines <br/>
	 * && —×, Logical AND operator <br/>
	 * || —×, Logical OR operator <br/>
	 * ! —○, NOT operator <br/>
	 * | —○, PIPE operator <br/>
	 * {} —○, Command combination operator. <br/>
	 * () —○, Precedence operator <br/>
	 */
	final static String[] ngChainList = new String[] { "&", ";", "\\", "&&",
			"||" };
	final static String okChainRegexp = "\\|";

	final public static int WAIT_MILLIS = 5000;

	// synchronized private static void setStop() {
	// isStop = true;
	// }

	public static boolean isOKChain(String cmd) {
		return checkChain(cmd, ngChainList);
	}

	private static boolean checkChain(String cmd, String[] chains) {
		for (String chain : chains) {
			if (cmd.contains(chain)) {
				return false;
			}
		}
		return true;
	}

	public static List<Cmd> pauseCmd(String cmdSWithParams, String regexp) {
		// cmdSWithParams e.g: tail -f a.txt | grep hello
		// multiCmdWithParams e.g: [tail -f a.txt , grep hello]
		List<Cmd> list = new ArrayList<Cmd>();
		String[] multiCmdWithParams = cmdSWithParams.split(regexp);
		for (String aCmdWithParams : multiCmdWithParams) {
			// aCmdWithParams e.g: [tail, -f, a.txt]
			// trim space & tab
			aCmdWithParams = aCmdWithParams.replaceAll("^[ \t]*|[ \t]*$", "");
			String[] splits = aCmdWithParams.split("[ \t]+");
			String cmdName = splits[0];
			String[] params = Arrays.copyOfRange(splits, 1, splits.length);
			Cmd cmd = new Cmd(cmdName, params);
			list.add(cmd);
		}
		return list;
	}

	public static List<Cmd> pauseCmd(String cmdSWithParams) {
		return pauseCmd(cmdSWithParams, okChainRegexp);
	}

	public static Set<String> getCmdKeySet(List<Cmd> cmds) {
		HashSet<String> set = new HashSet<String>();
		for (Cmd cmd : cmds) {
			set.add(cmd.getCmd());
		}
		return set;
	}

	// Assemble command
	public static List<String> createShellCmd(String cmd) {
		return createShellCmd(cmd, null);
	}

	public static List<String> createShellCmd(String cmd, String[] params) {
		List<String> shellCmd = new ArrayList<String>();

		if (isWindows()) {
			shellCmd.add("cmd");
			shellCmd.add("/C");
		} else {
			shellCmd.add("/bin/sh");
			shellCmd.add("-c");
		}

		shellCmd.add(cmd);

		if (params != null) {
			Collections.addAll(shellCmd, params);
		}

		return shellCmd;
	}

	public static boolean isWindows() {
		return System.getProperty("os.name").toLowerCase().contains("windows");
	}

	public static boolean isRunning(Process process) {
		try {
			process.exitValue();
			return false;
		} catch (Exception e) {
			return true;
		}
	}

}
