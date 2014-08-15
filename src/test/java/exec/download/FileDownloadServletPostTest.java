package exec.download;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import exec.download.FileDownloadServlet.PARAMS;

public class FileDownloadServletPostTest {

	// http://localhost:8080/download?filepath=./src/test/resources/foo.txt
	@Test
	public void testDownloadRelationPath() {
		File downFile = new MockRequest().sendRequestPost(PARAMS.filepath.name(),
				"./src/test/resources/foo.txt");

		assertNotNull(downFile);

		try {
			String respText = FileUtils.readFileToString(downFile, "UTF-8");
			assertEquals(64, respText.split("\n").length);
		} catch (IOException e) {
			fail(e.toString());
		}

		downFile.delete();
	}

	// http://localhost:8080/download?filepath=./src/test/resources/foo.txt
	@Test
	public void testDownloadAbsPath() {
		String SET_SOME_WORD = "Hello World!";
		String home = System.getProperty("user.home");

		try {
			// Create file to be download
			File tmp = new File(home, System.currentTimeMillis()
					+ "tmp-of-testDownloadAbsPath.log");
			FileUtils.writeStringToFile(tmp, SET_SOME_WORD);

			// Download it from net
			File downFile = new MockRequest().sendRequestPost(
					PARAMS.filepath.name(), tmp.getAbsolutePath());

			assertNotNull(downFile);

			String respText = FileUtils.readFileToString(downFile, "UTF-8");
			assertEquals(SET_SOME_WORD, respText);

			// clear
			tmp.delete();
			downFile.delete();
		} catch (IOException e) {
			fail(e.toString());
		}
	}

}
