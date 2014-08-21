package exec.download;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DispacherServlet extends HttpServlet {

	private static final long serialVersionUID = 5806912774073415731L;
	private static final String ENTRY_PAGE_FILE_PATH = "/allin1_entry.html";
	private static final String SECURE_KEY = "SECURE_KEY";
	private static final String SECURE_VAL = UConfig.getProperty(SECURE_KEY);
	private static final String APP_ID = "APP_ID";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException {
//		try {
//			req.setCharacterEncoding("UTF-8");
//			resp.setCharacterEncoding("UTF-8");
//		} catch (Exception e1) {
//		}

		String appID = getAppIDOnSecure(req);
		if (appID == null) {
			showEntryPage(resp);
			return;
		}

		// APP Select
		try {
			if ("H2Console".equals(appID)) {
				// NOT: include!
				getServletContext().getNamedDispatcher("H2Console").forward(
						req, resp);
			} else if ("FileDownloader".equals(appID)) {
				// NOT: include!
				getServletContext().getNamedDispatcher("FileDownloader")
						.forward(req, resp);
			} else {
				showEntryPage(resp);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String getAppIDOnSecure(HttpServletRequest req) {
		String appID = null;
		// Secure check
		Cookie[] cookies = req.getCookies();
		if (cookies==null){
			return null;
		}

		boolean isPassed = false;
		for (Cookie cookie : cookies) {
			if (SECURE_KEY.equals(cookie.getName())
					&& SECURE_VAL.equals(cookie.getValue())) {
				isPassed = true;
			} else if (APP_ID.equals(cookie.getName())) {
				appID = cookie.getValue();
			}
		}
		if (!isPassed) {
			return null;
		}
		return appID;
	}

	private void showEntryPage(HttpServletResponse resp) {
		UStreamResponse.respHtmlFileResource(resp, ENTRY_PAGE_FILE_PATH);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException {
		doGet(req, resp);
	}

}
