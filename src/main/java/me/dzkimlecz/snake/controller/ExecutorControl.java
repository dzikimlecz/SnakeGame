package me.dzkimlecz.snake.controller;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 *  Class providing all functionalities of ExecutorService <b>except of executing and scheduling tasks</b>.
 *  Used to control executor from outside the object
 */
class ExecutorControl implements ExecutorService {
    private final ExecutorService controlled;

    ExecutorControl(ExecutorService controlled) {
        this.controlled = controlled;
    }

    @Override
    public void shutdown() {
        controlled.shutdown();
    }

    @NotNull
    @Override
    public List<Runnable> shutdownNow() {
        return controlled.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return controlled.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return controlled.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, @NotNull TimeUnit unit) throws InterruptedException {
        return controlled.awaitTermination(timeout, unit);
    }

    @NotNull
    @Override
    public <T> Future<T> submit(@NotNull Callable<T> task) {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public <T> Future<T> submit(@NotNull Runnable task, T result) {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public Future<?> submit(@NotNull Runnable task) {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public <T> List<Future<T>> invokeAll(@NotNull Collection<? extends Callable<T>> tasks) {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public <T> List<Future<T>> invokeAll(@NotNull Collection<? extends Callable<T>> tasks, long timeout, @NotNull TimeUnit unit) {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public <T> T invokeAny(@NotNull Collection<? extends Callable<T>> tasks) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T invokeAny(@NotNull Collection<? extends Callable<T>> tasks, long timeout, @NotNull TimeUnit unit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void execute(@NotNull Runnable command) {
        throw new UnsupportedOperationException();
    }

}