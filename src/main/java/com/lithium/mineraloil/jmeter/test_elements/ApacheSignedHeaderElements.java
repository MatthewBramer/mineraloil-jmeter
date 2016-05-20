package com.lithium.mineraloil.jmeter.test_elements;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import lombok.experimental.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.jmeter.protocol.http.control.Header;
import org.joda.time.Instant;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Builder
@Slf4j
public class ApacheSignedHeaderElements {
    private final String secret;
    private final String key;
    private final HTTPSamplerElement request;

    public List<Header> getSignedHeaders() {
        long timestamp = Instant.now().getMillis();

        Header apiKeyHeader = new Header();
        apiKeyHeader.setName("X-Auth-ApiKey");
        apiKeyHeader.setValue(key);

        Header signatureHeader = new Header();
        signatureHeader.setName("X-Auth-Signature");
        signatureHeader.setValue(getSignature(getFingerprint(timestamp), secret));

        Header timeStampheader = new Header();
        timeStampheader.setName("X-Auth-Timestamp");
        timeStampheader.setValue(String.valueOf(timestamp));

        return Lists.newArrayList(apiKeyHeader, signatureHeader, timeStampheader);
    }

    public static String getSignature(String fingerprint, String secret) {
        try {
            Mac e = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(Charsets.UTF_8), "HmacSHA256");
            e.init(secretKey);
            e.update(fingerprint.getBytes(Charsets.UTF_8));
            String hash = Base64.encodeBase64String(e.doFinal());
            log.info(String.format("Generated Signature for fingerprint '%s' secret '%s': %s", fingerprint, secret, hash));
            return hash;
        } catch (InvalidKeyException | NoSuchAlgorithmException var5) {
            throw Throwables.propagate(var5);
        }
    }

    public String getFingerprint(long timestamp) {
        return String.format("%s|%s|%s|%s", timestamp, request.getMethod(), request.getPath(), request.getBody());
    }

}
