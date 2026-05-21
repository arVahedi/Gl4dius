package io.gl4dius.cli.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class DaemonExecutorConfig {

    @Bean(destroyMethod = "shutdownNow")
    public ExecutorService daemonModuleExecutorService() {
        return Executors.newCachedThreadPool(runnable -> {
            Thread thread = new Thread(runnable);
            thread.setName("gl4dius-daemon-" + thread.threadId());
            //In case of any changes in network state, iptables, ARP behavior, subprocesses, sockets, etc.,
            //we want explicit cleanup instead of the JVM silently killing the thread.
            thread.setDaemon(false);
            return thread;
        });
    }
}
