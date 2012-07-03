package cn.jachohx.crawler.util;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.util.ByteArrayBuffer;

public class EntityUtils {
	/**
     * Read the contents of an entity and return it as a byte array.
     *
     * @param entity
     * @return byte array containing the entity content. May be null if
     *   {@link HttpEntity#getContent()} is null.
     * @throws IOException if an error occurs reading the input stream
     * @throws IllegalArgumentException if entity is null or if content length > Integer.MAX_VALUE
     */
    public static byte[] toByteArray(final HttpEntity entity) throws IOException {
        if (entity == null) {
            throw new IllegalArgumentException("HTTP entity may not be null");
        }
        InputStream instream = entity.getContent();
        if (instream == null) {
            return null;
        }
        try {
            if (entity.getContentLength() > Integer.MAX_VALUE) {
                throw new IllegalArgumentException("HTTP entity too large to be buffered in memory");
            }
            int i = (int)entity.getContentLength();
            if (i < 0) {
                i = 4096;
            }
            ByteArrayBuffer buffer = new ByteArrayBuffer(i);
            byte[] tmp = new byte[4096];
            int l;
            while((l = instream.read(tmp)) != -1) {
                buffer.append(tmp, 0, l);
            }
            return buffer.toByteArray();
        } finally {
            instream.close();
        }
    }
    
    /**
     * Read the contents of an entity and return it as a byte array.
     *
     * @param entity
     * @return byte array containing the entity content. May be null if
     *   {@link HttpEntity#getContent()} is null.
     * @throws IOException if an error occurs reading the input stream
     * @throws IllegalArgumentException if entity is null or if content length > Integer.MAX_VALUE
     */
    public static byte[] toByteArray(final InputStream instream) throws IOException {
        if (instream == null) {
            return null;
        }
        try {
            ByteArrayBuffer buffer = new ByteArrayBuffer(4096);
            byte[] tmp = new byte[4096];
            int l;
            while((l = instream.read(tmp)) != -1) {
                buffer.append(tmp, 0, l);
            }
            return buffer.toByteArray();
        } finally {
            instream.close();
        }
    }
}
