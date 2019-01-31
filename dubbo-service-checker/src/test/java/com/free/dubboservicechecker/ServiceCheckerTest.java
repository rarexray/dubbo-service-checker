package com.free.dubboservicechecker;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author rarexray
 * @date 2019/1/31 上午10:38
 */
public class ServiceCheckerTest {

    private ServiceChecker serviceChecker;

    @Before
    public void init(){
        serviceChecker = new ServiceChecker();
        serviceChecker.buildEnv();
    }

    @Test
    public void isDubboAppRunning() throws Exception {
    }

    @Test
    public void appRunningHostList() throws Exception {
    }

    @Test
    public void getDependencyCount() throws Exception {
    }

    @Test
    public void findAll() throws Exception {
    }

    @Test
    public void printNoProviderService() throws Exception {
        serviceChecker.printNoProviderService();
    }

    @Test
    public void printMostDepandencedService() throws Exception {
        serviceChecker.printMostDepandencedService();
    }

    @Test
    public void printCycleService() throws Exception {
        serviceChecker.printCycleService();
    }

}