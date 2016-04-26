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
import java.util.Properties;

/**
 * Created by viren.thakkar on 12/23/15.
 */
public class LiaPerfTestWithLogins extends  AbstractPerfTestWithLogins{

    @Test
    public void remoteTest() {
        JMeterRunner jmeter = new JMeterRunner("httpRequest-perftest-logins");
        /*
          Get all vm arguments here.
         */

        String remoteJmeterInstance = System.getProperty("remoteJmeterHost");
        String elasticSearchCluster = System.getProperty("elasticSearchClusterName");
        String elasticSearchHost = System.getProperty("elasticSearchHost");
        int elasticSearchPort = Integer.parseInt(System.getProperty("elasticSearchPort"));
        String testRun = System.getProperty("testRun");
        String release = System.getProperty("release");
        String revision = System.getProperty("revision");
        String community = System.getProperty("community");
        String code_branch = System.getProperty("codeBranch");


        /*
          This is to pass extra parameters (JmeterProperties) to jmeter.
         */
        Properties properties = new Properties();
        properties.put("elasticSearchClusterName",elasticSearchCluster);
        properties.put("elasticSearchHost",elasticSearchHost);
        properties.put("elasticSearchPort",String.valueOf(elasticSearchPort));
        properties.put("testRun",testRun);
        properties.put("release",release);
        properties.put("revision",revision);
        properties.put("community",community);

        if (code_branch==null || code_branch.isEmpty()) {
            properties.put("codeBranch", "");
        }else {
            properties.put("codeBranch", code_branch);
        }

        jmeter.addExtraJmeterProperties(properties);

        prepareScenario(jmeter);

        jmeter.addElasticSearchMapping(elasticSearchCluster, elasticSearchHost, elasticSearchPort);
        jmeter.addElasticSearchListener(elasticSearchCluster, testRun, elasticSearchHost, elasticSearchPort);

        /* Now run the jmeter client which will run script on remote instance */
        List<String> remoteHosts = Arrays.asList(remoteJmeterInstance.split(","));

        jmeter.remoteRun(remoteHosts);
    }
}
