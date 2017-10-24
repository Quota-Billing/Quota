package edu.rosehulman.quota;

import edu.rosehulman.quota.controller.GetUserController;
import edu.rosehulman.quota.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import spark.Request;
import spark.Response;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Database.class, Request.class, Response.class, User.class })
public class GetUserControllerTest {

  @Test
  public void testGetUser() throws Exception {
    mockStatic(Database.class);
    Database database = mock(Database.class);
    when(Database.getInstance()).thenReturn(database);

    User user = mock(User.class);

    when(database.getUser("partner_id", "product_id", "user_id")).thenReturn(Optional.of(user));
    when(database.getUser("partner_id", "product_id", "bad_user_id")).thenReturn(Optional.empty());

    Request request = mock(Request.class);
    when(request.params(":partnerId")).thenReturn("partner_id");
    when(request.params(":productId")).thenReturn("product_id");
    when(request.params(":userId")).thenReturn("user_id");
    Response response = mock(Response.class);
    when(response.status()).thenReturn(200);

    GetUserController getUserController = new GetUserController();

    getUserController.handle(request, response);
    assertEquals(200, response.status());

    when(request.params(":userId")).thenReturn("bad_user_id");
    when(response.status()).thenReturn(404);

    getUserController.handle(request, response);
    assertEquals(404, response.status());
  }
}
