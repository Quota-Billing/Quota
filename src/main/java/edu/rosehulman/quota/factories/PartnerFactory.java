package edu.rosehulman.quota.factories;

import edu.rosehulman.quota.model.Partner;

public class PartnerFactory {

  public Partner createPartner(String partnerId, String apiKey) {
    Partner partner = new Partner();
    partner.setPartnerId(partnerId);
    partner.setApiKey(apiKey);
    return partner;
  }

}
