package org.interledger.ilp.ledger.client.commands;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.interledger.ilp.core.InterledgerAddress;
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
    return getDefaultOptions().addOption(Option.builder("account").argName("account").hasArg().required()
        .desc("ExtendedAccountInfo to query").build());
  }

  @Override
  protected void runCommand(CommandLine cmd) throws Exception {
    try {
      
      String account = cmd.getOptionValue("account");
      InterledgerAddress address = InterledgerAddress.fromPrefixAndPath(
          this.ledgerClient.getAdaptor().getLedgerInfo().getAddressPrefix(), 
          account);
      log.debug("Getting extended account details for " + account);
      AccountInfo accountData = ledgerClient.getAdaptor().getAccountInfo(address);
      
      log.info(accountData.toString());
    } catch (Exception e) {
      log.error("Error getting account data.", e);
    }
  }
}
