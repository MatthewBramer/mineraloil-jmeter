package com.lithium.mineraloil.jmeter.test_elements;

import lombok.Getter;
import lombok.experimental.Builder;
import org.apache.jmeter.sampler.TestAction;
import org.apache.jmeter.testelement.TestElement;

/**
 * Created by viren.thakkar on 4/25/16.
 */

@Builder
@Getter

public class TestActionElement extends JMeterStepImpl<TestActionElement> {

    private String name;
    @Override
    public TestAction getTestElement() {

        TestAction testAction = new TestAction();
        testAction.setName(name);
        return testAction;
    }
}
