package edu.rosehulman.quota;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.google.gson.JsonObject;

import edu.rosehulman.quota.controller.GetQuotaSDKController;
import edu.rosehulman.quota.model.Partner;
import edu.rosehulman.quota.model.Quota;
import edu.rosehulman.quota.model.Tier;
import edu.rosehulman.quota.model.UserTier;
import spark.HaltException;
import spark.Request;
import spark.Response;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Database.class, Request.class, Response.class })
public class GetQuotaSDKControllerTest {

  private Database database;
  private Request request;
  private Request badRequest;
  private Request missingPartnerRequest;
  private Request missingQuotaRequest;
  private Request missingUserTierRequest;
  private Request missingTierRequest;
  private Response response;
  private GetQuotaSDKController getQuotaSDKController;
  private Optional<Quota> optPresentQuota;
  private Optional<Quota> optMissingQuota;
  private Optional<Partner> optPresentPartner;
  private Optional<Partner> optMissingPartner;
  private Optional<UserTier> optPresentUserTier;
  private Optional<UserTier> optPresentUserTierMissingTier;
  private Optional<UserTier> optMissingUserTier;
  private Optional<Tier> optPresentTier;
  private Optional<Tier> optMissingTier;
  private Quota quota;
  private Partner partner;
  private UserTier userTier;
  private UserTier userTierMissingTier;
  private Tier tier;
  private JsonObject json;

  @Before
  public void setUp() throws Exception {
    // setup mocks
    mockStatic(Database.class);
    database = mock(Database.class);
    request = mock(Request.class);
    badRequest = mock(Request.class);
    missingPartnerRequest = mock(Request.class);
    missingQuotaRequest = mock(Request.class);
    missingUserTierRequest = mock(Request.class);
    missingTierRequest = mock(Request.class);
    response = mock(Response.class);
    quota = mock(Quota.class);
    partner = mock(Partner.class);
    userTier = mock(UserTier.class);
    userTierMissingTier = mock(UserTier.class);
    tier = mock(Tier.class);

    // real Objects
    getQuotaSDKController = new GetQuotaSDKController();
    optPresentQuota = Optional.of(quota);
    optMissingQuota = Optional.empty();
    optPresentPartner = Optional.of(partner);
    optMissingPartner = Optional.empty();
    optPresentUserTier = Optional.of(userTier);
    optPresentUserTierMissingTier = Optional.of(userTierMissingTier);
    optMissingUserTier = Optional.empty();
    optPresentTier = Optional.of(tier);
    optMissingTier = Optional.empty();
    json = new JsonObject();

    // returns
    when(partner.getPartnerId()).thenReturn("partnerId");

    when(userTier.getTierId()).thenReturn("tierId");
    when(userTier.getValue()).thenReturn("52");
    when(userTierMissingTier.getTierId()).thenReturn("missing_tierId");

    when(tier.getMax()).thenReturn("100");

    when(Database.getInstance()).thenReturn(database);
    when(request.params(":apiKey")).thenReturn("apiKey");
    when(request.params(":productId")).thenReturn("productId");
    when(request.params(":userId")).thenReturn("userId");
    when(request.params(":quotaId")).thenReturn("quotaId");

    when(badRequest.params(":apiKey")).thenReturn("bad_apiKey");
    when(badRequest.params(":productId")).thenReturn("productId");
    when(badRequest.params(":userId")).thenReturn("userId");
    when(badRequest.params(":quotaId")).thenReturn("quotaId");
    
    when(missingPartnerRequest.params(":apiKey")).thenReturn("missing_apiKey");
    when(missingPartnerRequest.params(":productId")).thenReturn("productId");
    when(missingPartnerRequest.params(":userId")).thenReturn("userId");
    when(missingPartnerRequest.params(":quotaId")).thenReturn("quotaId");

    when(missingQuotaRequest.params(":apiKey")).thenReturn("apiKey");
    when(missingQuotaRequest.params(":productId")).thenReturn("productId");
    when(missingQuotaRequest.params(":userId")).thenReturn("userId");
    when(missingQuotaRequest.params(":quotaId")).thenReturn("missing_quotaId");

    when(missingUserTierRequest.params(":apiKey")).thenReturn("apiKey");
    when(missingUserTierRequest.params(":productId")).thenReturn("productId");
    when(missingUserTierRequest.params(":userId")).thenReturn("missing_userTier_userID");
    when(missingUserTierRequest.params(":quotaId")).thenReturn("quotaId");

    when(missingTierRequest.params(":apiKey")).thenReturn("apiKey");
    when(missingTierRequest.params(":productId")).thenReturn("productId");
    when(missingTierRequest.params(":userId")).thenReturn("userTier_missingTier_userID");
    when(missingTierRequest.params(":quotaId")).thenReturn("quotaId");

    when(database.getPartnerByApi("apiKey")).thenReturn(optPresentPartner);
    when(database.getPartnerByApi("missing_apiKey")).thenReturn(optMissingPartner);
    when(database.getQuota("partnerId", "productId", "quotaId")).thenReturn(optPresentQuota);
    when(database.getQuota("partnerId", "productId", "missing_quotaId")).thenReturn(optMissingQuota);
    when(database.getUserTier("partnerId", "productId", "userId", "quotaId")).thenReturn(optPresentUserTier);
    when(database.getUserTier("partnerId", "productId", "userTier_missingTier_userID", "quotaId")).thenReturn(optPresentUserTierMissingTier);
    when(database.getUserTier("partnerId", "productId", "missing_userTier_userID", "quotaId")).thenReturn(optMissingUserTier);
    when(database.getTier("partnerId", "productId", "quotaId", "tierId")).thenReturn(optPresentTier);
    when(database.getTier("partnerId", "productId", "quotaId", "missing_tierId")).thenReturn(optMissingTier);

    when(userTier.getTierId()).thenReturn("tierId");
  }

  @Test
  public void testGetMissingPartner() throws Exception {
    // execute
    try {
      getQuotaSDKController.handle(missingPartnerRequest, response);
    } catch (HaltException e) {
      // verify
      assertEquals(404, e.statusCode());
      assertEquals("Missing Partner", e.body());
      Mockito.verify(database);
      return;
    }
    fail("Exception not thrown");
  }

  @Test
  public void testGetMissingQuota() throws Exception {
    // execute
    try {
      getQuotaSDKController.handle(missingQuotaRequest, response);
    } catch (HaltException e) {
      // verify
      assertEquals(404, e.statusCode());
      assertEquals("Missing Quota", e.body());
      Mockito.verify(database);
      return;
    }
    fail("Exception not thrown");
  }

  @Test
  public void testGetMissingUserTier() throws Exception {
    // execute
    String actual = (String) getQuotaSDKController.handle(missingUserTierRequest, response);
    json.addProperty("max", "0");
    json.addProperty("value", "0");
    // verify
    assertEquals(json.toString(), actual);
    Mockito.verify(database);
  }

  @Test
  public void testGetMissingTier() throws Exception {
    // execute
    try {
      getQuotaSDKController.handle(missingTierRequest, response);
    } catch (HaltException e) {
      // verify
      assertEquals(404, e.statusCode());
      assertEquals("Missing Tier", e.body());
      Mockito.verify(database);
      return;
    }
    fail("Exception not thrown");
  }

  @Test
  public void testGetQuota() throws Exception {
    // execute
    String actual = (String) getQuotaSDKController.handle(request, response);
    json.addProperty("max", "100");
    json.addProperty("value", "52");
    // verify
    assertEquals(json.toString(), actual);
    Mockito.verify(database);

  }

  @Test(expected = Exception.class)
  public void testGetQuotaUnsuccessfulGet() throws Exception {
    Mockito.doThrow(new Exception()).when(database).getPartner("bad_apiKey");
    // execute
    getQuotaSDKController.handle(badRequest, response);
    // verify
    Mockito.verify(database);
  }
}
