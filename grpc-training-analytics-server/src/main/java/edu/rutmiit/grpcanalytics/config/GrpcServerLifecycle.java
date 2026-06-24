package edu.rutmiit.grpcanalytics.config;

import edu.rutmiit.grpcanalytics.service.WorkoutAnalyticsServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
public class GrpcServerLifecycle implements SmartLifecycle {
    private static final Logger log = LoggerFactory.getLogger(GrpcServerLifecycle.class);

    @Value("${grpc.server.port:9090}")
    private int grpcPort;

    private Server server;
    private boolean running = false;

    /**
     * Запуск gRPC-сервера.
     * <p>
     * ServerBuilder — фабрика gRPC:
     * - forPort(port) — на каком порту слушать
     * - addService(serviceImpl) — регистрация реализации сервиса
     * - build() — создание сервера (без запуска)
     * - start() — начало приёма запросов
     * <p>
     * Можно зарегистрировать несколько сервисов через несколько addService().
     */
    @Override
    public void start() {
        try {
            server = ServerBuilder.forPort(grpcPort)
                    .addService(new WorkoutAnalyticsServiceImpl())
                    .build()
                    .start();
            running = true;
            log.info("gRPC-сервер запущен на порту {}", grpcPort);
            log.info("Сервис: WorkoutAnalytics.analyzeWorkout()");
        } catch (IOException e) {
            throw new RuntimeException("Не удалось запустить gRPC-сервер на порту " + grpcPort, e);
        }

    }

    /**
     * Graceful shutdown — корректная остановка gRPC-сервера.
     * <p>
     * shutdown() завершает приём новых запросов, но ждёт завершения текущих.
     * В проде добавляют awaitTermination() с таймаутом,
     * а затем shutdownNow() для принудительной остановки.
     */
    @Override
    public void stop() {
        if (server != null) {
            log.info("Остановка gRPC-сервера...");
            server.shutdown();
            running = false;
            log.info("gRPC-сервер остановлен");
        }
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    /**
     * Фаза запуска — чем меньше число, тем раньше стартует.
     * Integer.MAX_VALUE — стартуем последними (после Spring контекста).
     */
    @Override
    public int getPhase() {
        return Integer.MAX_VALUE;
    }
}
