package edu.rosehulman.quota;

import edu.rosehulman.quota.controller.FreezeUserController;
import edu.rosehulman.quota.controller.GetPartnerByApiController;
import edu.rosehulman.quota.controller.GetUserController;
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

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Database.class, Request.class, Response.class, User.class })
public class GetPartnerByApiControllerTest {

  Database database;
  Request request;
  Request badRequest;
  Request missingPartnerRequest;
  Response response;
  Optional<Partner> optPresentPartner;
  Optional<Partner> optMissingPartner;
  Partner partner = new Partner();
  GetPartnerByApiController getPartnerByApiController;
  Optional<Partner> optPartner;

  @Before
  public void setup() throws Exception {
    // setup
    // mocks
    mockStatic(Database.class);
    database = Mockito.mock(Database.class);
    request = mock(Request.class);
    badRequest = mock(Request.class);
    missingPartnerRequest = mock(Request.class);
    response = mock(Response.class);
    partner = mock(Partner.class);
    optPresentPartner = Optional.of(partner);
    optMissingPartner = Optional.empty();

    // real objects
    getPartnerByApiController = new GetPartnerByApiController();

    // returns
    when(missingPartnerRequest.params(":apiKey")).thenReturn("missing_apiKey");
    when(badRequest.params(":apiKey")).thenReturn("bad_apiKey");
    when(request.params(":apiKey")).thenReturn("apiKey");
    when(Database.getInstance()).thenReturn(database);
    when(database.getPartnerByApi("missing_apiKey")).thenReturn(optMissingPartner);
    when(database.getPartnerByApi("apiKey")).thenReturn(optPresentPartner);
    when(partner.getPartnerId()).thenReturn("partnerId");
  }

  @Test
  public void testGetMissingPartner() throws Exception {
    // execute
    try {
      getPartnerByApiController.handle(missingPartnerRequest, response);
    } catch (HaltException e) {
      assertEquals(404, e.statusCode());
      Mockito.verify(database);
      return;
    }
    fail("Exception not thrown");
  }

  @Test
  public void testGetPartner() throws Exception {
    // execute
    String actualResponse = (String) getPartnerByApiController.handle(request, response);
    // verify
    assertEquals("partnerId", actualResponse);
    Mockito.verify(database);
  }

  @Test(expected = Exception.class)
  public void testGetPartnerUnsuccessfulGet() throws Exception {
    Mockito.doThrow(new Exception()).when(database).getPartner("bad_apiKey");

    // execute
    getPartnerByApiController.handle(badRequest, response);
    // verify
    Mockito.verify(database);
  }

}
