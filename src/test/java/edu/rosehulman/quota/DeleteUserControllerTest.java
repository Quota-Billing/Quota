package edu.rosehulman.quota;

import edu.rosehulman.quota.client.SharedServiceClient;
import edu.rosehulman.quota.controller.DeleteUserController;
import edu.rosehulman.quota.model.Partner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import spark.Request;
import spark.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.util.Optional;

import org.apache.http.HttpException;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Database.class, Request.class, Response.class, SharedServiceClient.class})
public class DeleteUserControllerTest {

  @Test
  public void testDeleteUser() throws Exception {
    mockStatic(Database.class);
    Database database = mock(Database.class);
    when(Database.getInstance()).thenReturn(database);

    DeleteUserController deleteUserController = new DeleteUserController();
    Request request = mock(Request.class);
    Response response = mock(Response.class);

    when(request.params(":apiKey")).thenReturn("apiKey");
    when(request.params(":productId")).thenReturn("prod_id1");
    when(request.params(":userId")).thenReturn("user_id1");
    when(response.status()).thenReturn(200);

    Partner partner = mock(Partner.class);

    when(partner.getPartnerId()).thenReturn("part_id1");
    when(database.getPartnerByApi("apiKey")).thenReturn(Optional.of(partner));

    when(database.deleteUser("part_id1", "prod_id1", "user_id1")).thenReturn(true);

    mockStatic(SharedServiceClient.class);
    SharedServiceClient client = mock(SharedServiceClient.class);
    when(SharedServiceClient.getInstance()).thenReturn(client);
    when(client.deleteUser("part_id1", "prod_id1", "user_id1")).thenReturn(true);

    deleteUserController.handle(request, response);

    assertEquals(200, response.status()); // or 202

    when(request.params(":userId")).thenReturn("bad_user_id1");
    when(response.status()).thenReturn(404);
    when(client.deleteUser("part_id1", "prod_id1", "bad_user_id1")).thenReturn(false);

    assertEquals(404, response.status());
  }
}
