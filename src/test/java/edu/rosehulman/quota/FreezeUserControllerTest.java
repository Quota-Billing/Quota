package edu.rosehulman.quota;

import edu.rosehulman.quota.controller.FreezeUserController;
import edu.rosehulman.quota.model.Partner;
import edu.rosehulman.quota.model.User;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.google.gson.JsonObject;

import spark.HaltException;
import spark.Request;
import spark.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.util.Optional;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Database.class, Request.class, Response.class })
public class FreezeUserControllerTest {

  Database database;
  Request emptyBodyRequest;
  Request unFreezeRequest;
  Request missingUserRequest;
  Request badRequest;
  Response response;
  Optional<User> optPresentUser;
  Optional<User> optMissingUser;
  Optional<User> optBadUser;
  User user = new User();
  User badUser = new User();
  FreezeUserController freezeUserController;
  Optional<Partner> optPartner;
  Partner partner;
  JsonObject body;

  @Before
  public void setup() throws Exception {
    // setup
    // mocks
    mockStatic(Database.class);
    database = Mockito.mock(Database.class);
    emptyBodyRequest = mock(Request.class);
    unFreezeRequest = mock(Request.class);
    badRequest = mock(Request.class);
    missingUserRequest = mock(Request.class);
    response = mock(Response.class);
    partner = mock(Partner.class);
    optPresentUser = Optional.of(user);
    optBadUser = Optional.of(badUser);
    optMissingUser = Optional.empty();
    optPartner = Optional.of(partner);
    // badPartner = mock(Partner.class);
    // uesrFactory = mock(UserFactory.class);

    // real objects
    freezeUserController = new FreezeUserController();
    body = new JsonObject();
    body.addProperty("freeze", "false");

    // returns
    when(emptyBodyRequest.params(":apiKey")).thenReturn("apiKey");
    when(emptyBodyRequest.params(":productId")).thenReturn("productId");
    when(emptyBodyRequest.params(":userId")).thenReturn("userId");
    when(emptyBodyRequest.body()).thenReturn("");
    when(unFreezeRequest.params(":apiKey")).thenReturn("apiKey");
    when(unFreezeRequest.params(":productId")).thenReturn("productId");
    when(unFreezeRequest.params(":userId")).thenReturn("userId");
    when(unFreezeRequest.body()).thenReturn(body.toString());
    when(missingUserRequest.params(":apiKey")).thenReturn("apiKey");
    when(missingUserRequest.params(":productId")).thenReturn("productId");
    when(missingUserRequest.params(":userId")).thenReturn("missing_userId");
    when(badRequest.params(":apiKey")).thenReturn("apiKey");
    when(badRequest.params(":productId")).thenReturn("productId");
    when(badRequest.params(":userId")).thenReturn("bad_userId");
    when(badRequest.body()).thenReturn("");
    when(Database.getInstance()).thenReturn(database);
    when(database.getPartnerByApi("apiKey")).thenReturn(optPartner);
    when(partner.getPartnerId()).thenReturn("partnerId");
    when(database.getUser("partnerId", "productId", "userId")).thenReturn(optPresentUser);
    when(database.getUser("partnerId", "productId", "missing_userId")).thenReturn(optMissingUser);
    when(database.getUser("partnerId", "productId", "bad_userId")).thenReturn(optBadUser);
    when(database.updateUser(user)).thenReturn(true);
    when(database.updateUser(badUser)).thenReturn(false);

    // expects
    Mockito.doNothing().when(database).addPartner(partner);
  }

  @Test
  public void testFreezeMissingUser() throws Exception {
    // execute
    try {
      freezeUserController.handle(missingUserRequest, response);
    } catch (HaltException e) {
      assertEquals(404, e.statusCode());
      Mockito.verify(database);
      return;
    }
    fail();
  }

  @Test
  public void testFreezeUserEmptyBody() throws Exception {
    // execute
    String actualResponse = (String) freezeUserController.handle(emptyBodyRequest, response);
    // verify
    assertEquals("", actualResponse);
    Mockito.verify(database);
  }

  @Test
  public void testUnFreezeUser() throws Exception {
    // execute
    String actualResponse = (String) freezeUserController.handle(unFreezeRequest, response);
    // verify
    assertEquals("", actualResponse);
    Mockito.verify(database);
  }

  @Test
  public void testFreezeUserUnsuccessfulUpdate() throws Exception {
    // execute
    try {
      freezeUserController.handle(badRequest, response);
    } catch (HaltException e) {
      assertEquals(500, e.statusCode());
      Mockito.verify(database);
      return;
    }
    fail();
  }

}
