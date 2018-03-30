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

import edu.rosehulman.quota.controller.GetQuotaBillingController;
import edu.rosehulman.quota.model.Quota;
import edu.rosehulman.quota.model.Tier;
import edu.rosehulman.quota.model.UserTier;
import spark.HaltException;
import spark.Request;
import spark.Response;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Database.class })
public class GetQuotaBillingControllerTest {

  private Database database;
  private Request request;
  private Response response;
  private GetQuotaBillingController getQuotaBillingController;
  private Quota quota;
  private UserTier userTier;
  private Tier tier;

  @Before
  public void setUp() throws Exception {
    // Setup Mocks
    mockStatic(Database.class);
    database = mock(Database.class);
    request = mock(Request.class);
    response = mock(Response.class);
    quota = mock(Quota.class);
    userTier = mock(UserTier.class);
    tier = mock(Tier.class);

    // Real Objects
    getQuotaBillingController = new GetQuotaBillingController();
    
    // Conditionals
    when(Database.getInstance()).thenReturn(database);
    when(request.params(":partnerId")).thenReturn("partnerId");
    when(request.params(":productId")).thenReturn("productId");
    when(request.params(":userId")).thenReturn("userId");
    when(request.params(":quotaId")).thenReturn("quotaId");
    when(userTier.getTierId()).thenReturn("tierId");
  }

  @Test
  public void testGetQuota() throws Exception {
    when(userTier.getValue()).thenReturn("3");
    when(tier.getTierId()).thenReturn("tierId");
    when(tier.getMax()).thenReturn("5");
    JsonObject ret = new JsonObject();
    ret.addProperty("tierId", "tierId");
    ret.addProperty("max", "5");
    ret.addProperty("value", "3");
    
    Optional<Quota> optionQuota = Optional.of(quota);
    when(database.getQuota("partnerId", "productId", "quotaId")).thenReturn(optionQuota);

    Optional<UserTier> optionUT = Optional.of(userTier);
    when(database.getUserTier("partnerId", "productId", "userId", "quotaId")).thenReturn(optionUT);

    Optional<Tier> optionTier = Optional.of(tier);
    when(database.getTier("partnerId", "productId", "quotaId", "tierId")).thenReturn(optionTier);
    
    // execute
    String actualResponse = (String) getQuotaBillingController.handle(request, response);

    // verify
    assertEquals(ret.toString(), actualResponse);
    Mockito.verify(database);
  }

  @Test
  public void testGetQuotaException() throws Exception {
    when(database.getQuota("partnerId", "productId", "quotaId")).thenReturn(Optional.empty());

    // execute
    try {
      getQuotaBillingController.handle(request, response);
    } catch (HaltException e) {
      assertEquals(404, e.statusCode());
      assertEquals("Quota not present", e.body());
      Mockito.verify(database);
      return;
    }
    fail();
  }

  @Test
  public void testGetUserTierException() throws Exception {
    Optional<Quota> optionQuota = Optional.of(quota);
    when(database.getQuota("partnerId", "productId", "quotaId")).thenReturn(optionQuota);

    when(database.getUserTier("partnerId", "productId", "userId", "quotaId")).thenReturn(Optional.empty());

    // execute
    try {
      getQuotaBillingController.handle(request, response);
    } catch (HaltException e) {
      assertEquals(404, e.statusCode());
      assertEquals("User Tier not present", e.body());
      Mockito.verify(database);
      return;
    }
    fail();
  }

  @Test
  public void testGetTierException() throws Exception {
    Optional<Quota> optionQuota = Optional.of(quota);
    when(database.getQuota("partnerId", "productId", "quotaId")).thenReturn(optionQuota);

    Optional<UserTier> optionUT = Optional.of(userTier);
    when(database.getUserTier("partnerId", "productId", "userId", "quotaId")).thenReturn(optionUT);

    when(database.getTier("partnerId", "productId", "quotaId", "tierId")).thenReturn(Optional.empty());
    
    // execute
    try {
      getQuotaBillingController.handle(request, response);
    } catch (HaltException e) {
      assertEquals(404, e.statusCode());
      assertEquals("Tier not present", e.body());
      Mockito.verify(database);
      return;
    }
    fail();
  }

}
