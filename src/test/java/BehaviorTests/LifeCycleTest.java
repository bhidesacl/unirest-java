package BehaviorTests;

import io.github.openunirest.http.Unirest;
import io.github.openunirest.http.async.utils.AsyncIdleConnectionMonitorThread;
import io.github.openunirest.http.options.Options;
import io.github.openunirest.http.utils.SyncIdleConnectionMonitorThread;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static io.github.openunirest.http.options.Option.*;
import static org.mockito.Mockito.*;

public class LifeCycleTest extends BddTest {
    @After @Before
    public void tearDown() throws Exception {
        Options.reset();
    }

    @Test
    public void testShutdown() throws IOException {
        CloseableHttpClient httpc = mock(CloseableHttpClient.class);
        SyncIdleConnectionMonitorThread connMonitor = mock(SyncIdleConnectionMonitorThread.class);
        CloseableHttpAsyncClient asyncClient = mock(CloseableHttpAsyncClient.class);
        when(asyncClient.isRunning()).thenReturn(true);
        AsyncIdleConnectionMonitorThread asyncMonitor = mock(AsyncIdleConnectionMonitorThread.class);

        Options.setOption(HTTPCLIENT, httpc);
        Options.setOption(SYNC_MONITOR, connMonitor);
        Options.setOption(ASYNCHTTPCLIENT, asyncClient);
        Options.setOption(ASYNC_MONITOR, asyncMonitor);

        Unirest.shutdown();

        verify(httpc).close();
        verify(connMonitor).interrupt();
        verify(asyncClient).close();
        verify(asyncMonitor).interrupt();
    }

    @Test
    public void DoesNotBombOnNullOptions() throws IOException {
        Options.setOption(HTTPCLIENT, null);
        Options.setOption(SYNC_MONITOR, null);
        Options.setOption(ASYNCHTTPCLIENT, null);
        Options.setOption(ASYNC_MONITOR, null);

        Unirest.shutdown();
    }

    @Test
    public void willNotShutdownInactiveAsyncClient() throws IOException {
        CloseableHttpAsyncClient asyncClient = mock(CloseableHttpAsyncClient.class);
        when(asyncClient.isRunning()).thenReturn(false);

        Options.setOption(ASYNCHTTPCLIENT, asyncClient);

        Unirest.shutdown();

        verify(asyncClient, never()).close();
    }
}
