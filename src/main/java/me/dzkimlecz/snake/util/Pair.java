package me.dzkimlecz.snake.util;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public final class Pair<E> implements Collection<E> {

    private final E first;
    private final E second;
    private Object[] content;

    private Pair(E first, E second) {
        this.first = first;
        this.second = second;
    }

    public static <T> Pair<T> of(@NotNull T e, @NotNull T e1) {
        return new Pair<>(e, e1);
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pair)) return false;
        Pair<?> pair = (Pair<?>) o;
        return first.equals(pair.first) && second.equals(pair.second);
    }

    @Override public String toString() {
        return "{" + first + " to " + second + "}";
    }

    @Override public int hashCode() {
        return Objects.hash(first, second);
    }

    public @NotNull E first() {
        return first;
    }

    public @NotNull E second() {
        return second;
    }

    @Override public int size() {
        return 2;
    }

    @Override public boolean isEmpty() {
        return false;
    }

    @Override public boolean contains(Object o) {
        return first.equals(o) || second.equals(o);
    }

    @Override public @NotNull Iterator<E> iterator() {
        return new Iterator<>() {
            private final AtomicInteger cursor = new AtomicInteger(-1);
            @Override public boolean hasNext() {
                return cursor.get() != 1;
            }

            @Override public E next() {
                switch(cursor.incrementAndGet()) {
                    case 0:
                        return first;
                    case 1:
                        return second;
                    default:
                        throw new NoSuchElementException();
                }
            }

        };
    }

    @Override public Object[] toArray() {
        return Objects.requireNonNullElseGet(content, () -> (content = new Object[]{first, second}));
    }

    @Override public <T> T @NotNull [] toArray(T @NotNull [] a) {
        if (a.length < 2) {
            try {
                @SuppressWarnings("unchecked")
                final var content = (T[]) Arrays.copyOf(toArray(), 2, a.getClass());
                return content;
            } catch (ClassCastException e) {
                throw new ArrayStoreException("Can't store content in array of this type.");
            }
        }
        System.arraycopy(toArray(), 0, a, 0, 2);
        if (a.length > 2)
            a[2] = null;
        return a;
    }

    @Override public boolean containsAll(Collection<?> c) {
        return c.stream().allMatch(this::contains);
    }

    @Override public boolean add(E e) {
        throw new UnsupportedOperationException("Pairs are immutable");
    }

    @Override public boolean remove(Object o) {
        throw new UnsupportedOperationException("Pairs are immutable");
    }

    @Override public boolean addAll(@NotNull Collection<? extends E> c) {
        throw new UnsupportedOperationException("Pairs are immutable");
    }

    @Override public boolean removeAll(@NotNull Collection<?> c) {
        throw new UnsupportedOperationException("Pairs are immutable");
    }

    @Override public boolean retainAll(@NotNull Collection<?> c) {
        throw new UnsupportedOperationException("Pairs are immutable");
    }

    @Override public void clear() {
        throw new UnsupportedOperationException("Pairs are immutable");
    }

}
