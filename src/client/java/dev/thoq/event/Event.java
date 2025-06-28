/*
 * Copyright (c) Rye Client 2025-2025.
 *
 * This file belongs to Rye Client,
 * an open-source Fabric injection client.
 * Rye GitHub: https://github.com/RyeClient/rye-v1.git
 *
 * This project (and subsequently, its files) are all licensed under the MIT License.
 * This project should have come with a copy of the MIT License.
 * If it did not, you may obtain a copy here:
 * MIT License: https://opensource.org/license/mit
 *
 */

package dev.thoq.event;

public class Event {

    private boolean canceled;
    private boolean pre = true;

    public boolean isCanceled() {
        return canceled;
    }

    public final boolean isPre() {
        return pre;
    }

    public final boolean isPost() {
        return !pre;
    }

    public final void setPost() {
        pre = false;
    }

    public final void cancel() {
        canceled = true;
    }
}