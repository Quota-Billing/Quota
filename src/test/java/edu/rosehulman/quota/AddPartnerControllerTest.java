package edu.rosehulman.quota;

import edu.rosehulman.quota.client.SharedServiceClient;
import edu.rosehulman.quota.controller.AddPartnerController;
import edu.rosehulman.quota.model.Partner;

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

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Database.class, Request.class, Response.class, SharedServiceClient.class })
public class AddPartnerControllerTest {

  @Test(expected = Exception.class)
  public void testAddPartnerException() throws Exception {
    // setup
    // mocks
    mockStatic(Database.class);
    mockStatic(SharedServiceClient.class);
    Database database = Mockito.mock(Database.class);
    when(Database.getInstance()).thenReturn(database);
    Request request = mock(Request.class);
    Response response = mock(Response.class);
    Partner partner = mock(Partner.class);

    // real objects
    AddPartnerController addPartnerController = new AddPartnerController();
    JsonObject body = new JsonObject();
    body.addProperty("partnerId", "partnerId");
    body.addProperty("apiKey", "apiKey");

    // Partner partner = Mockito.mock(Partner.class); ***

    // expects
    when(new Partner()).thenReturn(partner);

    Mockito.doThrow(new Exception()).when(database).addPartner(partner);

    when(request.body()).thenReturn(body.toString());
    // ***??
    when(response.status()).thenReturn(500);

    // execute
    String actualResponse = (String) addPartnerController.handle(request, response);

    // verify
    assertEquals("", actualResponse);
    Mockito.verify(database);
  }
}
