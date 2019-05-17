package org.retamia.lalafell.record.utils;

import java.nio.ByteBuffer;

public final class ByteBufferUtils {

    public static ByteBuffer Clone(final ByteBuffer original, boolean consistency)
    {
        final ByteBuffer clone = (original.isDirect()) ? ByteBuffer.allocateDirect(original.capacity()) : ByteBuffer.allocate(original.capacity());
        final ByteBuffer readOnlyCopy = original.asReadOnlyBuffer();

        readOnlyCopy.flip();
        clone.put(readOnlyCopy);
        clone.flip();

        if (consistency) {
            clone.position(original.position());
            clone.limit(original.limit());
            clone.order(original.order());
        }

        return clone;
    }

    public static ByteBuffer Clone(final ByteBuffer original)
    {
        return Clone(original, false);
    }
}
