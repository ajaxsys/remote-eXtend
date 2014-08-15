package exec.shell.util;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class ArrayUtilsTest {

	@Test
	public void testConcat() {
		final String[] expect = new String[] { "a", "b", "c" };
		final String[] concatResult = ArrayUtils.concat(new String[] { "a" },
				new String[] { "b", "c" });
		Assert.assertArrayEquals(expect, concatResult);
	}

	@Test
	public void testToArray() {
		final String[] expect = new String[] { "a", "b", "c" };
		List<String> list = new ArrayList<String>();
		list.add("a");
		list.add("b");
		list.add("c");

		Assert.assertArrayEquals(expect, ArrayUtils.toArray(list));
	}
}
