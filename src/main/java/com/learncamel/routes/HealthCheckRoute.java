package com.learncamel.routes;

import com.learncamel.alert.MailProcessor;
import com.learncamel.processor.HealthCheckProcessor;
import org.apache.camel.Predicate;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by z001qgd on 2/10/18.
 */
@Component
public class HealthCheckRoute extends RouteBuilder{


    @Autowired
    HealthCheckProcessor healthCheckProcessor;

    @Autowired
    MailProcessor mailProcessor;

    Predicate isNotMock =  header("env").isNotEqualTo("mock");


    @Override
    public void configure() throws Exception {

            from("{{healthRoute}}").routeId("healthRoute")
                .choice() // Content based EIP
                    .when(isNotMock) // not mock check
                        .pollEnrich("http://localhost:8080/health")
                    .end()
                .process(healthCheckProcessor)
                .choice()
                    .when(header("error").isEqualTo(true))
                        .choice()
                            .when(isNotMock)
                                .process(mailProcessor)
                            .end()
                .end();
    }
}
