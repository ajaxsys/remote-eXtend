package exec.download;

import static org.junit.Assert.*;

import org.junit.Test;

public class UConfigTest {

	@Test
	public void testGetProperty() {
		assertTrue(UConfig.getProperty("SECURE_KEY").length() > 1);
	}

}
