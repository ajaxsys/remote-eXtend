package exec.shell;

import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import exec.shell.util.CmdUtils;

public class CmdExecutorTest {

	@Test
	public void testExecDirectly() {

		String input = "cat ./src/test/resources/foo.txt | grep finalName";
		if (CmdUtils.isWindows()) {
			// type .\src\test\resources\foo.txt | findstr finalName
			input = input.replace("cat ", "type ").replace("grep ", "findstr ")
					.replaceAll("/", "\\\\\\\\");// 正規表現の\==Java中の\\\\
		}

		try {
			String result = new CmdExecutor().execShell(input);
			Assert.assertEquals("\t\t\t\t<finalName>test.me</finalName>\n",
					result);
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testExecShell() {
		String input = "ls ./src/test/resources/foo.txt";

		Cmd firstCmd = CmdUtils.pauseCmd(input, "\\|").get(0);

		try {
			String result = new CmdExecutor().execShell(firstCmd);
			Assert.assertEquals("./src/test/resources/foo.txt\n", result);
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}

	// @Test
	// public void testExecShells() {
	// String input = "cat ./src/test/resources/foo.txt | grep finalName";
	// if (CmdUtils.isWindows()) {
	// // type .\src\test\resources\foo.txt | findstr finalName
	// input = input.replace("cat ", "type ").replace("grep ", "findstr ")
	// .replaceAll("/", "\\\\\\\\");// 正規表現の\==Java中の\\\\
	// }
	//
	// List<Cmd> cmds = CmdUtils.pauseCmd(input, "\\|");
	//
	// try {
	// String result = new CmdExecutor().execShell(cmds);
	// // NOTICE: Please confirm the difference with `testExecDirectly`
	// // method
	// Assert.assertEquals("src/test/resources/foo.txt\n"
	// + "pom.xml:52:\t\t\t\t<finalName>test.me</finalName>\n",
	// result);
	// // -- .matches("pom.xml.*<finalName>test.me</finalName>\n"));
	// } catch (IOException e) {
	// fail(e.getMessage());
	// }
	// }

	@Test
	public void testTail() {
		String input = "tail -n 2 ./src/test/resources/foo.txt";
		String expect = "\t\t</build>\n</project>\n";

		try {
			String result = new CmdExecutor().execShell(input);
			Assert.assertEquals(expect, result);
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}

	// FIXME How kill tail.exe automatically?
	// @Test Must execute `killall tail.exe` on windowns
	public void testTailCanNotBeKilled() {
		String input = "tail -f -n 2 ./src/test/resources/foo.txt";
		String expect = "\t\t</build>\n</project>\n\n\n[WARN] Stop because no response after 5000 ms.\n"
				+ "[WARN] You MUST manually CHECK if this process is alive then KILL it.\n"
				+ "[WARN] Original command: tail -f -n 2 ./src/test/resources/foo.txt";

		try {
			String result = new CmdExecutor().execShell(input);
			Assert.assertEquals(expect, result);
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testLess() {
		String input = "less ./src/test/resources/foo.txt";
		String expect = "\t\t</build>\n</project>\n";

		try {
			String result = new CmdExecutor().execShell(input);
			Assert.assertTrue(result.endsWith(expect));
			Assert.assertEquals(64, result.split("\n").length);
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}
}
