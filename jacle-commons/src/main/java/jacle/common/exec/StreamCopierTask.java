package jacle.common.exec;

import jacle.common.io.RuntimeIOException;

import java.io.InputStream;
import java.io.OutputStream;


/**
 * Pumps an input stream to an output stream until nothing is available. Based
 * loosely upon the Apache Common Exec class, StreamPumper.
 */
public class StreamCopierTask implements Runnable {

    private static int BUFFER_SIZE = 1000;   
    
    private final InputStream inputStream;
    private final OutputStream outputStream;
    
    /**
     * Constructor. Does not take ownership of the provided streams. I.E., this
     * object will not close the streams.
     */
    public StreamCopierTask(InputStream input, OutputStream output) {
        this.inputStream = input;
        this.outputStream = output;
    }
    
    /**
     * Copies all bytes available from the {@link InputStream} to the
     * {@link OutputStream}. If the stream is delayed, due to network traffic,
     * etc, this method will block until completion or a connection failure.
     */
    @Override
    public void run() {
        final byte[] buf = new byte[BUFFER_SIZE];
        int length;
        try {
            while ((length = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, length);
            }
        } catch (Exception e) {
            throw new RuntimeIOException("Failed to pump stream", e);
        }
    }
}
