package edu.rosehulman.quota;

import edu.rosehulman.quota.controller.GetUserController;
import edu.rosehulman.quota.model.Partner;
import edu.rosehulman.quota.model.User;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import spark.HaltException;
import spark.Request;
import spark.Response;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Database.class, Request.class, Response.class, User.class })
public class GetUserControllerTest {

  private Database database;
  private Request request;
  private Request badRequest;
  private Request missingPartnerRequest;
  private Request missingUserRequest;
  private Response response;
  private GetUserController getUserController;

  @Before
  public void setUp() throws Exception {
    Optional<User> optPresentUser;
    Optional<User> optMissingUser;
    Optional<Partner> optPresentPartner;
    Optional<Partner> optMissingPartner;
    User user;
    Partner partner;
    
    // setup mocks
    mockStatic(Database.class);
    database = mock(Database.class);
    request = mock(Request.class);
    badRequest = mock(Request.class);
    missingPartnerRequest = mock(Request.class);
    missingUserRequest = mock(Request.class);
    response = mock(Response.class);
    partner = mock(Partner.class);
    user = mock(User.class);

    // real Objects
    getUserController = new GetUserController();
    optPresentUser = Optional.of(user);
    optMissingUser = Optional.empty();
    optPresentPartner = Optional.of(partner);
    optMissingPartner = Optional.empty();

    // returns
    when(partner.getPartnerId()).thenReturn("partnerId");

    when(user.getUserId()).thenReturn("userId");

    when(Database.getInstance()).thenReturn(database);
    when(request.params(":apiKey")).thenReturn("apiKey");
    when(request.params(":productId")).thenReturn("productId");
    when(request.params(":userId")).thenReturn("userId");

    when(badRequest.params(":apiKey")).thenReturn("bad_apiKey");
    when(badRequest.params(":productId")).thenReturn("productId");
    when(badRequest.params(":userId")).thenReturn("userId");

    when(missingPartnerRequest.params(":apiKey")).thenReturn("missing_apiKey");
    when(missingPartnerRequest.params(":productId")).thenReturn("productId");
    when(missingPartnerRequest.params(":userId")).thenReturn("userId");

    when(missingUserRequest.params(":apiKey")).thenReturn("apiKey");
    when(missingUserRequest.params(":productId")).thenReturn("productId");
    when(missingUserRequest.params(":userId")).thenReturn("missing_userID");
    when(missingUserRequest.params(":quotaId")).thenReturn("quotaId");

    when(database.getPartnerByApi("apiKey")).thenReturn(optPresentPartner);
    when(database.getPartnerByApi("missing_apiKey")).thenReturn(optMissingPartner);
    when(database.getUser("partnerId", "productId", "userId")).thenReturn(optPresentUser);
    when(database.getUser("partnerId", "productId", "missing_userID")).thenReturn(optMissingUser);
  }

  @Test
  public void testGetMissingPartner() throws Exception {
    // execute
    try {
      getUserController.handle(missingPartnerRequest, response);
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
  public void testGetMissingUser() throws Exception {
    // execute
    try {
      getUserController.handle(missingUserRequest, response);
    } catch (HaltException e) {
      // verify
      assertEquals(404, e.statusCode());
      assertEquals("Missing User", e.body());
      Mockito.verify(database);
      return;
    }
    fail("Exception not thrown");
  }

  @Test
  public void testGetUser() throws Exception {
    // execute
    String actual = (String) getUserController.handle(request, response);

    // verify
    assertEquals("userId", actual);
    Mockito.verify(database);
  }

  @Test(expected = Exception.class)
  public void testGetUserUnsuccessfulGet() throws Exception {
    Mockito.doThrow(new Exception()).when(database).getPartner("bad_apiKey");

    // execute
    getUserController.handle(badRequest, response);
    // verify
    Mockito.verify(database);
  }

}
