package exec.download;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MockRequest {
	enum Method {
		POST, GET, PUT, DELETE
	}

	/**
	 * Send request with single parameters.
	 *
	 * @param params
	 * @param mockValues
	 * @return
	 */
	public File sendRequestGet(String param, String mockValue) {
		return sendRequestGet(new String[] { param },
				new String[] { mockValue });
	}

	public File sendRequestGet(String[] params, String[] mockValues) {
		return sendRequest(params, mockValues, Method.GET);
	}

	public File sendRequestPost(String param, String mockValue) {
		return sendRequestPost(new String[] { param },
				new String[] { mockValue });
	}

	public File sendRequestPost(String params[], String[] mockValues) {
		return sendRequest(params, mockValues, Method.POST);
	}

	/**
	 * Send request with multiple parameters.<br>
	 * e.g: param=mockValue<br>
	 * NOTICE: params.length must equals mockValues.length
	 *
	 * @param params
	 * @param mockValues
	 * @return
	 */
	public File sendRequest(String params[], String[] mockValues, Method method) {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);

		for (int i = params.length - 1; i >= 0; i--) {
			when(request.getParameter(params[i])).thenReturn(mockValues[i]);
		}

		File tmp = null;
		PrintWriter writer = null;
		ServletOutputStream stream = null;
		try {
			tmp = File.createTempFile("tmp_", ".tmp");

			// Same file with different responses: text or stream
			writer = new PrintWriter(tmp, "UTF-8");
			when(response.getWriter()).thenReturn(writer);
			stream = new StubServletOutputStream(tmp);
			when(response.getOutputStream()).thenReturn(stream);

			switch (method) {
			case GET:
				new FileDownloadServlet().doGet(request, response);
				break;
			case POST:
				new FileDownloadServlet().doPost(request, response);
				break;
			default:
				System.out.println("Un support");
			}

			// only if you want to verify cmd was called...
			for (int i = params.length - 1; i >= 0; i--) {
				verify(request, atLeast(1)).getParameter(params[i]);
			}

		} catch (Exception e) {
			fail(e.toString());
		} finally {
			try {
				if (writer != null)
					writer.close();
				if (stream != null)
					stream.close();
			} catch (IOException e) {
			}
		}
		return tmp;

	}
}
