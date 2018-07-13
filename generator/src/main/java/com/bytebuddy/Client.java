package com.bytebuddy;

public interface Client<T, U> {
    public T invoke(U input);
}
