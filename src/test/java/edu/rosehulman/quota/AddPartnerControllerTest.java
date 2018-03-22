package edu.rosehulman.quota;

import edu.rosehulman.quota.controller.AddPartnerController;
import edu.rosehulman.quota.factories.PartnerFactory;
import edu.rosehulman.quota.model.Partner;

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

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Database.class, Request.class, Response.class})
public class AddPartnerControllerTest {

  Database database;
  Request request;
  Request badRequest;
  Response response;
  Partner partner;
  Partner badPartner;
  AddPartnerController addPartnerController;
  JsonObject body;
  JsonObject badBody;
  PartnerFactory factory;

  @Before
  public void setup() throws Exception {
    // setup
    // mocks
    mockStatic(Database.class);
    database = Mockito.mock(Database.class);
    request = mock(Request.class);
    badRequest = mock(Request.class);
    response = mock(Response.class);
    partner = mock(Partner.class);
    badPartner = mock(Partner.class);
    factory = mock(PartnerFactory.class);

    // real objects
    addPartnerController = new AddPartnerController(factory);
    body = new JsonObject();
    body.addProperty("partnerId", "partnerId");
    body.addProperty("apiKey", "apiKey");
    badBody = new JsonObject();
    badBody.addProperty("partnerId", "bad_partnerId");
    badBody.addProperty("apiKey", "bad_apiKey");
    
    // returns
    when(Database.getInstance()).thenReturn(database);
    when(factory.createPartner("partnerId", "apiKey")).thenReturn(partner);
    when(factory.createPartner("bad_partnerId", "bad_apiKey")).thenReturn(badPartner);
  }

  @Test
  public void testAddPartner() throws Exception {
    // expects
    Mockito.doNothing().when(database).addPartner(partner);

    when(request.body()).thenReturn(body.toString());

    // execute
    String actualResponse = (String) addPartnerController.handle(request, response);

    // verify
    assertEquals("", actualResponse);
    Mockito.verify(database);
  }

  @Test(expected = Exception.class)
  public void testAddPartnerException() throws Exception {
    // expects
    Mockito.doThrow(new Exception()).when(database).addPartner(badPartner);

    when(badRequest.body()).thenReturn(badBody.toString());

    // execute
    String actualResponse = (String) addPartnerController.handle(badRequest, response);

    // verify
    assertEquals("", actualResponse);
    Mockito.verify(database);
  }

}
