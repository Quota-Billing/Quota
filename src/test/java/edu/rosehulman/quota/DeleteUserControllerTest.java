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

import spark.HaltException;
import spark.Request;
import spark.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.util.Optional;
import java.util.ServiceConfigurationError;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Database.class, Request.class, Response.class, SharedServiceClient.class })
public class DeleteUserControllerTest {

  private Database database;
  private SharedServiceClient shared;
  private Request request;
  private Response response;
  private DeleteUserController deleteUserController;

  @Before
  public void setUp() throws Exception {
    // Setup Mocks
    mockStatic(Database.class);
    mockStatic(SharedServiceClient.class);
    database = mock(Database.class);
    shared = mock(SharedServiceClient.class);
    request = mock(Request.class);
    response = mock(Response.class);
    Partner partner = mock(Partner.class);

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

  @Test
  public void testDeleteUserException() throws Exception {
    when(request.params(":userId")).thenReturn("badUserId");
    when(database.deleteUser("partnerId", "productId", "badUserId")).thenReturn(false);
    when(shared.deleteUser("partnerId", "productId", "badUserId")).thenReturn(true);

    // execute
    try {
      deleteUserController.handle(request, response);
    } catch (HaltException e) {
      assertEquals(404, e.statusCode());
      assertEquals("Deleting user in database failed", e.body());
      Mockito.verify(database);
      return;
    }
    fail();
  }

  @Test(expected = ServiceConfigurationError.class)
  public void testDeleteUserSharedException() throws Exception {
    when(request.params(":userId")).thenReturn("badSharedUserId");
    when(database.deleteUser("partnerId", "productId", "badSharedUserId")).thenReturn(true);
    when(shared.deleteUser("partnerId", "productId", "badUserId")).thenReturn(false);

    // execute
    deleteUserController.handle(request, response);
  }

  @Test
  public void testNoPartner() throws Exception {
    when(database.getPartnerByApi("apiKey")).thenReturn(Optional.empty());
    
    // execute
    try {
      deleteUserController.handle(request, response);
    } catch (HaltException e) {
      assertEquals(404, e.statusCode());
      assertEquals("Partner not present", e.body());
      Mockito.verify(database);
      return;
    }
    fail();
  }
}
