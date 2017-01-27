package org.interledger.ilp.client.commands;

import java.math.BigDecimal;

import javax.money.Monetary;
import javax.money.MonetaryAmount;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.interledger.ilp.InterledgerAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TransferCommand extends LedgerCommand {

  private static final Logger log = LoggerFactory.getLogger(TransferCommand.class);

  @Override
  public String getCommand() {
    return "transfer";
  }

  @Override
  public String getDescription() {
    return "Transfer from one account to another.";
  }
  
  @Override
  public Options getOptions() {
    return getDefaultOptions()
        .addOption(
            Option.builder("to").argName("to account").hasArg().required()
            .desc("ExtendedAccountInfo to be credited").build())
        .addOption(
            Option.builder("amount").argName("amount").hasArg().required()
            .desc("Amount to be transferred").build());
  }

  @Override
  protected void runCommand(CommandLine cmd) throws Exception {
    
    try {
      MonetaryAmount amount = Monetary.getDefaultAmountFactory()
          .setCurrency(ledgerClient.getLedgerInfo().getCurrencyUnit())
          .setNumber(new BigDecimal(cmd.getOptionValue("amount")))
          .create();
      ledgerClient.sendTransfer(new InterledgerAddress(cmd.getOptionValue("to")), amount);
    } catch (Exception e) {
      log.error("Error creating transfer.", e);
    }

  }
}
