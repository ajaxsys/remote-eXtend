package exec.download;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import exec.download.FileDownloadServlet.PARAMS;

public class FileDownloadServletGetTest {

	// Get http://localhost:8080/download?filepath=./pom.xml
	@Test
	public void testListSingleFile() {
		String GET_FILE_NAME = "tmp-日本語-testDownloadAbsPath.log";
		String home = System.getProperty("user.home");

		try {
			// Create file to be download
			File tmp = new File(home, GET_FILE_NAME);
			FileUtils.writeStringToFile(tmp, GET_FILE_NAME);

			// file name<tab>file size<tab>last modified date
			String EXP_OUTPUT = GET_FILE_NAME + "\t37\t"
					+ new Date(tmp.lastModified()) + "\n";

			String resp = testCommon(tmp);

			assertEquals(EXP_OUTPUT, resp);

		} catch (IOException e) {
			fail(e.toString());
		}
	}

	@Test
	public void testIndexFile() {
		String resp = testCommon(null);
		assertTrue(resp.endsWith("</html>"));
	}

	// Get http://localhost:8080/download?filepath=./src/test/resources/list/foo
	@Test
	public void testListMultiFileLargeThen10() {

		// Create file to be download
		File tmp = new File("src/test/resources/list", "foo");
		// file name<tab>file size<tab>last modified date

		String resp = testCommon(tmp);
		assertEquals(11, resp.split("\n").length ); // Line match
	}

	// Get
	// http://localhost:8080/download?filepath=./src/test/resources/list/foo1
	@Test
	public void testListMultiFileSmallThen10() {

		// Create file to be download
		File tmp = new File("src/test/resources/list", "foo1");
		// file name<tab>file size<tab>last modified date
		String EXP_OUTPUT = "foo1.txt	3	.+\n" + "foo10.txt	3	.+\n"
				+ "foo11.txt	3	.+\n";

		String resp = testCommon(tmp);
		assertTrue(resp != null && resp.matches(EXP_OUTPUT));
	}

	private String testCommon(File tmp) {

		try {
			// Download it from net
			File downFile = new MockRequest().sendRequestGet(
						PARAMS.filepath.name(), tmp == null ? "" : tmp.getAbsolutePath());

			assertNotNull(downFile);

			String respText = FileUtils.readFileToString(downFile, "UTF-8");

			// clear
			if (tmp != null) {
				tmp.delete();
			}

			downFile.delete();

			return respText;
		} catch (IOException e) {
			fail(e.toString());
		}
		return null;
	}

	@Test
	public void testIsWhiteList() {
		String[] pathRegexpWhiteList = new String[] { "^.*\\.log$",
				"^.*\\.txt$" };

		File ngFile = new File("src/test/resources/forbidden.file");
		File okFile1 = new File("src/test/resources/foo.txt");
		File okFile2 = new File("src/test/resources/bar.log");

		assertFalse(new FileDownloadServlet().isWhiteListFile(
				ngFile.getAbsolutePath(), pathRegexpWhiteList));
		assertTrue(new FileDownloadServlet().isWhiteListFile(
				okFile1.getAbsolutePath(), pathRegexpWhiteList));
		assertTrue(new FileDownloadServlet().isWhiteListFile(
				okFile2.getAbsolutePath(), pathRegexpWhiteList));
	}
}
