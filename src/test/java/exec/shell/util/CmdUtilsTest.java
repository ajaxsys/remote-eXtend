package exec.shell.util;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import exec.shell.Cmd;

public class CmdUtilsTest {

	@Test
	public void testIsEmpty() {
		Assert.assertTrue(CmdUtils.isEmpty(""));
		Assert.assertTrue(CmdUtils.isEmpty(null));
	}

	@Test
	public void testIsOKChain() {
		Assert.assertFalse(CmdUtils.isOKChain("ls *.txt ; del -rf"));
		Assert.assertFalse(CmdUtils.isOKChain("ls *.txt && del -rf"));
		Assert.assertFalse(CmdUtils.isOKChain("ls *.txt || del -rf"));
		Assert.assertFalse(CmdUtils.isOKChain("ls *.txt & del -rf"));
		Assert.assertFalse(CmdUtils.isOKChain("\\\n ls"));

		Assert.assertTrue(CmdUtils.isOKChain("ls *.txt | grep abc "));
	}

	@Test
	public void testPauseCmdStringWithRegexpSplitter() {
		// Notice : complex command should use `Commons CLI`:
		// http://commons.apache.org/cli/
		String input = "ls *.txt |grep abc|\tgrep def";
		List<Cmd> pausedCmd = CmdUtils.pauseCmd(input, "\\|");
		Assert.assertEquals(pausedCmd.get(0).getCmd(), "ls");
		Assert.assertEquals(pausedCmd.get(1).getCmd(), "grep");
		Assert.assertEquals(pausedCmd.get(2).getCmd(), "grep");

		Assert.assertArrayEquals(pausedCmd.get(0).getParams(),
				new String[] { "*.txt" });
		Assert.assertArrayEquals(pausedCmd.get(1).getParams(),
				new String[] { "abc" });
		Assert.assertArrayEquals(pausedCmd.get(2).getParams(),
				new String[] { "def" });
	}

	@Test
	public void testCreateShellCmdWindows() {
		System.setProperty("os.name", "Windows XP");
		String[] expect = new String[] { "cmd", "/C", "ls", "*.txt" };
		testCreateShellCmd(expect);
	}

	@Test
	public void testCreateShellCmdLinux() {
		System.setProperty("os.name", "Redhat");
		String[] expect = new String[] { "/bin/sh", "-c", "ls", "*.txt" };
		testCreateShellCmd(expect);
	}

	@Test
	public void testIsWindows() {
		System.setProperty("os.name", "Windows XP");
		Assert.assertTrue(CmdUtils.isWindows());
		System.setProperty("os.name", "Redhat");
		Assert.assertFalse(CmdUtils.isWindows());
	}

	private void testCreateShellCmd(String[] expect) {
		String input = "ls *.txt |grep abc|\tgrep def";

		List<Cmd> pausedCmd = CmdUtils.pauseCmd(input, "\\|");
		Cmd firstCmd = pausedCmd.get(0);
		List<String> shellCmd = CmdUtils.createShellCmd(firstCmd.getCmd(),
				firstCmd.getParams());

		Assert.assertArrayEquals(expect, ArrayUtils.toArray(shellCmd));
	}
}
