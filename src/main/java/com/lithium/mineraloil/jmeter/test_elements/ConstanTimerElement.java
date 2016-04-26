package com.lithium.mineraloil.jmeter.test_elements;

import lombok.Getter;
import lombok.experimental.Builder;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.timers.ConstantTimer;

/**
 * Created by viren.thakkar on 4/24/16.
 */
@Builder
@Getter

public class ConstanTimerElement extends JMeterStepImpl<ConstanTimerElement> {

    private String delay="0";

    @Override
    public ConstantTimer  getTestElement() {
        ConstantTimer constantTimer = new ConstantTimer();
        constantTimer.setDelay(delay);
        return constantTimer;
    }
}
