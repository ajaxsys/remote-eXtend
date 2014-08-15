package exec.download;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FileDownloadServlet extends HttpServlet {

	private static final long serialVersionUID = 5806912774073415731L;

	private static final String DOWNLOADER_FILE_PATH = "/allin1_downloader.html";
	private static final String DL_CONFIG_KEY_WHITE_LIST = "download_whitelist_regexp";
	private static final String DL_CONFIG_FILE_MAX_SIZE = "download_max_size";

	private static String[] pathRegexpWhiteList = UConfig.getProperty(
			DL_CONFIG_KEY_WHITE_LIST).split(",");

	public static enum PARAMS {
		filepath
	};

	/**
	 * Get file download
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException {
		setReqEncoding(req);

		String filePath = req.getParameter(PARAMS.filepath.name());
		if (filePath == null || filePath.trim().isEmpty()) {
			resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return;
		}
		File file = new File(filePath);

		if (!file.exists() || file.isDirectory() || !isWhiteListFile(filePath)) {
			resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return;
		}

		if (file.length() > Long.valueOf(UConfig.getProperty(DL_CONFIG_FILE_MAX_SIZE))){
			resp.setStatus(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE);
			return;
		}

		UStreamResponse.respFileDownload(resp, file);
	}

	/**
	 * Get file list
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException {
		setReqEncoding(req);

		String filePathOrigin = req.getParameter(PARAMS.filepath.name());
		// Show index when no input
		if (filePathOrigin == null || filePathOrigin.trim().isEmpty()) {
			UStreamResponse.respHtmlFileResource(resp, DOWNLOADER_FILE_PATH);
			// resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
			return;
		}
		// Turn to linux mode(for test in Windows)
		String filePath = filePathOrigin.replaceAll("\\\\", "/");

		/*
		 * "/usr/bi" --> folder:[/usr/] + file or folder:[bi*] <br />
		 * "/usr/bin/" --> folder:[/usr/bin/] + show all file in directory
		 */
		String folderPath = filePath
				.substring(0, filePath.lastIndexOf("/") + 1);
		String filePartern = filePath.substring(filePath.lastIndexOf("/") + 1);

		File folder = new File(folderPath);

		if (!folder.exists() || !folder.isDirectory()) {
			resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
			return;
		}

		// Do list
		FilenameFilter fileter = getFileNameFileter(filePartern);
		File[] files;
		if (fileter != null) {
			files = folder.listFiles(fileter);
		} else {
			files = folder.listFiles();
		}

		showFileListResult(resp, files);
	}

	private void setReqEncoding(HttpServletRequest req) {
		try {
			req.setCharacterEncoding("UTF-8");
		} catch (Exception e) {
		}
	}

	private void showFileListResult(HttpServletResponse resp, File[] files) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (file.isFile() && !isWhiteListFile(file)) {
				continue;
			}

			sb.append(file.getName() + (file.isDirectory() ? "/" : ""));
			sb.append("\t").append(file.length()).append("\t")
					.append(new Date(file.lastModified()));
			sb.append("\n");
		}

		UStreamResponse.respText(resp, sb.toString());
	}

	private FilenameFilter getFileNameFileter(final String filePartern) {
		if ("".equals(filePartern.trim())) {
			return null;
		}
		return new FilenameFilter() {
			public boolean accept(File file, String name) {
				return name.startsWith(filePartern);
			}
		};
	}

	boolean isWhiteListFile(File file) {
		return isWhiteListFile(file.getAbsolutePath(), pathRegexpWhiteList);
	}

	boolean isWhiteListFile(String fileName) {
		return isWhiteListFile(fileName, pathRegexpWhiteList);
	}

	boolean isWhiteListFile(String fileName, String[] whiteList) {
		for (String regexp : whiteList) {
			if (fileName.matches(regexp))
				return true;
		}
		return false;
	}

}
