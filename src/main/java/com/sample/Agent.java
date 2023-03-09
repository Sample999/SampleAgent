package com.sample;

import com.sample.agent.SampleInstrumentationAgent;

import java.lang.instrument.Instrumentation;

/**
 * @author 82196
 */
public class Agent{

    public static void premain(String args, Instrumentation instrumentation) {
        System.out.println("[Agent] In premain method");
        String className = args;
        if(null != className && "" != className){
            SampleInstrumentationAgent.transformClass(className, instrumentation);
        }else{
            SampleInstrumentationAgent.transformClass(instrumentation);
        }
    }
}