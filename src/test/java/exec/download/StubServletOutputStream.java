package exec.download;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;

public class StubServletOutputStream extends ServletOutputStream {
    public ByteArrayOutputStream baos = new ByteArrayOutputStream();
    private File tmpFile;

    public StubServletOutputStream(File tmp) {
        this.tmpFile = tmp;
    }

    public void write(int i) throws IOException {
        baos.write(i);
    }

    @Override
    public void flush() throws IOException {
        FileOutputStream fos = new FileOutputStream (tmpFile);
        try {
            baos.writeTo(fos);
        } catch(IOException ioe) {
            // Handle exception here
            ioe.printStackTrace();
        } finally {
            fos.close();
        }
    }


}