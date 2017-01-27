package org.interledger.ilp.client.commands;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.interledger.ilp.InterledgerAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MessageCommand extends LedgerCommand {
  private static final Logger log = LoggerFactory.getLogger(MessageCommand.class);

  @Override
  public String getCommand() {
    return "message";
  }

  @Override
  public String getDescription() {
    return "Send a message.";
  }

  @Override
  public Options getOptions() {
    return getDefaultOptions()
        .addOption(Option.builder("to").argName("to account").hasArg().required()
            .desc("ExtendedAccountInfo to which message is sent.").build())
        .addOption(Option.builder("data").argName("data").hasArg().required()
            .desc("Message to be sent.").build());
  }

  @Override
  protected void runCommand(CommandLine cmd) throws Exception {
    try {
      
      InterledgerAddress to = new InterledgerAddress(cmd.getOptionValue("to"));
      ledgerClient.sendMessage(to, "TEST", cmd.getOptionValue("data"), 60);
    } catch (Exception e) {
      log.error("error sending message", e);
    }
  }
  
}
