package org.retamia.lalafell.record.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public final class NioBufferUtils {

    public static final int BYTES_PER_FLOAT = 4;

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

    public static ByteBuffer FromByte(final byte []values) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(values.length)
                .order(ByteOrder.nativeOrder())
                .put(values);

        byteBuffer.position(0);
        return byteBuffer;
    }

    public static FloatBuffer FromFloat(final float []values) {
        FloatBuffer buffer = ByteBuffer.allocateDirect(values.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(values);
        buffer.position(0);
        return buffer;
    }
}
