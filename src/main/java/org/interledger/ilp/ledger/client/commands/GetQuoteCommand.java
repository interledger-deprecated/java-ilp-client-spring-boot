package org.interledger.ilp.ledger.client.commands;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.interledger.ilp.core.InterledgerAddress;
import org.interledger.ilp.ledger.client.json.JsonQuoteRequest;
import org.interledger.ilp.ledger.client.json.JsonQuoteResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class GetQuoteCommand extends LedgerCommand {

  private static final Logger log = LoggerFactory.getLogger(GetQuoteCommand.class);

  @Override
  public String getCommand() {
    return "getQuote";
  }

  @Override
  public String getDescription() {
    return "Request a quote";
  }

  @Override
  public Options getOptions() {
    return getDefaultOptions()
        .addOption(
            Option.builder("sourceAmount").argName("source amount").hasArg()
            .desc("Source Amount").build())
        .addOption(
            Option.builder("destAmount").argName("destination amount").hasArg()
            .desc("Destination Amount").build())
        .addOption(
            Option.builder("sourceAddress").argName("source address").hasArg()
            .desc("Source ILP Address").build())
        .addOption(
            Option.builder("destAddress").argName("destination address").hasArg().required()
            .desc("Recipients ILP Address").build())
        .addOption(
            Option.builder("expiry").argName("destination expiry").hasArg()
            .desc("Quote expiry").build())
        .addOption(Option.builder("connector").argName("connector").hasArg()
            .desc("Connector").build());
    
  }

  @Override
  protected void runCommand(CommandLine cmd) throws Exception {
    JsonQuoteRequest quoteParams = new JsonQuoteRequest();
    quoteParams.setSourceAddress(cmd.getOptionValue("sourceAddress"));
    quoteParams.setSourceAmount(cmd.getOptionValue("sourceAmount"));
    quoteParams.setDestinationAmount(cmd.getOptionValue("destAmount"));
    quoteParams.setDestinationAddress(cmd.getOptionValue("destAddress"));
    if(cmd.getOptionValue("destExpiry") != null) {
      quoteParams.setDestinationExpiryDuration(Integer.valueOf(cmd.getOptionValue("destExpiry")));
    }
    
    InterledgerAddress ledger = ledgerClient.getAdaptor().getLedgerInfo().getAddressPrefix();
    
    if(cmd.getOptionValue("connector") != null) {
      Set<InterledgerAddress> connectors = new HashSet<>();
      connectors.add(InterledgerAddress.fromPrefixAndPath(ledger, cmd.getOptionValue("connector")));
      quoteParams.setConnectors(connectors);      
    }
    
    JsonQuoteResponse quoteResponse = this.ledgerClient.requestQuote(quoteParams);
    if (quoteResponse == null) {
      log.info("no quote received");
    } else {
      log.info(
          "received quote: source amount {}, expiry duration {}; dest amount {}, expiry duration {}, connector account {}",
          quoteResponse.getSourceAmount(), quoteResponse.getSourceExpiryDuration(),
          quoteResponse.getDestinationAmount(), quoteResponse.getDestinationExpiryDuration(),
          quoteResponse.getSourceConnectorAccount());
    }
  }
}

