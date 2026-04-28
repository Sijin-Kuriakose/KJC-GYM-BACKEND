package in.edu.kristujayanti.util;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.streams.ReadStream;
import java.io.IOException;
import java.io.InputStream;

public class VertxAsyncInputStreamUtil implements ReadStream<Buffer> {
    private final Vertx vertx;
    private final InputStream inputStream;
    private static final int BUFFER_SIZE = 8192; // 8 KB buffer size
    private Handler<Buffer> dataHandler;
    private Handler<Void> endHandler;
    private Handler<Throwable> exceptionHandler;

    public VertxAsyncInputStreamUtil(Vertx vertx, InputStream inputStream) {
        this.vertx = vertx;
        this.inputStream = inputStream;
        scheduleRead();
    }


    private void scheduleRead() {
        vertx.executeBlocking(() -> {
            try {
                byte[] buffer = new byte[BUFFER_SIZE];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    if (dataHandler != null) {
                        Buffer vertxBuffer = Buffer.buffer().appendBytes(buffer, 0, bytesRead);
                        vertx.runOnContext(v -> dataHandler.handle(vertxBuffer));
                    }
                }
                if (endHandler != null) {
                    vertx.runOnContext(v -> endHandler.handle(null));
                }
            } catch (IOException e) {
                if (exceptionHandler != null) {
                    vertx.runOnContext(v -> exceptionHandler.handle(e));
                }
            } finally {
                try {
                    inputStream.close();
                } catch (IOException ignored) {
                }
            }
            return null; // Required because executeBlocking with Callable<T> needs a return value
        });
    }

    @Override
    public ReadStream<Buffer> handler(Handler<Buffer> handler) {
        this.dataHandler = handler;
        return this;
    }

    @Override
    public ReadStream<Buffer> pause() {
        // No-op for now
        return this;
    }

    @Override
    public ReadStream<Buffer> resume() {
        // No-op for now
        return this;
    }

    @Override
    public ReadStream<Buffer> fetch(long amount) {
        // No-op for now
        return this;
    }

    @Override
    public ReadStream<Buffer> endHandler(Handler<Void> handler) {
        this.endHandler = handler;
        return this;
    }

    @Override
    public ReadStream<Buffer> exceptionHandler(Handler<Throwable> handler) {
        this.exceptionHandler = handler;
        return this;
    }
}
