package edu.rosehulman.quota;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import spark.Request;
import spark.Response;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Database.class, Request.class, Response.class})
public class TestAddUserController {

  @Test
  public void testAddUser() throws Exception {
    mockStatic(Database.class);
    Database database = Mockito.mock(Database.class);
    when(Database.getInstance()).thenReturn(database);
    when(database.addUser("the_partner_id", "the_product_id", "the_user_id")).thenReturn(true);

    AddUserController addUserController = new AddUserController();

    Request request = mock(Request.class);
    when(request.params(":partnerId")).thenReturn("the_partner_id");
    when(request.params(":productId")).thenReturn("the_product_id");
    when(request.params(":userId")).thenReturn("the_user_id");
    Response response = mock(Response.class);
    when(response.status()).thenReturn(200);

    Response actualResponse = (Response) addUserController.handle(request, response);
    
    assertEquals(200, actualResponse.status());

  }
}
