package edu.rosehulman.quota;

import edu.rosehulman.quota.client.SharedServiceClient;
import edu.rosehulman.quota.controller.AddUserController;
import edu.rosehulman.quota.factories.UserFactory;
import edu.rosehulman.quota.model.Partner;
import edu.rosehulman.quota.model.User;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.google.gson.JsonObject;

import spark.Request;
import spark.Response;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.util.Optional;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Database.class, Request.class, Response.class, SharedServiceClient.class })
public class AddUserControllerTest {

  private Database database;
  private SharedServiceClient shared;
  private Request request;
  private Response response;
  private AddUserController addUserController;
  private JsonObject body;
  private UserFactory factory;
  private User user;
  private User badUser;
  private User badSharedUser;

  @Before
  public void setUp() throws Exception {
    // setup
    // mocks
    mockStatic(Database.class);
    mockStatic(SharedServiceClient.class);
    database = mock(Database.class);
    shared = mock(SharedServiceClient.class);
    when(Database.getInstance()).thenReturn(database);
    when(SharedServiceClient.getInstance()).thenReturn(shared);
    request = mock(Request.class);
    response = mock(Response.class);
    user = mock(User.class);
    badUser = mock(User.class);
    badSharedUser = mock(User.class);
    factory = mock(UserFactory.class);

    // real objects
    body = new JsonObject();
    addUserController = new AddUserController(factory);
    when(factory.createUser("partnerId", "productId", "badUserId")).thenReturn(badUser);
    when(factory.createUser("partnerId", "productId", "badSharedUserId")).thenReturn(badSharedUser);

    when(request.params(":apiKey")).thenReturn("apiKey");
    when(request.params(":productId")).thenReturn("productId");

    Partner partner = new Partner();
    partner.setPartnerId("partnerId");
    Optional<Partner> option = Optional.of(partner);
    when(database.getPartnerByApi("apiKey")).thenReturn(option);
  }

  @Test
  public void testAddUser() throws Exception {
    body.addProperty("id", "userId");
    when(request.body()).thenReturn(body.toString());
    when(factory.createUser("partnerId", "productId", "userId")).thenReturn(user);
    when(shared.addUser("partnerId", "productId", "userId")).thenReturn(true);
    Mockito.doNothing().when(database).addUser(user);

    // execute
    String actualResponse = (String) addUserController.handle(request, response);

    // verify
    assertEquals("", actualResponse);
    Mockito.verify(database);
  }

  @Test(expected = Exception.class)
  public void testAddUserException() throws Exception {
    body.addProperty("id", "badUserId");
    when(request.body()).thenReturn(body.toString());
    when(factory.createUser("partnerId", "productId", "badUserId")).thenReturn(badUser);
    Mockito.doThrow(new Exception()).when(database).addUser(badUser);

    // execute
    String actualResponse = (String) addUserController.handle(request, response);

    // verify
    assertEquals("", actualResponse);
    Mockito.verify(database);
  }

  @Test(expected = Exception.class)
  public void testAddUserSharedException() throws Exception {
    body.addProperty("id", "badSharedUserId");
    when(request.body()).thenReturn(body.toString());
    when(factory.createUser("partnerId", "productId", "badSharedUserId")).thenReturn(badSharedUser);
    when(shared.addUser("partnerId", "productId", "badSharedUserId")).thenReturn(false);
    Mockito.doThrow(new Exception()).when(database).addUser(badSharedUser);

    // execute
    String actualResponse = (String) addUserController.handle(request, response);

    // verify
    assertEquals("", actualResponse);
    Mockito.verify(database);
  }

}
