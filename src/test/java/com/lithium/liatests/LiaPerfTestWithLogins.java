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
public class LiaPerfTestWithLogins {

    @Test
    public void remoteTest() {
        /*
          Get all vm arguments here.
         */
        String protocol = System.getProperty("protocol", "http");
        int port = Integer.parseInt(System.getProperty("port", "80"));
        String domain = System.getProperty("host");
        String urlPath = System.getProperty("path", "/");
        int threads = Integer.parseInt(System.getProperty("users"));
        int rampup = Integer.parseInt(System.getProperty("rampup"));
        String remoteJmeterInstance = System.getProperty("remoteJmeterHost");
        String csvFile = System.getProperty("csvFileLocation");
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

        JMeterRunner jmeter = new JMeterRunner("httpRequest-perftest");
        jmeter.addExtraJmeterProperties(properties);

        /* Create header element */
        HeaderManager headerManager = new HeaderManager();
        Header header = new Header("User-Agent", "${useragent}");
        headerManager.add(header);

        /* IfController to check whether user is anonymous or not */

        IfControllerElement ifControllerAnonymousUser = IfControllerElement.builder().name("AnonymousUser").condition("${user_id}<0").build();


        /* Create HTTP Sampler for anonyous call */
        HTTPSamplerElement AnoynoumUserRequest = HTTPSamplerElement.builder()
                .domain(domain)
                .port(port)
                .protocol(protocol)
                .path("${url}")
                .method("GET")
                .implementation("HttpClient4").headerManager(headerManager)
                .build();


        ifControllerAnonymousUser.addStep(AnoynoumUserRequest);

        /* IfController to check whether user is actual  */

        IfControllerElement ifControllerActualUser = IfControllerElement.builder().name("ActualUser").condition("${user_id}>0").build();

        IfControllerElement ifControllerAnonToUser = IfControllerElement.builder().name("AnonToUser").condition("\"${COOKIE_LithiumUserInfo}\"==\"\\${COOKIE_LithiumUserInfo}\"").build();

//        ifControllerActualUser.addStep(ifControllerAnonToUser);

        IfControllerElement ifControllerOneToAnotherUser = IfControllerElement.builder().name("OneToAnother").condition("\"${COOKIE_LithiumUserInfo}\"!=\"\\${COOKIE_LithiumUserInfo}\" && ${COOKIE_LithiumUserInfo}!=${user_id}").build();

        //       ifControllerActualUser.addStep(ifControllerOneToAnotherUser);




 /* Create HTTP Sampler for loading  login page  */
        HTTPSamplerElement loadLoginPage = HTTPSamplerElement.builder()
                .domain(domain)
                .port(port)
                .protocol(protocol)
                .path("/t5/user/userloginpage")
                .method("GET")
                .implementation("HttpClient4").headerManager(headerManager)
                .build();

        /* All regex calls to extract dynamica values from previous request page for login purpose*/

        RegularExpressionExtractorElement regex_lia_action_token = RegularExpressionExtractorElement.builder().referenceName("lia_action_token").regex("value=\"([^\"]+)\" name=\"lia-action-token\"").template("$1$").matchNumber("1").name("regex_lia_action_token").build();
        // regularExpressionExtractorElement.getTestElement().setUseField(regularExpressionExtractorElement.getTestElement().USE_BODY);
        regex_lia_action_token.getTestElement().useBody();

        RegularExpressionExtractorElement regex_form_data = RegularExpressionExtractorElement.builder().name("regex_form_data").referenceName("form_data").regex("value=\"([^\"]+)\" name=\"t:formdata\"").template("$1$").matchNumber("1").build();
        regex_form_data.getTestElement().useBody();


        RegularExpressionExtractorElement regex_ac_data = RegularExpressionExtractorElement.builder().name("regex_ac_data").referenceName("ac_data").regex("value=\"([^\"]+)\" name=\"t:ac\"").template("$1$").matchNumber("1").build();
        regex_ac_data.getTestElement().useBody();


        /* Extrat information from loaded login page before login */
        loadLoginPage.addStep(regex_lia_action_token);
        loadLoginPage.addStep(regex_form_data);
        loadLoginPage.addStep(regex_ac_data);


        /* Create HttpSampler Object */
        List<HTTPArgument> anon_to_user_argumentList = new ArrayList<>();

        anon_to_user_argumentList.add(new HTTPArgument("t:ac","${ac_data}"));
        anon_to_user_argumentList.add(new HTTPArgument("dest_url",protocol+"://"+domain+":"+port+"${url}"));
        anon_to_user_argumentList.add(new HTTPArgument("liaFormContentKey","UserLoginPage::userloginform.form.form:"));
        anon_to_user_argumentList.add(new HTTPArgument("t:formdata","${form_data}"));
        anon_to_user_argumentList.add(new HTTPArgument("lia-action-token","${lia_action_token}"));
        anon_to_user_argumentList.add(new HTTPArgument("form_UID","form"));
        anon_to_user_argumentList.add(new HTTPArgument("form_instance_key",""));
        anon_to_user_argumentList.add(new HTTPArgument("login","${loginname}"));
        anon_to_user_argumentList.add(new HTTPArgument("password", "abcabcabc"));
        anon_to_user_argumentList.add(new HTTPArgument("submitContextX", "Submit"));
        anon_to_user_argumentList.add(new HTTPArgument("submitContext", "Sign In"));


        HeaderManager headerMgrAnonToUser = new HeaderManager();

        headerManager.add(new Header("Referer", protocol + "://" + domain + ":" + port + "/t5/user/userloginpage?dest_url=" + protocol + "://" + domain + ":" + port + "${url}"));

        HTTPSamplerElement login_user = HTTPSamplerElement.builder()
                .domain(domain)
                .port(port)
                .protocol(protocol)
                .path("/t5/user/loginpage.userloginform.form.form.form")
             .method("POST")
                .implementation("HttpClient4")
                .followRedirects(true)
                .useKeepAlive(true)
                .doMultiPartPost(true)
                .arguments(anon_to_user_argumentList)
                .headerManager(headerMgrAnonToUser)
                .build();

        ((HTTPSamplerProxy)login_user.getTestElement()).setDoBrowserCompatibleMultipart(true);



        HTTPSamplerElement simplyGetUrlCall = HTTPSamplerElement.builder()
                .domain(domain)
                .port(port)
                .protocol(protocol)
                .path("${url}")
                .method("GET")
                .implementation("HttpClient4").headerManager(headerManager)
                .followRedirects(true)
                .useKeepAlive(true)
                .doMultiPartPost(true)
                .arguments(anon_to_user_argumentList)

                .build();

        ((HTTPSamplerProxy)login_user.getTestElement()).setDoBrowserCompatibleMultipart(true);

/* This is for anonymous user to login user.Under actual user condition add annon to use condition and then add loginpage get call, login user and then make normal ${url} call */
        ifControllerActualUser.addStep(ifControllerAnonToUser);
        ifControllerAnonToUser.addReportableStep(loadLoginPage);
        ifControllerAnonToUser.addReportableStep(login_user);
        ifControllerAnonToUser.addReportableStep(simplyGetUrlCall);

/* This is for one user to another user.Under actual user condition add one to another user condition and then add loginpage get call, login user and then make normal ${url} call */

        ifControllerActualUser.addStep(ifControllerOneToAnotherUser);
        ifControllerOneToAnotherUser.addReportableStep(loadLoginPage);
        ifControllerOneToAnotherUser.addReportableStep(login_user);
        ifControllerOneToAnotherUser.addReportableStep(simplyGetUrlCall);

        ThreadGroupElement threadGroup = ThreadGroupElement.builder().threadCount(threads).rampUp(rampup)
                .continueForever(true).setScheduler(true).duration(Integer.parseInt(System.getProperty("duration", "10")))
                .name("Show Active Users Test").build();
        CSVDataSetElement csvDataSetElement = CSVDataSetElement.builder().
                fileName(csvFile).
                delimiter("\\t").variableNames("method,url,user_agent,user_id,loginname").name("allurls").quotedData(false).stopThread(true).shareMode("shareMode.all")
                .recycle(true).build();

        threadGroup.addStep(csvDataSetElement);
        threadGroup.addReportableStep(ifControllerAnonymousUser);
        threadGroup.addReportableStep(ifControllerActualUser);

        jmeter.addStep(threadGroup);


        jmeter.addElasticSearchMapping(elasticSearchCluster, elasticSearchHost, elasticSearchPort);
        jmeter.addElasticSearchListener(elasticSearchCluster, testRun, elasticSearchHost, elasticSearchPort);

        /* Now run the jmeter client which will run script on remote instance */
        List<String> remoteHosts = Arrays.asList(remoteJmeterInstance.split(","));

        jmeter.remoteRun(remoteHosts);
    }
}
