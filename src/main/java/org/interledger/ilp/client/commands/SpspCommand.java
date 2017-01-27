package org.interledger.ilp.client.commands;

import java.math.BigDecimal;
import java.net.URI;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.UUID;

import javax.money.Monetary;
import javax.money.MonetaryAmount;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.interledger.client.quoting.BasicQuoteSelectionStrategies;
import org.interledger.ilp.InterledgerPaymentRequest;
import org.interledger.ilqp.client.IlqpQuoteService;
import org.interledger.ilqp.client.model.ClientQuoteRequest;
import org.interledger.quoting.QuoteService;
import org.interledger.quoting.model.QuoteResponse;
import org.interledger.setup.SetupService;
import org.interledger.setup.spsp.model.Invoice;
import org.interledger.setup.spsp.model.ReceiverType;
import org.interledger.setup.spsp.model.SpspReceiver;
import org.interledger.setup.spsp.model.SpspReceiverQuery;
import org.interledger.spsp.client.SpringSpspSenderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SpspCommand extends LedgerCommand {

  private static final Logger log = LoggerFactory.getLogger(SpspCommand.class);
  
  @Override
  public String getCommand() {
    return "spsp";
  }

  @Override
  public String getDescription() {
    return "Send SPSP payment";
  }

  @Override
  public Options getOptions() {
    return getDefaultOptions()
        .addOption(
            Option.builder("receiver").argName("receiver uri").hasArg().required()
            .desc("Receiver endpoint").build())
        .addOption(
            Option.builder("amount").argName("amount").hasArg()
            .desc("Amount the receiver will get").build())
        .addOption(
            Option.builder("memo").argName("memo").hasArg()
            .desc("Sender memo").build());
    
  }

  @Override
  protected void runCommand(CommandLine cmd) throws Exception {
    
    SetupService service = new SpringSpspSenderService();
    URI receiverUri = URI.create(cmd.getOptionValue("receiver"));
    SpspReceiver receiver = (SpspReceiver) service.query(new SpspReceiverQuery(receiverUri));

    log.info("Receiver account: " + receiver.getAccount());
    log.info("Receiver type: " + receiver.getClass().getName());
    log.info("Receiver currency: " + receiver.getCurrencyUnit());
    
    MonetaryAmount amount;
    if(receiver.getType() == ReceiverType.invoice && 
        receiver.getClass().isAssignableFrom(Invoice.class)) {
      amount = ((Invoice) receiver).getAmount();
    } else {
      if(cmd.getOptionValue("amount") != null) {
        amount = Monetary.getDefaultAmountFactory()
            .setCurrency(receiver.getCurrencyUnit())
            .setNumber(new BigDecimal(cmd.getOptionValue("amount")))
            .create();
      } else {
        log.error("Receiver is not an invoice therefore an amount is required.");
        return;
      }
      
    }
    
    String senderIdentifier = "test@test";
    String memo = "test";
    
    if(cmd.getOptionValue("memo") != null) {
      memo = cmd.getOptionValue("memo");
    }
    
    
    InterledgerPaymentRequest ipr = service.setupPayment(receiver, amount, senderIdentifier, memo);
    log.info("IPR address: " + ipr.getAddress());
    log.info("IPR amount: " + ipr.getAmount());
    log.info("IPR condition: " + ipr.getCondition().getUri().toString());
    log.info("IPR expiry: " + ipr.getExpiresAt());
    
    QuoteService quoteService = new IlqpQuoteService(ledgerClient);
    
    ClientQuoteRequest query = new ClientQuoteRequest();
    query.setDestinationAddress(receiver.getAccount());
    query.setDestinationAmount(ipr.getAmount());
    query.setDestinationExpiryDuration(Duration.between(ZonedDateTime.now(), ipr.getExpiresAt()));
    query.setSourceAddress(ledgerClient.getAccount());
    
    log.info("Getting quote...");
    QuoteResponse quote = quoteService.requestQuote(query, BasicQuoteSelectionStrategies.LowestSourceAmount);
    log.info("Quote connector address: " + quote.getSourceConnectorAccount());
    log.info("Quote source amount: " + quote.getSourceAmount());
    
    
    log.info("Making payment...");
    UUID transferId = ledgerClient.makePayment(quote, ipr);
    
    log.info("Transfer UUID: " + transferId);
        
  }

}

