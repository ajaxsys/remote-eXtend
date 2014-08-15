package exec.shell;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import exec.shell.util.CmdManager;
import exec.shell.util.CmdUtils;
import exec.shell.util.HtmlEscape;

public class RunnerServlet extends HttpServlet {

	private static final long serialVersionUID = 7370668445991255135L;

	private static final String HEADER = "<html><head><meta http-equiv='Content-Type' content='text/html;charset=UTF-8' /><title>Shell Result</title></head><body>";
	private static final String FOOTER = "</body></html>";
	private boolean isPost = false;

	// For Ajax
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		isPost = true;
		doGet(req, resp);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		// Get param
		String cmdWithParams = req.getParameter("cmd");

		setHttpHeader(resp);

		String response = execute(cmdWithParams);

		// Output
		if (isPost) {
			// For Ajax
			resp.getWriter().append(response);
		} else {
			// For web show
			resp.getWriter().append(HEADER).append(HtmlEscape.escape(response))
					.append(FOOTER).flush();
		}

	}

	private String execute(String cmdWithParams) throws IOException {
		// Check
		if (CmdUtils.isEmpty(cmdWithParams)) {
			return "NG command.";
		}

		// prevent `|` inject. e.g: `ls / | del *`
		if (!CmdUtils.isOKChain(cmdWithParams)) {
			return "NOT allow chain command except `|`";
		}

		List<Cmd> cmds = CmdUtils.pauseCmd(cmdWithParams);

		if (!CmdManager.isWhiteList(CmdUtils.getCmdKeySet(cmds))) {
			return "Not allow command.";
		}

		return new CmdExecutor().execShell(cmdWithParams);
	}

	private void setHttpHeader(ServletResponse res) {
		res.setCharacterEncoding("UTF-8");
		res.setContentType("text/html; charset=UTF-8");
	}

}
