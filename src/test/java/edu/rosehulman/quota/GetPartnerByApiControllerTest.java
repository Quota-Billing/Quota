package edu.rosehulman.quota;

import edu.rosehulman.quota.controller.GetPartnerByApiController;
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
public class GetPartnerByApiControllerTest {

  private Database database;
  private Request request;
  private Request badRequest;
  private Request missingPartnerRequest;
  private Response response;
  private Optional<Partner> optPresentPartner;
  private Optional<Partner> optMissingPartner;
  private Partner partner = new Partner();
  private GetPartnerByApiController getPartnerByApiController;

  @Before
  public void setUp() throws Exception {
    // setup
    // mocks
    mockStatic(Database.class);
    database = mock(Database.class);
    request = mock(Request.class);
    badRequest = mock(Request.class);
    missingPartnerRequest = mock(Request.class);
    response = mock(Response.class);
    partner = mock(Partner.class);

    // real objects
    getPartnerByApiController = new GetPartnerByApiController();
    optPresentPartner = Optional.of(partner);
    optMissingPartner = Optional.empty();

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
