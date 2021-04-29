package de.terrarier.terracloud.lib;

import java.util.Objects;
import java.util.UUID;

public final class SimpleUUID {

    private final byte[] data;

    public SimpleUUID() {
        final String part = UUID.randomUUID().toString().split("-", 2)[0];
        this.data = part.getBytes();
    }

    public SimpleUUID(byte[] data) {
        this.data = data;
    }

    public byte[] getBytes() {
        return this.data;
    }

    public String toString() {
        return new String(data);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleUUID that = (SimpleUUID) o;
        return Objects.equals(toString(), that.toString());
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }
}
