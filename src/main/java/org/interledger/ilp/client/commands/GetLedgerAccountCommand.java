package org.interledger.ilp.client.commands;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.interledger.ilp.core.ledger.model.AccountInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class GetLedgerAccountCommand extends LedgerCommand {

  private static final Logger log = LoggerFactory.getLogger(GetLedgerAccountCommand.class);

  @Override
  public String getCommand() {
    return "getLedgerAccount";
  }

  @Override
  public String getDescription() {
    return "Get account info.";
  }

  @Override
  public Options getOptions() {
    return getDefaultOptions();
  }

  @Override
  protected void runCommand(CommandLine cmd) throws Exception {
    try {
      
      log.debug("Getting account details for " + ledgerClient.getAccount());
      AccountInfo accountData = ledgerClient.getAccountInfo();
      
      log.info(accountData.toString());
    } catch (Exception e) {
      log.error("Error getting account data.", e);
    }
  }
}
