package edu.rosehulman.quota;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.util.Optional;
import java.util.ServiceConfigurationError;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.google.gson.JsonObject;

import edu.rosehulman.quota.client.BillingClient;
import edu.rosehulman.quota.controller.SetUserTierController;
import edu.rosehulman.quota.factories.UserTierFactory;
import edu.rosehulman.quota.model.Partner;
import edu.rosehulman.quota.model.UserTier;
import spark.HaltException;
import spark.Request;
import spark.Response;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ Database.class, BillingClient.class })
public class SetUserTierControllerTest {

  private Database database;
  private BillingClient billing;
  private Request request;
  private Response response;
  private SetUserTierController setUserTierController;
  private UserTierFactory factory;
  private UserTier userTier;

  @Before
  public void setUp() throws Exception {
    // Setup Mocks
    mockStatic(Database.class);
    mockStatic(BillingClient.class);
    database = mock(Database.class);
    billing = mock(BillingClient.class);
    request = mock(Request.class);
    response = mock(Response.class);
    factory = mock(UserTierFactory.class);
    Partner partner = mock(Partner.class);
    userTier = mock(UserTier.class);

    // Real Objects
    setUserTierController = new SetUserTierController(factory);
    Optional<Partner> option = Optional.of(partner);
    
    // Conditionals
    when(Database.getInstance()).thenReturn(database);
    when(BillingClient.getInstance()).thenReturn(billing);
    when(request.params(":apiKey")).thenReturn("apiKey");
    when(request.params(":productId")).thenReturn("productId");
    when(request.params(":userId")).thenReturn("userId");
    when(request.params(":quotaId")).thenReturn("quotaId");
    when(request.params(":tierId")).thenReturn("tierId");
    when(partner.getPartnerId()).thenReturn("partnerId");
    when(database.getPartnerByApi("apiKey")).thenReturn(option);
  }

  @Test
  public void testSetUserTier() throws Exception {
    when(request.body()).thenReturn("");
    
    Optional<UserTier> optional = Optional.of(userTier);
    when(database.getUserTier("partnerId", "productId", "userId", "quotaId")).thenReturn(optional);
    when(factory.createUserTier("partnerId", "productId", "quotaId", "tierId", "userId", "0")).thenReturn(userTier);
    when(database.deleteUserTier("partnerId", "productId", "userId", "quotaId")).thenReturn(true);
    Mockito.doNothing().when(database).addUserTier(userTier);
    when(billing.setUserTier("partnerId", "productId", "quotaId", "tierId", "userId")).thenReturn(true);

    // execute
    String actualResponse = (String) setUserTierController.handle(request, response);

    // verify
    assertEquals("", actualResponse);
    Mockito.verify(database);
  }

  @Test
  public void testSetUserTierRollover() throws Exception {
    JsonObject body = new JsonObject();
    body.addProperty("rollover", "true");
    when(request.body()).thenReturn(body.toString());
    when(userTier.getValue()).thenReturn("5");
    
    Optional<UserTier> optional = Optional.of(userTier);
    when(database.getUserTier("partnerId", "productId", "userId", "quotaId")).thenReturn(optional);
    when(factory.createUserTier("partnerId", "productId", "quotaId", "tierId", "userId", "5")).thenReturn(userTier);
    when(database.deleteUserTier("partnerId", "productId", "userId", "quotaId")).thenReturn(true);
    Mockito.doNothing().when(database).addUserTier(userTier);
    when(billing.setUserTier("partnerId", "productId", "quotaId", "tierId", "userId")).thenReturn(true);

    // execute
    String actualResponse = (String) setUserTierController.handle(request, response);

    // verify
    assertEquals("", actualResponse);
    Mockito.verify(database);
  }

  @Test(expected = ServiceConfigurationError.class)
  public void testBillingError() throws Exception {
    when(request.body()).thenReturn("");
    
    Optional<UserTier> optional = Optional.of(userTier);
    when(database.getUserTier("partnerId", "productId", "userId", "quotaId")).thenReturn(optional);
    when(factory.createUserTier("partnerId", "productId", "quotaId", "tierId", "userId", "0")).thenReturn(userTier);
    when(database.deleteUserTier("partnerId", "productId", "userId", "quotaId")).thenReturn(true);
    Mockito.doNothing().when(database).addUserTier(userTier);
    when(billing.setUserTier("partnerId", "productId", "quotaId", "tierId", "userId")).thenReturn(false);

    // execute
    setUserTierController.handle(request, response);
  }

  @Test(expected = Exception.class)
  public void testSetBadUserTier() throws Exception {
    when(request.body()).thenReturn("");
    
    Optional<UserTier> optional = Optional.of(userTier);
    when(database.getUserTier("partnerId", "productId", "userId", "quotaId")).thenReturn(optional);
    when(factory.createUserTier("partnerId", "productId", "quotaId", "tierId", "userId", "0")).thenReturn(userTier);
    when(database.deleteUserTier("partnerId", "productId", "userId", "quotaId")).thenReturn(true);
    Mockito.doThrow(new Exception()).when(database).addUserTier(userTier);
    
    // execute
    setUserTierController.handle(request, response);
  }

  @Test
  public void testNoPartner() throws Exception {
    when(database.getPartnerByApi("apiKey")).thenReturn(Optional.empty());
    
    // execute
    try {
      setUserTierController.handle(request, response);
    } catch (HaltException e) {
      assertEquals(404, e.statusCode());
      assertEquals("Partner not present", e.body());
      Mockito.verify(database);
      return;
    }
    fail();
  }
}
