package com.lithium.mineraloil.jmeter.test_elements;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.experimental.Builder;
import org.apache.jmeter.protocol.http.control.CookieManager;
import org.apache.jmeter.testelement.TestElement;

/**
 * Created by viren.thakkar on 4/21/16.
 */
@Builder
@Getter
public class HttpCookieManagerElement extends JMeterStepImpl<HttpCookieManagerElement> {
    private String name;

    @Override
    public CookieManager getTestElement() {
        return getHttpCookieManager();
    }

    public CookieManager getHttpCookieManager() {
        Preconditions.checkNotNull(name);
        CookieManager cookieManager = new CookieManager();
        cookieManager.setName(name);
        return cookieManager;

    }
}
