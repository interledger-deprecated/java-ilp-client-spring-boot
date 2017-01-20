package org.interledger.ilp.client.commands;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ConnectCommand extends LedgerCommand {

  private static final Logger log = LoggerFactory.getLogger(ConnectCommand.class);
  
  @Override
  public String getCommand() {
    return "connect";
  }

  @Override
  public String getDescription() {
    return "Connect the adaptor";
  }
  
  @Override
  public Options getOptions() {
    return getDefaultOptions();
  }

  @Override
  protected void runCommand(CommandLine cmd) throws Exception {
    
    if(this.ledgerClient.isConnected()) {
      log.info("Already connected");
      log.debug(this.ledgerClient.getLedgerInfo().toString());
      return;
    }
    
    log.info("Connecting...");
    this.ledgerClient.connect();
    
  }
}
