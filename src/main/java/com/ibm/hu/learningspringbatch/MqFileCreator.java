package com.ibm.hu.learningspringbatch;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.util.MultiValueMap;

import java.util.Map;

public class MqFileCreator implements ItemProcessor {
    @Override
    public Object process(Object o) throws Exception {
        return o;
    }
}
