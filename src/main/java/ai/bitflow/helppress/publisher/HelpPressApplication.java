package ai.bitflow.helppress.publisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.context.ConfigurableApplicationContext;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author metho
 */
@Slf4j
@SpringBootApplication
public class HelpPressApplication { // extends Application

    private final Logger logger = LoggerFactory.getLogger(HelpPressApplication.class);
    private ConfigurableApplicationContext ctx;

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(HelpPressApplication.class);
        app.addListeners(new ApplicationPidFileWriter());
        app.run();
    }

}
