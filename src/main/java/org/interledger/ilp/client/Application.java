package org.interledger.ilp.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;

import org.apache.commons.cli.HelpFormatter;
import org.interledger.ilp.client.commands.LedgerCommand;
import org.interledger.ilp.client.events.ClientLedgerConnectEvent;
import org.interledger.ilp.client.events.ClientLedgerErrorEvent;
import org.interledger.ilp.client.events.ClientLedgerMessageEvent;
import org.interledger.ilp.client.events.ClientLedgerTransferEvent;
import org.interledger.ilp.ledger.events.LedgerEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@SpringBootApplication
public class Application implements CommandLineRunner, ApplicationContextAware{
  
  private static final Logger log = LoggerFactory.getLogger(Application.class);

  private ApplicationContext applicationContext;
    
  public static void main(String[] args) {
    SpringApplication.run("classpath:/META-INF/application-context.xml", args);
  }

  public void run(String... args) throws Exception {
    
    if(args.length == 0) {
      
      Map<String, LedgerCommand> commands = applicationContext.getBeansOfType(LedgerCommand.class);

      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp("<command> [options]", commands.get("helpCommand").getDefaultOptions());
      
       System.out.println("\r\n"
          + "Settings are read from application.properties but may be overridden through options.\r\n"
          + "\r\n"
          + "Commands:\r\n");
       
       for (LedgerCommand command : commands.values()) {
         System.out.println(command.getCommand() + " - " + command.getDescription());
       }
                    
      //Loop and read in commands
      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
      String commandLine = reader.readLine();
      
      while(!"quit".equals(commandLine.trim())) {
        
      args = commandLine.split(" ");
        
        if(args.length > 0) {
          LedgerCommand command = LedgerCommand.getLedgerCommand(args[0], commands);
          if(command != null) {
            try {
              command.run(args);
            } catch (Exception e) {
              e.printStackTrace(System.err);
            }
          } else {
            log.error("Unrecognized command: " + args[0]);
          }
        } else {
          //Empty line
        }
        
        commandLine = reader.readLine();
        
      }
    }
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }

  @EventListener
  public void onLedgerEvent(LedgerEvent event) {
    
    if(event instanceof ClientLedgerTransferEvent) {
      log.info("Transfer: {}", ((ClientLedgerTransferEvent) event).getTransfer().toString());
    }
    
    if (event instanceof ClientLedgerMessageEvent) {
            
      log.info("Message: {}", ((ClientLedgerMessageEvent) event).getMessage().toString());

    }
    
    if(event instanceof ClientLedgerErrorEvent) {
      log.error("Error event...", ((ClientLedgerErrorEvent) event).getError());
    }
    
    if(event instanceof ClientLedgerConnectEvent) {
      log.info("Connected to " + ((ClientLedgerConnectEvent) event).getSource().toString());
    }
    
  }
  
}
