package dev.it.api.service.v4;

public interface PostFetch<T> {
    T call(T object);
}
