package edu.rosehulman.quota;

import edu.rosehulman.quota.client.SharedServiceClient;
import edu.rosehulman.quota.controller.DeleteUserController;
import edu.rosehulman.quota.model.Partner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import spark.Request;
import spark.Response;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.util.Optional;
import java.util.ServiceConfigurationError;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Database.class, Request.class, Response.class, SharedServiceClient.class })
public class DeleteUserControllerTest {

  Database database;
  SharedServiceClient shared;
  Request request;
  Response response;
  DeleteUserController deleteUserController;
  Partner partner;

  @Before
  public void setup() throws Exception {
    // Setup Mocks
    mockStatic(Database.class);
    mockStatic(SharedServiceClient.class);
    database = mock(Database.class);
    shared = mock(SharedServiceClient.class);
    request = mock(Request.class);
    response = mock(Response.class);
    partner = mock(Partner.class);

    // Real Objects
    deleteUserController = new DeleteUserController();
    Optional<Partner> option = Optional.of(partner);
    
    // Conditionals
    when(Database.getInstance()).thenReturn(database);
    when(SharedServiceClient.getInstance()).thenReturn(shared);
    when(request.params(":apiKey")).thenReturn("apiKey");
    when(request.params(":productId")).thenReturn("productId");
    when(partner.getPartnerId()).thenReturn("partnerId");
    when(database.getPartnerByApi("apiKey")).thenReturn(option);
  }

  @Test
  public void testDeleteUser() throws Exception {
    when(request.params(":userId")).thenReturn("userId");
    when(database.deleteUser("partnerId", "productId", "userId")).thenReturn(true);
    when(shared.deleteUser("partnerId", "productId", "userId")).thenReturn(true);

    // execute
    String actualResponse = (String) deleteUserController.handle(request, response);

    // verify
    assertEquals("", actualResponse);
    Mockito.verify(database);
  }

  @Test(expected = Exception.class)
  public void testDeleteUserException() throws Exception {
    when(request.params(":userId")).thenReturn("badUserId");
    when(database.deleteUser("partnerId", "productId", "badUserId")).thenReturn(false);
    when(shared.deleteUser("partnerId", "productId", "badUserId")).thenReturn(true);

    // execute
    deleteUserController.handle(request, response);
  }

  @Test(expected = ServiceConfigurationError.class)
  public void testDeleteUserSharedException() throws Exception {
    when(request.params(":userId")).thenReturn("badSharedUserId");
    when(database.deleteUser("partnerId", "productId", "badSharedUserId")).thenReturn(true);
    when(shared.deleteUser("partnerId", "productId", "badUserId")).thenReturn(false);

    // execute
    deleteUserController.handle(request, response);
  }

}
