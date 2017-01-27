package org.interledger.ilp.client.commands;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

import javax.money.Monetary;
import javax.money.MonetaryAmount;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.interledger.client.quoting.BasicQuoteSelectionStrategies;
import org.interledger.ilp.InterledgerAddress;
import org.interledger.ilqp.client.IlqpQuoteService;
import org.interledger.ilqp.client.model.ClientQuoteRequest;
import org.interledger.quoting.QuoteService;
import org.interledger.quoting.model.QuoteResponse;
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
    
    ClientQuoteRequest quoteParams = new ClientQuoteRequest();
    quoteParams.setSourceAddress(ledgerClient.getAccount());
    
    if(cmd.getOptionValue("sourceAmount") != null) {
      MonetaryAmount amount = Monetary.getDefaultAmountFactory()
          .setCurrency(ledgerClient.getLedgerInfo().getCurrencyUnit())
          .setNumber(new BigDecimal(cmd.getOptionValue("sourceAmount")))
          .create();
      quoteParams.setSourceAmount(amount);
    }
    
    if(cmd.getOptionValue("destAmount") != null) {
      MonetaryAmount amount = Monetary.getDefaultAmountFactory()
          //FIXME Should be currency of dest ledger
          .setCurrency(ledgerClient.getLedgerInfo().getCurrencyUnit())
          .setNumber(new BigDecimal(cmd.getOptionValue("destAmount")))
          .create();
      quoteParams.setDestinationAmount(amount);
    }
    
    quoteParams.setDestinationAddress(new InterledgerAddress(cmd.getOptionValue("destAddress")));
    
    if(cmd.getOptionValue("expiry") != null) {
      quoteParams.setDestinationExpiryDuration(
          Duration.of(Integer.valueOf(cmd.getOptionValue("expiry")), ChronoUnit.MILLIS));
    }
    
    InterledgerAddress ledger = ledgerClient.getLedgerInfo().getAddressPrefix();
    
    QuoteService service = new IlqpQuoteService(ledgerClient);
    
    log.info("Getting quote...");
    QuoteResponse response;
    if(cmd.getOptionValue("connector") != null){
      InterledgerAddress connector = InterledgerAddress.fromPrefixAndPath(ledger, cmd.getOptionValue("connector"));
      response = service.requestQuote(quoteParams, connector);
    } else {
      response = service.requestQuote(quoteParams, BasicQuoteSelectionStrategies.LowestSourceAmount);
    }
    log.info("Quote: " + response);
    
  }

}

