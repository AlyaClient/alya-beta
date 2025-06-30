/*
 * Copyright (c) Rye Client 2024-2025.
 *
 * This file belongs to Rye Client,
 * an open-source Fabric injection client.
 * Rye GitHub: https://github.com/RyeClient/rye-v1.git
 *
 * THIS PROJECT DOES NOT HAVE A WARRANTY.
 *
 * Rye (and subsequently, its files) are all licensed under the MIT License.
 * Rye should have come with a copy of the MIT License.
 * If it did not, you may obtain a copy here:
 * MIT License: https://opensource.org/license/mit
 *
 */

package dev.thoq.utilities.types;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

@SuppressWarnings("unused")
public final class ImmutablePair<A, B> extends Pair<A, B> {
    private final A a;
    private final B b;

    ImmutablePair(A a, B b) {
        this.a = a;
        this.b = b;
    }

    public static <A, B> ImmutablePair<A, B> of(A a, B b) {
        return new ImmutablePair<>(a, b);
    }

    public Pair<A, A> pairOfFirst() {
        return Pair.of(a);
    }

    public Pair<B, B> pairOfSecond() {
        return Pair.of(b);
    }

    @Override
    public A getFirst() {
        return a;
    }

    @Override
    public B getSecond() {
        return b;
    }


    @Override
    public <R> R apply(BiFunction<? super A, ? super B, ? extends R> func) {
        return func.apply(a, b);
    }

    @Override
    public void use(BiConsumer<? super A, ? super B> func) {
        func.accept(a, b);
    }
}
