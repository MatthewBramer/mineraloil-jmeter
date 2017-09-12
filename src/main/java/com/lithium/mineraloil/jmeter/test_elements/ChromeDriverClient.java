package com.lithium.mineraloil.jmeter.test_elements;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.threads.JMeterVariables;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.util.concurrent.TimeUnit;

/**
 * Created by viren.thakkar on 9/11/17.
 */
public class ChromeDriverClient extends AbstractJavaSamplerClient {

    private static final String WEBURL = "base_url";
    private static final String Selenium_Server = "selenium_server_url";
    private String protocol;
    private String domain;
    private String port;
    private WebDriver driver;
    JMeterVariables vars;
    private Arguments arguments;
    @Override
    public Arguments getDefaultParameters() {

        Arguments defaultParameters = new Arguments();
        defaultParameters.addArgument(WEBURL,"http://localhost:8080");
        defaultParameters.addArgument(Selenium_Server,"http://localhost:4444/wd/hub");

        return defaultParameters;
    }

    @Override
    public void setupTest(JavaSamplerContext context) {

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.chrome();
        capabilities.setJavascriptEnabled(true);
        driver= new RemoteWebDriver(capabilities);
        driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
        protocol=System.getProperty("protocol", "http");
        domain=System.getProperty("host");
        port=System.getProperty("port");
        super.setupTest(context);

    }

    @Override
    public SampleResult runTest(JavaSamplerContext javaSamplerContext) {

        JMeterVariables vars = JMeterContextService.getContext().getVariables();
        String url = (String)vars.getObject( "url" );

        SampleResult sampleResult = new SampleResult();

        sampleResult.sampleStart();
        try {
            driver.get(protocol+"://" + domain +":" +port + url);

            sampleResult.setSampleLabel(url);
            sampleResult.sampleEnd(); // stop stopwatch
            sampleResult.setSuccessful(true);
            sampleResult.setResponseMessage( "URL loaded" );
            sampleResult.setResponseCodeOK();
            sampleResult.setRequestHeaders("NONE");
            sampleResult.setResponseHeaders("NONE");
            sampleResult.setResponseData("NONE",null);


        }catch (WebDriverException e){
            sampleResult.sampleEnd();
            sampleResult.setSuccessful(false);
            sampleResult.setResponseMessage( "Exception: thrown" );
            sampleResult.setRequestHeaders("NONE");
            sampleResult.setResponseHeaders("NONE");

        }

        return sampleResult;

    }

    @Override
    public void teardownTest(JavaSamplerContext context) {
        driver.close();
        driver.quit();
    }
}