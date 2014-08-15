package exec.shell.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class HtmlEscapeTest {

	@Test
	public void testEscapeTextArea() {
		String input = "<html lang=\"en\">日\n 本 語</html>";
		String output = "&lt;html&nbsp;lang=&quot;en&quot;&gt;日\n&nbsp;本&nbsp;語&lt;/html&gt;";

		// Not escape br tag versus Escape method
		assertEquals("Escape < > \" ", output, HtmlEscape.escapeTextArea(input));
	}

	@Test
	public void testEscape() {
		String input = "<html lang=\"en\">日\n 本& 語</html>";
		String output = "&lt;html&nbsp;lang=&quot;en&quot;&gt;日<br/>&nbsp;本&amp;&nbsp;語&lt;/html&gt;";

		assertEquals("Escape < > \" ", output, HtmlEscape.escape(input));
	}

	@Test
	public void testEscapeTags() {
		String input = "<html lang=\"en\">日 本 語</html>";
		String output = "&lt;html lang=&quot;en&quot;&gt;日 本 語&lt;/html&gt;";

		assertEquals("Escape < > \" ", output, HtmlEscape.escapeTags(input));
	}

	@Test
	public void testEscapeBr() {
		String input = "<html lang=\"en\">日\n 本\n 語</html>";
		String output = "<html lang=\"en\">日<br/> 本<br/> 語</html>";

		assertEquals("Escape \\n ", output, HtmlEscape.escapeBr(input));
	}

	@Test
	public void testEscapeSpecial() {
		String input = "<html lang=\"en\">日 & 本 & 語</html>";
		String output = "<html&nbsp;lang=\"en\">日&nbsp;&amp;&nbsp;本&nbsp;&amp;&nbsp;語</html>";
		assertEquals("Escape & ", output, HtmlEscape.escapeSpecial(input));
	}

}
