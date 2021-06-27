package me.dzkimlecz.snake.util;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;

public class Pair<E> implements Collection<E> {

    private final E first;
    private final E second;
    private final Object[] content;

    public Pair(@NotNull E first, @NotNull E second) {
        this.first = first;
        this.second = second;
        content = new Object[] {first, second};
    }

    public @NotNull E first() {
        return first;
    }

    public @NotNull E second() {
        return second;
    }

    @Override
    public int size() {
        return 2;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean contains(Object o) {
        return first.equals(o) || second.equals(o);
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<>() {
            private final AtomicInteger cursor = new AtomicInteger(-1);
            @Override
            public boolean hasNext() {
                return cursor.get() != 1;
            }

            @Override
            public E next() {
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

    @Override
    public Object[] toArray() {
        return content;
    }

    @Override
    public <T> T[] toArray(T @NotNull [] a) {
        if (a.length < 2) {
            try {
                @SuppressWarnings("unchecked")
                final var content = (T[]) Arrays.copyOf(this.content, 2, a.getClass());
                return content;
            } catch (ClassCastException e) {
                throw new ArrayStoreException("Can't store content in array of this type.");
            }
        }
        System.arraycopy(content, 0, a, 0, 2);
        if (a.length > 2)
            a[2] = null;
        return a;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return c.stream().allMatch(this::contains);
    }

    @Override
    public boolean add(E e) {
        throw new UnsupportedOperationException("Pairs are immutable");
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("Pairs are immutable");
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends E> c) {
        throw new UnsupportedOperationException("Pairs are immutable");
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        throw new UnsupportedOperationException("Pairs are immutable");
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        throw new UnsupportedOperationException("Pairs are immutable");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Pairs are immutable");
    }

}