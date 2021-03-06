package org.interledger.ilp.client.events;

import org.interledger.ilp.ledger.events.LedgerEvent;
import org.interledger.ilp.ledger.events.LedgerEventHandler;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

public class ApplicationEventPublishingLedgerEventHandler implements LedgerEventHandler, ApplicationEventPublisherAware{
  
  private ApplicationEventPublisher publisher;
  
  public ApplicationEventPublishingLedgerEventHandler (ApplicationEventPublisher publisher) {
    this.publisher = publisher;
  }
  
  @Override
  public void handleLedgerEvent(LedgerEvent event) {
    publisher.publishEvent(event);
  }

  @Override
  public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
    this.publisher = applicationEventPublisher;
  }

}
