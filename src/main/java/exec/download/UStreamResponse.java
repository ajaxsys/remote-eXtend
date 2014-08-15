package exec.download;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletResponse;

public class UStreamResponse {

	public static void respFileDownload(HttpServletResponse resp, File file) {
		// Do download
		InputStream in = null;
		resp.setContentType("application/octet-stream");
		resp.setHeader("Content-Disposition", "attachment; filename=\""
				+ encode(file.getName(), "UTF-8") + "\"");
		resp.setHeader("Content-Length", String.valueOf(file.length()));
		try {
			in = new FileInputStream(file);
			writeOutput(resp, in);
		} catch (IOException e) {
			e.printStackTrace();
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	public static void respHtmlFileResource(HttpServletResponse resp,
			String rscFilePath) {
		InputStream in = new UStreamResponse().getClass().getResourceAsStream(
				rscFilePath);
		try {
			resp.setContentType("text/html; charset=UTF-8");
			resp.setCharacterEncoding("UTF-8");
			writeOutput(resp, in);
		} catch (IOException e) {
			e.printStackTrace();
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	public static void respText(HttpServletResponse resp, String text) {
		PrintWriter out = null;
		try {
			resp.setCharacterEncoding("UTF-8");
			resp.setContentType("text/plain; charset=UTF-8");
			out = resp.getWriter();
			out.print(text);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} finally {
//			if (out != null) {
//				out.close();
//			}
		}
	}
	private static void writeOutput(HttpServletResponse resp, InputStream in)
			throws IOException {
		OutputStream out = null;
		try {

			out = resp.getOutputStream();
			byte[] buff = new byte[2048];
			int len = 0;
			while ((len = in.read(buff, 0, buff.length)) != -1) {
				out.write(buff, 0, len);
			}
			out.flush();
		} catch (IOException e){
			//User cancel
			System.out.println("Download Stopped." + e.getCause());
		}	finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
//			if (out != null) {
//				try {
//					out.close();
//				} catch (IOException e) {
//				}
//			}
		}
	}

	private static String encode(String name, String encode) {
		StringBuilder sb = new StringBuilder();
		char[] array = name.toCharArray();
		for (char c : array) {
			if (!isAscii(c)) {
				try {
					sb.append(URLEncoder.encode(String.valueOf(c), encode));
				} catch (UnsupportedEncodingException e) {
				}
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	private static boolean isAscii(char c) {
		return c <= 256;
	}

}
