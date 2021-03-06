package com.umg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.umg.asset.transformer.AssertProcessTemplate;

/**
 * Created by Somnath.
 */
@SpringBootApplication
public class Application implements CommandLineRunner {
    static Logger log = LoggerFactory.getLogger(Application.class);

    @Autowired
    AssertProcessTemplate assertProcessTemplate;
   
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {  
        
        log.debug("Attributes feed job started");
        assertProcessTemplate.run(args);
        log.debug("Attributes feed job finished");
    }
}
