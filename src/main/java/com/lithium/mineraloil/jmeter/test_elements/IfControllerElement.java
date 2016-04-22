package com.lithium.mineraloil.jmeter.test_elements;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.experimental.Builder;
import org.apache.jmeter.control.IfController;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.testelement.TestElement;

/**
 * Created by viren.thakkar on 4/21/16.
 */
@Getter
@Builder
public class IfControllerElement extends JMeterStepImpl<IfControllerElement>  {

    private String name;
    private String condition;

   public IfController getIfController(){

       Preconditions.checkNotNull(name);
       IfController ifController = new IfController();
       ifController.setName(name);
       ifController.setCondition(condition);
       ifController.setEvaluateAll(true);

       return ifController;
   }
    @Override
    public TestElement getTestElement() {
        return getIfController();
    }
}
