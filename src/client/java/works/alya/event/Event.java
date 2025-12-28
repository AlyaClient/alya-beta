/*
 * Copyright (c) Alya Client 2024 - 2025.
 *
 * This file belongs to Alya Client,
 * an open-source Fabric injection client.
 *     <https://github.com/AlyaClient/alya-beta.git>
 *
 * THIS PROJECT DOES NOT HAVE A WARRANTY.
 * This file is provided with a free license in hopes that it will be useful.
 *
 * Alya Client is licensed under the MIT License.
 * Alya Client should have come with a copy of the MIT License.
 * If it did not, you may obtain a copy here:
 *     <https://opensource.org/license/mit>
 *
 */

package works.alya.event;

@SuppressWarnings("unused")
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