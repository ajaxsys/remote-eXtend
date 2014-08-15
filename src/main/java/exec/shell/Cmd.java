package exec.shell;

public class Cmd {
	String cmd;
	String[] params;

	public Cmd(String cmd, String[] params) {
		this.cmd = cmd;
		this.params = params;
	}

	public String getCmd() {
		return cmd;
	}

	public String[] getParams() {
		return params;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(cmd).append(" ");
		for (String p : params) {
			sb.append(p).append(" ");
		}
		return sb.toString();
	}

}
