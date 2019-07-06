package com.learncamel.routes;

import org.apache.camel.CamelContext;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

/**
 * Created by z001qgd on 1/4/18.
 */
@ActiveProfiles("dev")
@RunWith(CamelSpringBootRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class KafkaRouteTest extends CamelTestSupport {

    @Autowired
    private CamelContext context;

    @Autowired
    protected CamelContext createCamelContext() {
        return context;
    }

    @Autowired
    private ProducerTemplate producerTemplate;

    @Autowired
    private  ConsumerTemplate consumerTemplate;

    @Override
    protected RouteBuilder createRouteBuilder(){
        return new KafkaRoute();
    }

    @Autowired
    Environment environment;

    @Before
    public void setUp(){

    }

   @Test
    public void kafkaRoute(){

       String input = "{\"transactionType\":\"ADD\", \"sku\":\"200\", \"itemDescription\":\"SamsungTV\", \"price\":\"500.00\"}\n";
       String response = (String) producerTemplate.requestBody("kafka:inputItemTopic?brokers=localhost:9092", input);
       assertNotNull(response);
   }
    @Test
    public void kafkaRouteError(){

        String input = "{\"transactionType\":\"ADD\", \"sku\":\"\", \"itemDescription\":\"SamsungTV\", \"price\":\"500.00\"}\n";
        producerTemplate.sendBody("kafka:inputItemTopic?brokers=localhost:9092", input);
        String response = (String) consumerTemplate.receiveBody("kafka:errorTopic?brokers=localhost:9092");
        System.out.println("Response is " + response);
        assertNotNull(response);
    }
}
