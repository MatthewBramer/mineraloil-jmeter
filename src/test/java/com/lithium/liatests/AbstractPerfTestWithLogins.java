package com.lithium.liatests;

import com.lithium.mineraloil.jmeter.JMeterRunner;
import com.lithium.mineraloil.jmeter.test_elements.*;
import org.apache.jmeter.protocol.http.control.CookieManager;
import org.apache.jmeter.protocol.http.control.Header;
import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.protocol.http.util.HTTPArgument;
import org.apache.jmeter.sampler.TestAction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by viren.thakkar on 4/25/16.
 */
public abstract class AbstractPerfTestWithLogins {

    public void prepareScenario(JMeterRunner jmeter){
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
        String createDashboard = System.getProperty("createDashboard","true");
     /*   String elasticSearchCluster = System.getProperty("elasticSearchClusterName");
        String elasticSearchHost = System.getProperty("elasticSearchHost");
        int elasticSearchPort = Integer.parseInt(System.getProperty("elasticSearchPort"));
        String testRun = System.getProperty("testRun");
        String release = System.getProperty("release");
        String release = System.getProperty("release");
        String revision = System.getProperty("revision");
        String community = System.getProperty("community");

        /*
          This is to pass extra parameters (JmeterProperties) to jmeter.
         */
       /* Properties properties = new Properties();
        properties.put("elasticSearchClusterName",elasticSearchCluster);
        properties.put("elasticSearchHost",elasticSearchHost);
        properties.put("elasticSearchPort",String.valueOf(elasticSearchPort));
        properties.put("testRun",testRun);
        properties.put("release",release);
        properties.put("revision",revision);
        properties.put("community",community);*/


        // jmeter.addExtraJmeterProperties(properties);

        /* Http Cache manager*/
        HttpCacheManagerElement httpCacheManagerElement = HttpCacheManagerElement.builder().name("httpCacheManager_global").build();
        httpCacheManagerElement.getTestElement().setUseExpires(true);

        /* Http cookie manager at global level for thread group*/

        HttpCookieManagerElement httpCookieManagerElement = HttpCookieManagerElement.builder().name("httpCookieManager_global").build();
        httpCookieManagerElement.getTestElement().setImplementation(CookieManager.DEFAULT_IMPLEMENTATION);
        httpCookieManagerElement.getTestElement().setProperty("CookieManager.check.cookies",false);
        jmeter.cookieManager=httpCookieManagerElement.getTestElement();
        jmeter.getTestPlan().setProperty("CookieManager.check.cookies",false);

        /* Create header element */
        HeaderManager headerManager = new HeaderManager();
        Header header = new Header("User-Agent", "${useragent}");
        headerManager.add(header);

        /*Set cookie check to false*/

        /* IfController to check whether user is anonymous or not */

        IfControllerElement ifControllerAnonymousUser = IfControllerElement.builder().name("AnonymousUser").condition("${user_id}<0").build();

        IfControllerElement ifControllerUserNotLoggedInBefore = IfControllerElement.builder().name("checkIfUserNotLoggedInBefore").condition("\"${COOKIE_LithiumUserInfo}\"==\"\\${COOKIE_LithiumUserInfo}\"").build();
        IfControllerElement ifControllerUserLoggedAndNeedToLogout = IfControllerElement.builder().name("checkIfUserLoggedAndNeedToLogout").condition("\"${COOKIE_LithiumUserInfo}\"!=\"\\${COOKIE_LithiumUserInfo}\" && \"${COOKIE_LithiumUserInfo}\".length>0").build();

        //TestActionElement moveOnToNextIteration = TestActionElement.builder().name("MoveOnToNextIteration").build();
       // moveOnToNextIteration.getTestElement().setTarget(TestAction.RESTART_NEXT_LOOP);



        /* Create HTTP Sampler for anonyous call */
        HTTPSamplerElement anoynoumUserRequest = HTTPSamplerElement.builder()
                .domain(domain)
                .port(port)
                .protocol(protocol)
                .path("${url}")
                .method("GET")
                .implementation("HttpClient4").headerManager(headerManager)
                .build();

        /* Create request for logout from current session so that we can make either Anonymous call or make another user login*/

        HTTPSamplerElement logout = HTTPSamplerElement.builder()
                .domain(domain)
                .port(port)
                .protocol(protocol)
                .path("/t5/community/page.logoutpage?t:cp=authentication/contributions/unticketedauthenticationactions&dest_url=" + protocol + "://" + domain + ":" + port + "${url}")
                .method("GET")
                .implementation("HttpClient4").headerManager(headerManager)
                .build();

        ifControllerUserNotLoggedInBefore.addReportableStep(anoynoumUserRequest);
        //ifControllerUserNotLoggedInBefore.addStep(moveOnToNextIteration);

        ifControllerUserLoggedAndNeedToLogout.addReportableStep(logout);
        //ifControllerUserLoggedAndNeedToLogout.addStep(moveOnToNextIteration);

        ifControllerAnonymousUser.addStep(ifControllerUserNotLoggedInBefore);
        ifControllerAnonymousUser.addStep(ifControllerUserLoggedAndNeedToLogout);


        /* IfController to check whether user is actual  */

        IfControllerElement ifControllerActualUser = IfControllerElement.builder().name("ActualUser").condition("${user_id}>0").build();

        IfControllerElement ifControllerAnonToUser = IfControllerElement.builder().name("AnonToUser").condition("\"${COOKIE_LithiumUserInfo}\"==\"\\${COOKIE_LithiumUserInfo}\"").build();

        IfControllerElement ifControllerOneToAnotherUser = IfControllerElement.builder().name("OneToAnother").condition("\"${COOKIE_LithiumUserInfo}\"!=\"\\${COOKIE_LithiumUserInfo}\" && ${COOKIE_LithiumUserInfo}!=${user_id}").build();


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
        //ifControllerAnonToUser.addStep(moveOnToNextIteration);


/* This is for one user to another user.Under actual user condition add one to another user condition and then add loginpage get call, login user and then make normal ${url} call */

        ifControllerActualUser.addStep(ifControllerOneToAnotherUser);

        /* Added regex for logout call so that next login call can have data ready. */
        logout.addStep(regex_lia_action_token);
        logout.addStep(regex_ac_data);
        logout.addStep(regex_lia_action_token);

        ifControllerOneToAnotherUser.addReportableStep(logout);
        ifControllerOneToAnotherUser.addReportableStep(login_user);
        ifControllerOneToAnotherUser.addReportableStep(simplyGetUrlCall);
        //ifControllerOneToAnotherUser.addStep(moveOnToNextIteration);

        ThreadGroupElement threadGroup = ThreadGroupElement.builder().threadCount(threads).rampUp(rampup)
                .continueForever(true).setScheduler(true).duration(Integer.parseInt(System.getProperty("duration", "10")))
                .name("Show Active Users Test").build();
        CSVDataSetElement csvDataSetElement = CSVDataSetElement.builder().
                fileName(csvFile).
                delimiter("\\t").variableNames("method,url,user_agent,user_id,loginname").name("allurls").quotedData(false).stopThread(false).shareMode("shareMode.all")
                .recycle(true).build();

        threadGroup.addStep(httpCookieManagerElement);
        threadGroup.addStep(httpCacheManagerElement);
        threadGroup.addStep(csvDataSetElement);
        threadGroup.addReportableStep(ifControllerAnonymousUser);
        threadGroup.addReportableStep(ifControllerActualUser);

        jmeter.addStep(threadGroup);
    }
}
