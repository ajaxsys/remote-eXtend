package exec.shell.util;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

public class CmdManagerTest {

	@Test
	public void testIsWhiteList() {
		Set<String> cmds = new HashSet<String>();
		cmds.add("ls");
		cmds.add("tail");
		Assert.assertTrue(CmdManager.isWhiteList(cmds));
		cmds.add("java -jar some-dangerous.jar");
		Assert.assertFalse(CmdManager.isWhiteList(cmds));
	}

}
