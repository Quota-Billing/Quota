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

import edu.rosehulman.quota.controller.SetQuotaController;
import edu.rosehulman.quota.model.Partner;
import edu.rosehulman.quota.model.UserTier;
import spark.HaltException;
import spark.Request;
import spark.Response;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Database.class })
public class SetQuotaControllerTest {

  private Database database;
  private Request request;
  private Response response;
  private SetQuotaController setQuotaController;
  private UserTier userTier;

  @Before
  public void setUp() throws Exception {
    // Setup Mocks
    mockStatic(Database.class);
    database = mock(Database.class);
    request = mock(Request.class);
    response = mock(Response.class);
    Partner partner = mock(Partner.class);
    userTier = mock(UserTier.class);

    // Real Objects
    setQuotaController = new SetQuotaController();
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
  public void testSetQuota() throws Exception {
    Optional<UserTier> optionUT = Optional.of(userTier);
    when(database.getUserTier("partnerId", "productId", "userId", "quotaId")).thenReturn(optionUT);

    when(request.body()).thenReturn("");
    when(database.updateUserTier(userTier)).thenReturn(true);
    
    // execute
    String actualResponse = (String) setQuotaController.handle(request, response);

    // verify
    assertEquals("", actualResponse);
    Mockito.verify(database);
  }

  @Test(expected = HaltException.class)
  public void testNoUserTier() throws Exception {
    when(database.getUserTier("partnerId", "productId", "userId", "quotaId")).thenReturn(Optional.empty());

    // execute
    setQuotaController.handle(request, response);
  }

  @Test
  public void testSetQuotaSpecifiedInBody() throws Exception {
    Optional<UserTier> optionUT = Optional.of(userTier);
    when(database.getUserTier("partnerId", "productId", "userId", "quotaId")).thenReturn(optionUT);

    JsonObject body = new JsonObject();
    body.addProperty("count", "5");
    when(request.body()).thenReturn(body.toString());
    
    when(database.updateUserTier(userTier)).thenReturn(true);
    
    // execute
    String actualResponse = (String) setQuotaController.handle(request, response);

    // verify
    assertEquals("", actualResponse);
    Mockito.verify(database);
  }

  @Test(expected = HaltException.class)
  public void testDatabaseError() throws Exception {
    Optional<UserTier> optionUT = Optional.of(userTier);
    when(database.getUserTier("partnerId", "productId", "userId", "quotaId")).thenReturn(optionUT);

    when(request.body()).thenReturn("");
    when(database.updateUserTier(userTier)).thenReturn(false);
    
    // execute
    setQuotaController.handle(request, response);
  }

}
