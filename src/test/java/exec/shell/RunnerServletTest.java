package exec.shell;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

public class RunnerServletTest {

	@Test
	public void testDoPost1() {
		String shellCMD = "ls ./src/test/resources/foo.txt";
		boolean isAjax = true;
		String expect = "./src/test/resources/foo.txt\n";
		String result = sendRequest(shellCMD, isAjax);
		Assert.assertEquals(expect, result);
	}

	@Test
	public void testDoGet1() {
		String shellCMD = "ls ./src/test/resources/foo.txt";
		boolean isAjax = false;
		String expect = "<html><head><meta http-equiv='Content-Type' content='text/html;charset=UTF-8' /><title>Shell Result</title></head><body>./src/test/resources/foo.txt<br/></body></html>";

		String result = sendRequest(shellCMD, isAjax);
		Assert.assertEquals(expect, result);
	}

	// http://localhost:8080/shell?cmd=tail -n 2 ./src/test/resources/foo.txt
	// http://localhost:8080/shell?cmd=tail%20-n%202%20./src/test/resources/foo.txt
	@Test
	public void testTail() {
		String shellCMD = "tail -n 2 ./src/test/resources/foo.txt";
		boolean isAjax = true;
		String expect = "\t\t</build>\n</project>\n";

		String result = sendRequest(shellCMD, isAjax);
		Assert.assertEquals(expect, result);
	}

	// http://localhost:8080/shell?cmd=tail -f -n 2 ./src/test/resources/foo.txt
	// http://localhost:8080/shell?cmd=tail%20-f%20-n%202%20./src/test/resources/foo.txt
	// FIXME How kill tail.exe automatically?
	// @Test Must execute `killall tail.exe` on windowns
	public void testTailF() {
		String shellCMD = "tail -f -n 2 ./src/test/resources/foo.txt";
		boolean isAjax = true;
		String expect = "\t\t</build>\n</project>\n\n\n[WARN] Stop because no response after 5000 ms.\n"
				+ "[WARN] You MUST manually CHECK if this process is alive then KILL it.\n"
				+ "[WARN] Original command: tail -f -n 2 ./src/test/resources/foo.txt";

		String result = sendRequest(shellCMD, isAjax);
		Assert.assertEquals(expect, result);
	}

	private String sendRequest(String cmd, boolean isAjaxPost) {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);

		final String PARAM_CMD = "cmd";
		when(request.getParameter(PARAM_CMD)).thenReturn(cmd);
		try {
			File tmp = File.createTempFile("tmp_", ".tmp");
			PrintWriter writer = new PrintWriter(tmp);
			when(response.getWriter()).thenReturn(writer);

			if (isAjaxPost)
				new RunnerServlet().doPost(request, response);
			else
				new RunnerServlet().doGet(request, response);

			// only if you want to verify cmd was called...
			verify(request, atLeast(1)).getParameter(PARAM_CMD);

			writer.flush(); // it may not have been flushed yet...
			return FileUtils.readFileToString(tmp, "UTF-8");
		} catch (Exception e) {
			fail(e.getMessage());
			return null;
		}
	}
}
