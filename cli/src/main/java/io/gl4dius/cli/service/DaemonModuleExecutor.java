package io.gl4dius.cli.service;

import io.gl4dius.cli.Gl4diusApplication;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static java.lang.Thread.sleep;

@Slf4j
@Service
@RequiredArgsConstructor
public class DaemonModuleExecutor {

    private final Map<UUID, List<RunningDaemon>> sessionRunningDaemons = new ConcurrentHashMap<>();

    private final ExecutorService daemonModuleExecutorService;

    public void execute(String daemonName, Runnable task) {
        execute(daemonName, task, Duration.ZERO);
    }

    public void execute(String daemonName, Runnable task, Duration delayInterval) {
        var sessionId = Gl4diusApplication.getCurrentSession()
                .orElseThrow(() -> new IllegalStateException("Cannot execute daemon outside of a session"))
                .getId();

        var daemon = RunningDaemon.builder()
                .name(daemonName)
                .build();
        this.sessionRunningDaemons
                .computeIfAbsent(sessionId, id -> Collections.synchronizedList(new ArrayList<>()))
                .add(daemon);

        Future<?> future = this.daemonModuleExecutorService.submit(() -> {
            log.debug("Daemon started: {}, delayInterval: {}", daemonName, delayInterval);
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    task.run();
                    if (!delayInterval.isZero()) {
                        sleep(delayInterval);
                    }
                }
            } catch (Exception e) {
                log.error("Daemon failed: {}", daemonName, e);
                Thread.currentThread().interrupt();
            } finally {
                log.debug("Daemon stopped: {}", daemonName);
            }
        });

        daemon.setFuture(future);
    }

    public void stop(UUID sessionId) {
        var daemons = this.sessionRunningDaemons.remove(sessionId);

        if (daemons == null || daemons.isEmpty()) {
            log.debug("No daemons to stop for session: {}", sessionId);
            return;
        }

        synchronized (daemons) {
            for (RunningDaemon daemon : daemons) {
                Future<?> future = daemon.getFuture();
                if (future != null) {
                    future.cancel(true);
                }

                log.debug("Stop requested for daemon: {}, session: {}", daemon.getName(), sessionId);
            }
        }
    }

    @Getter
    @Setter
    @Builder
    private static final class RunningDaemon {
        private String name;
        private Future<?> future;
    }
}
