package com.aaronthompson.harrier;

import com.aaronthompson.harrier.controller.HarrierController;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Import;

import org.springframework.beans.factory.annotation.Autowired;
import javax.sql.DataSource;
import static java.lang.System.exit;


@SpringBootApplication
public class HarrierApplication extends SpringBootServletInitializer {

    @Autowired
    DataSource dataSource;

    //static Logger logger = LoggerFactory.getLogger(HarrierApplication.class);
//    private final static Logger logger = LogManager.getLogger(HarrierApplication.class);
//
    public static void main(String[] args) {

        SpringApplication.run(HarrierApplication.class, args);
//        logger.error("error msg");
//        logger.debug("debug msg");
//        logger.info("info msg");
//        logger.warn("warning msg");
    }

    // proves that hikariCP is active --> prints out Hikari(null)
    //implements CommandLineRunner
//    @Override
//    public void run(String... args) throws Exception {
//        System.out.println("DATASOURCE = " + dataSource);
//        exit(0);
//    }

}
