package com.lithium.liatests;

import com.lithium.mineraloil.jmeter.JMeterRunner;
import com.lithium.mineraloil.jmeter.test_elements.*;
import org.apache.jmeter.protocol.http.control.Header;
import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.protocol.http.util.HTTPArgument;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by viren.thakkar on 12/23/15.
 */
public class LiaWarmpupPerfTestWithLogins_localrun extends  AbstractPerfTestWithLogins{

    @Test
    public void remoteTest() {

        JMeterRunner jmeter = new JMeterRunner("httpRequest-warmpup");
        prepareScenario(jmeter);

        String remoteJmeterInstance = System.getProperty("remoteJmeterHost");

        // jmeter.addElasticSearchMapping(elasticSearchCluster, elasticSearchHost, elasticSearchPort);
        //jmeter.addElasticSearchListener(elasticSearchCluster, testRun, elasticSearchHost, elasticSearchPort);

        /* Now run the jmeter client which will run script on remote instance */
        List<String> remoteHosts = Arrays.asList(remoteJmeterInstance.split(","));

        jmeter.run();
    }
}
