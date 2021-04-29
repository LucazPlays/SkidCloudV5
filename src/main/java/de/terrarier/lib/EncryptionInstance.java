package de.terrarier.lib;

import java.security.PrivateKey;
import java.security.PublicKey;

public final class EncryptionInstance {

    private final String algorithm;
    private final int length;
    private final PrivateKey privateKey;
    private final PublicKey internalPublicKey;
    private PublicKey externalPublicKey;

    public EncryptionInstance(String algorithm, int length, PrivateKey privateKey, PublicKey publicKey) {
        this.algorithm = algorithm;
        this.length = length;
        this.privateKey = privateKey;
        this.internalPublicKey = publicKey;
    }

    public String getAlgorithm() {
        return this.algorithm;
    }

    public int getLength() {
        return this.length;
    }

    public PrivateKey getPrivateKey() {
        return this.privateKey;
    }

    public PublicKey getInternalPublicKey() {
        return this.internalPublicKey;
    }

    public PublicKey getExternalPublicKey() {
        return this.externalPublicKey;
    }

    public void setExternalPublicKey(PublicKey externalPublicKey) {
        this.externalPublicKey = externalPublicKey;
    }

}
