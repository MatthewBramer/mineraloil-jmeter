package com.lithium.mineraloil.jmeter.test_elements;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.experimental.Builder;
import org.apache.jmeter.protocol.http.control.CacheManager;
import org.apache.jmeter.testelement.TestElement;

/**
 * Created by viren.thakkar on 4/26/16.
 */
@Builder
@Getter
public class HttpCacheManagerElement extends JMeterStepImpl<HttpCacheManagerElement> {
    private String name;
    @Override
    public CacheManager getTestElement() {
        Preconditions.checkNotNull(name);
        CacheManager cacheManager = new CacheManager();
        cacheManager.setName(name);
        cacheManager.setUseExpires(true);
        return cacheManager;
    }
}
