package org.interledger.ilp.client.commands;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DisconnectCommand extends LedgerCommand {

  private static final Logger log = LoggerFactory.getLogger(DisconnectCommand.class);

  @Override
  public String getCommand() {
    return "disconnect";
  }

  @Override
  public String getDescription() {
    return "Disconnect the adaptor";
  }
  
  @Override
  public Options getOptions() {
    return getDefaultOptions();
  }

  @Override
  protected void runCommand(CommandLine cmd) throws Exception {
    if(!this.ledgerClient.isConnected()) {
      log.info("Not connected");
      return;
    }
    
    log.info("Closing connection...");
    
    this.ledgerClient.disconnect();
    log.info("Disconnected");
  }
}
