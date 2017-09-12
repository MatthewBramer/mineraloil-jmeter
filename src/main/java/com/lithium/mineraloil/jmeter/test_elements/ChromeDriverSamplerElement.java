package com.lithium.mineraloil.jmeter.test_elements;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.experimental.Builder;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jmeter.protocol.http.control.gui.HttpTestSampleGui;
import org.apache.jmeter.protocol.http.gui.HTTPArgumentsPanel;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.protocol.http.util.HTTPArgument;
import org.apache.jmeter.protocol.java.sampler.JavaSampler;
import org.apache.jmeter.testelement.TestElement;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
public class ChromeDriverSamplerElement extends JMeterStepImpl<ChromeDriverSamplerElement> {


    public TestElement getTestElement() {


        JavaSampler javaSampler = new JavaSampler();

        javaSampler.setName("JavaRequest");
        javaSampler.setEnabled(true);
        javaSampler.setClassname("com.lithium.mineraloil.jmeter.test_elements.ChromeDriverClient");

        return javaSampler;
    }


}
