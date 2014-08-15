package exec.shell.util;

import java.util.HashSet;
import java.util.Set;

public class CmdManager {
	static Set<String> whiteListCmds = new HashSet<String>();
	// TODO Read from configuration files
	static {
		// List
		whiteListCmds.add("dir");
		whiteListCmds.add("ls");
		whiteListCmds.add("ll");
		// echo
		whiteListCmds.add("echo");
		// File
		whiteListCmds.add("tail");
		whiteListCmds.add("grep");
		whiteListCmds.add("less");
		// Certain cmd with parameters, like Java
		whiteListCmds.add("java -version");
	}
	public static boolean isWhiteList(Set<String> cmds) {
		return whiteListCmds.containsAll(cmds);
	}
}
