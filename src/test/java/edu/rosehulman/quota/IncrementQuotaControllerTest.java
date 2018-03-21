package edu.rosehulman.quota;

import static org.junit.Assert.assertEquals;
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
import java.util.ServiceConfigurationError;

import edu.rosehulman.quota.client.BillingClient;
import edu.rosehulman.quota.client.SharedServiceClient;
import edu.rosehulman.quota.controller.IncrementQuotaController;
import edu.rosehulman.quota.model.Partner;
import edu.rosehulman.quota.model.Quota;
import edu.rosehulman.quota.model.Tier;
import edu.rosehulman.quota.model.User;
import edu.rosehulman.quota.model.UserTier;
import spark.HaltException;
import spark.Request;
import spark.Response;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Database.class, SharedServiceClient.class, BillingClient.class })
public class IncrementQuotaControllerTest {

  private Database database;
  private Request request;
  private Response response;
  private IncrementQuotaController incrementQuotaController;
  private Quota quota;
  private User user;
  private UserTier userTier;
  private Tier tier;

  @Before
  public void setUp() throws Exception {
    // Setup Mocks
    mockStatic(Database.class);
    mockStatic(SharedServiceClient.class);
    database = mock(Database.class);
    request = mock(Request.class);
    response = mock(Response.class);
    Partner partner = mock(Partner.class);
    quota = mock(Quota.class);
    user = mock(User.class);
    userTier = mock(UserTier.class);
    tier = mock(Tier.class);

    // Real Objects
    incrementQuotaController = new IncrementQuotaController();
    Optional<Partner> optionPartner = Optional.of(partner);  
    
    // Conditionals
    when(Database.getInstance()).thenReturn(database);
    when(request.params(":apiKey")).thenReturn("apiKey");
    when(request.params(":productId")).thenReturn("productId");
    when(request.params(":userId")).thenReturn("userId");
    when(request.params(":quotaId")).thenReturn("quotaId");
    when(partner.getPartnerId()).thenReturn("partnerId");
    when(database.getPartnerByApi("apiKey")).thenReturn(optionPartner);
  }

  @Test
  public void testIncrement() throws Exception {
    Optional<User> optionUser = Optional.of(user);
    when(database.getUser("partnerId", "productId", "userId")).thenReturn(optionUser);
    when(user.isFrozen()).thenReturn(false);

    Optional<UserTier> optionUT = Optional.of(userTier);
    when(database.getUserTier("partnerId", "productId", "userId", "quotaId")).thenReturn(optionUT);
    when(userTier.getTierId()).thenReturn("tierId");
    when(userTier.getValue()).thenReturn("3");

    Optional<Tier> optionTier = Optional.of(tier);
    when(database.getTier("partnerId", "productId", "quotaId", "tierId")).thenReturn(optionTier);
    when(tier.getMax()).thenReturn("5");
    when(tier.getGraceExtra()).thenReturn("5");

    when(request.body()).thenReturn("");

    when(database.updateUserTier(userTier)).thenReturn(true);

    // execute
    String actualResponse = (String) incrementQuotaController.handle(request, response);

    // verify
    assertEquals("", actualResponse);
    Mockito.verify(database);
  }

  @Test
  public void testIncrementInGrace() throws Exception {
    Optional<User> optionUser = Optional.of(user);
    when(database.getUser("partnerId", "productId", "userId")).thenReturn(optionUser);
    when(user.isFrozen()).thenReturn(false);

    Optional<UserTier> optionUT = Optional.of(userTier);
    when(database.getUserTier("partnerId", "productId", "userId", "quotaId")).thenReturn(optionUT);
    when(userTier.getTierId()).thenReturn("tierId");
    when(userTier.getValue()).thenReturn("5");

    Optional<Tier> optionTier = Optional.of(tier);
    when(database.getTier("partnerId", "productId", "quotaId", "tierId")).thenReturn(optionTier);
    when(tier.getMax()).thenReturn("5");
    when(tier.getGraceExtra()).thenReturn("5");

    when(request.body()).thenReturn("");

    when(database.updateUserTier(userTier)).thenReturn(true);

    // execute
    String actualResponse = (String) incrementQuotaController.handle(request, response);

    JsonObject ret = new JsonObject();
    ret.addProperty("isGrace", true);
    
    // verify
    assertEquals(ret.toString(), actualResponse);
    Mockito.verify(database);
  }

  @Test(expected = HaltException.class)
  public void testIncrementDatabaseFail() throws Exception {
    Optional<User> optionUser = Optional.of(user);
    when(database.getUser("partnerId", "productId", "userId")).thenReturn(optionUser);
    when(user.isFrozen()).thenReturn(false);

    Optional<UserTier> optionUT = Optional.of(userTier);
    when(database.getUserTier("partnerId", "productId", "userId", "quotaId")).thenReturn(optionUT);
    when(userTier.getTierId()).thenReturn("tierId");
    when(userTier.getValue()).thenReturn("3");

    Optional<Tier> optionTier = Optional.of(tier);
    when(database.getTier("partnerId", "productId", "quotaId", "tierId")).thenReturn(optionTier);
    when(tier.getMax()).thenReturn("5");
    when(tier.getGraceExtra()).thenReturn("5");

    when(request.body()).thenReturn("");

    when(database.updateUserTier(userTier)).thenReturn(false);

    // execute
    incrementQuotaController.handle(request, response);
  }

  @Test(expected = HaltException.class)
  public void testIncrementTimeAboveQuota() throws Exception {
    Optional<User> optionUser = Optional.of(user);
    when(database.getUser("partnerId", "productId", "userId")).thenReturn(optionUser);
    when(user.isFrozen()).thenReturn(false);

    Optional<UserTier> optionUT = Optional.of(userTier);
    when(database.getUserTier("partnerId", "productId", "userId", "quotaId")).thenReturn(optionUT);
    when(userTier.getTierId()).thenReturn("tierId");
    when(userTier.getValue()).thenReturn("1");

    Optional<Tier> optionTier = Optional.of(tier);
    when(database.getTier("partnerId", "productId", "quotaId", "tierId")).thenReturn(optionTier);
    when(tier.getMax()).thenReturn("5");
    when(tier.getGraceExtra()).thenReturn("5");

    JsonObject body = new JsonObject();
    body.addProperty("count", "2hr");
    when(request.body()).thenReturn(body.toString());
    
    Optional<Quota> optionQuota = Optional.of(quota);
    when(database.getQuota("partnerId", "productId", "quotaId")).thenReturn(optionQuota);
    when(quota.getType()).thenReturn("time");

    mockStatic(BillingClient.class);
    BillingClient billing = mock(BillingClient.class);
    when(BillingClient.getInstance()).thenReturn(billing);
    when(billing.quotaReached("partnerId", "productId", "userId", "quotaId", "tierId")).thenReturn("bill");

    // execute
    incrementQuotaController.handle(request, response);
  }

  @Test
  public void testIncrementTime() throws Exception {
    Optional<User> optionUser = Optional.of(user);
    when(database.getUser("partnerId", "productId", "userId")).thenReturn(optionUser);
    when(user.isFrozen()).thenReturn(false);

    Optional<UserTier> optionUT = Optional.of(userTier);
    when(database.getUserTier("partnerId", "productId", "userId", "quotaId")).thenReturn(optionUT);
    when(userTier.getTierId()).thenReturn("tierId");
    when(userTier.getValue()).thenReturn("1");

    Optional<Tier> optionTier = Optional.of(tier);
    when(database.getTier("partnerId", "productId", "quotaId", "tierId")).thenReturn(optionTier);
    when(tier.getMax()).thenReturn("5");
    when(tier.getGraceExtra()).thenReturn("5");

    JsonObject body = new JsonObject();
    body.addProperty("count", "2s");
    when(request.body()).thenReturn(body.toString());
    
    Optional<Quota> optionQuota = Optional.of(quota);
    when(database.getQuota("partnerId", "productId", "quotaId")).thenReturn(optionQuota);
    when(quota.getType()).thenReturn("time");

    when(database.updateUserTier(userTier)).thenReturn(true);

    // execute
    String actualResponse = (String) incrementQuotaController.handle(request, response);

    // verify
    assertEquals("", actualResponse);
    Mockito.verify(database);
  }

  @Test(expected = HaltException.class)
  public void testIncrementStorageAboveQuota() throws Exception {
    Optional<User> optionUser = Optional.of(user);
    when(database.getUser("partnerId", "productId", "userId")).thenReturn(optionUser);
    when(user.isFrozen()).thenReturn(false);

    Optional<UserTier> optionUT = Optional.of(userTier);
    when(database.getUserTier("partnerId", "productId", "userId", "quotaId")).thenReturn(optionUT);
    when(userTier.getTierId()).thenReturn("tierId");
    when(userTier.getValue()).thenReturn("1");

    Optional<Tier> optionTier = Optional.of(tier);
    when(database.getTier("partnerId", "productId", "quotaId", "tierId")).thenReturn(optionTier);
    when(tier.getMax()).thenReturn("5");
    when(tier.getGraceExtra()).thenReturn("5");

    JsonObject body = new JsonObject();
    body.addProperty("count", "2MB");
    when(request.body()).thenReturn(body.toString());
    
    Optional<Quota> optionQuota = Optional.of(quota);
    when(database.getQuota("partnerId", "productId", "quotaId")).thenReturn(optionQuota);
    when(quota.getType()).thenReturn("storage");

    mockStatic(BillingClient.class);
    BillingClient billing = mock(BillingClient.class);
    when(BillingClient.getInstance()).thenReturn(billing);
    when(billing.quotaReached("partnerId", "productId", "userId", "quotaId", "tierId")).thenReturn("bill");

    // execute
    incrementQuotaController.handle(request, response);
  }

  @Test
  public void testIncrementStorage() throws Exception {
    Optional<User> optionUser = Optional.of(user);
    when(database.getUser("partnerId", "productId", "userId")).thenReturn(optionUser);
    when(user.isFrozen()).thenReturn(false);

    Optional<UserTier> optionUT = Optional.of(userTier);
    when(database.getUserTier("partnerId", "productId", "userId", "quotaId")).thenReturn(optionUT);
    when(userTier.getTierId()).thenReturn("tierId");
    when(userTier.getValue()).thenReturn("1");

    Optional<Tier> optionTier = Optional.of(tier);
    when(database.getTier("partnerId", "productId", "quotaId", "tierId")).thenReturn(optionTier);
    when(tier.getMax()).thenReturn("5");
    when(tier.getGraceExtra()).thenReturn("5");

    JsonObject body = new JsonObject();
    body.addProperty("count", "2B");
    when(request.body()).thenReturn(body.toString());
    
    Optional<Quota> optionQuota = Optional.of(quota);
    when(database.getQuota("partnerId", "productId", "quotaId")).thenReturn(optionQuota);
    when(quota.getType()).thenReturn("storage");

    when(database.updateUserTier(userTier)).thenReturn(true);

    // execute
    String actualResponse = (String) incrementQuotaController.handle(request, response);

    // verify
    assertEquals("", actualResponse);
    Mockito.verify(database);
  }

  @Test
  public void testIncrementMoreThanOne() throws Exception {
    Optional<User> optionUser = Optional.of(user);
    when(database.getUser("partnerId", "productId", "userId")).thenReturn(optionUser);
    when(user.isFrozen()).thenReturn(false);

    Optional<UserTier> optionUT = Optional.of(userTier);
    when(database.getUserTier("partnerId", "productId", "userId", "quotaId")).thenReturn(optionUT);
    when(userTier.getTierId()).thenReturn("tierId");
    when(userTier.getValue()).thenReturn("1");

    Optional<Tier> optionTier = Optional.of(tier);
    when(database.getTier("partnerId", "productId", "quotaId", "tierId")).thenReturn(optionTier);
    when(tier.getMax()).thenReturn("5");
    when(tier.getGraceExtra()).thenReturn("5");

    JsonObject body = new JsonObject();
    body.addProperty("count", "2");
    when(request.body()).thenReturn(body.toString());
    
    Optional<Quota> optionQuota = Optional.of(quota);
    when(database.getQuota("partnerId", "productId", "quotaId")).thenReturn(optionQuota);
    when(quota.getType()).thenReturn("numeric");

    when(database.updateUserTier(userTier)).thenReturn(true);

    // execute
    String actualResponse = (String) incrementQuotaController.handle(request, response);

    // verify
    assertEquals("", actualResponse);
    Mockito.verify(database);
  }

  @Test(expected = ServiceConfigurationError.class)
  public void testAboveQuotaBadBill() throws Exception {
    Optional<User> optionUser = Optional.of(user);
    when(database.getUser("partnerId", "productId", "userId")).thenReturn(optionUser);
    when(user.isFrozen()).thenReturn(false);

    Optional<UserTier> optionUT = Optional.of(userTier);
    when(database.getUserTier("partnerId", "productId", "userId", "quotaId")).thenReturn(optionUT);
    when(userTier.getTierId()).thenReturn("tierId");
    when(userTier.getValue()).thenReturn("11");

    Optional<Tier> optionTier = Optional.of(tier);
    when(database.getTier("partnerId", "productId", "quotaId", "tierId")).thenReturn(optionTier);
    when(tier.getMax()).thenReturn("5");
    when(tier.getGraceExtra()).thenReturn("5");

    mockStatic(BillingClient.class);
    BillingClient billing = mock(BillingClient.class);
    when(BillingClient.getInstance()).thenReturn(billing);
    when(billing.quotaReached("partnerId", "productId", "userId", "quotaId", "tierId")).thenReturn(null);
    
    // execute
    incrementQuotaController.handle(request, response);
  }
  @Test(expected = HaltException.class)
  public void testAboveQuotaGoodBill() throws Exception {
    Optional<User> optionUser = Optional.of(user);
    when(database.getUser("partnerId", "productId", "userId")).thenReturn(optionUser);
    when(user.isFrozen()).thenReturn(false);

    Optional<UserTier> optionUT = Optional.of(userTier);
    when(database.getUserTier("partnerId", "productId", "userId", "quotaId")).thenReturn(optionUT);
    when(userTier.getTierId()).thenReturn("tierId");
    when(userTier.getValue()).thenReturn("11");

    Optional<Tier> optionTier = Optional.of(tier);
    when(database.getTier("partnerId", "productId", "quotaId", "tierId")).thenReturn(optionTier);
    when(tier.getMax()).thenReturn("5");
    when(tier.getGraceExtra()).thenReturn("5");

    mockStatic(BillingClient.class);
    BillingClient billing = mock(BillingClient.class);
    when(BillingClient.getInstance()).thenReturn(billing);
    when(billing.quotaReached("partnerId", "productId", "userId", "quotaId", "tierId")).thenReturn("bill");
    
    // execute
    incrementQuotaController.handle(request, response);
  }

  @Test(expected = HaltException.class)
  public void testNoTier() throws Exception {
    Optional<User> optionUser = Optional.of(user);
    when(database.getUser("partnerId", "productId", "userId")).thenReturn(optionUser);
    when(user.isFrozen()).thenReturn(false);

    Optional<UserTier> optionUT = Optional.of(userTier);
    when(database.getUserTier("partnerId", "productId", "userId", "quotaId")).thenReturn(optionUT);
    when(userTier.getTierId()).thenReturn("tierId");

    when(database.getTier("partnerId", "productId", "quotaId", "tierId")).thenReturn(Optional.empty());

    // execute
    incrementQuotaController.handle(request, response);
  }

  @Test(expected = HaltException.class)
  public void testNoUserTier() throws Exception {
    Optional<User> optionUser = Optional.of(user);
    when(database.getUser("partnerId", "productId", "userId")).thenReturn(optionUser);
    when(user.isFrozen()).thenReturn(false);

    when(database.getUserTier("partnerId", "productId", "userId", "quotaId")).thenReturn(Optional.empty());

    // execute
    incrementQuotaController.handle(request, response);
  }

  @Test(expected = HaltException.class)
  public void testFrozenUser() throws Exception {
    Optional<User> optionUser = Optional.of(user);
    when(database.getUser("partnerId", "productId", "userId")).thenReturn(optionUser);
    when(user.isFrozen()).thenReturn(true);

    // execute
    incrementQuotaController.handle(request, response);
  }

  @Test(expected = HaltException.class)
  public void testNoUser() throws Exception {
    when(database.getUser("partnerId", "productId", "userId")).thenReturn(Optional.empty());
    
    // execute
    incrementQuotaController.handle(request, response);
  }

}
