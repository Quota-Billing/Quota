package edu.rosehulman.quota;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import edu.rosehulman.quota.controller.GetQuotaController;
import edu.rosehulman.quota.model.Partner;
import edu.rosehulman.quota.model.Quota;
import spark.HaltException;
import spark.Request;
import spark.Response;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Database.class, Request.class, Response.class})
public class GetQuotaControllerTest {

  @Test
  public void testGetQuota() throws Exception {
    mockStatic(Database.class);
    Database database = mock(Database.class);
    when(Database.getInstance()).thenReturn(database);

    Quota quota = mock(Quota.class);
    Partner partner = mock(Partner.class);

    when(database.getPartnerByApi("apiKey")).thenReturn(Optional.of(partner));
    when(partner.getPartnerId()).thenReturn("partner_id");

    when(database.getQuota("partner_id", "product_id", "quota_id")).thenReturn(Optional.of(quota));
    when(database.getQuota("partner_id", "product_id", "bad_quota_id")).thenReturn(Optional.empty());

    Request request = mock(Request.class);
    when(request.params(":apiKey")).thenReturn("apiKey");
    when(request.params(":productId")).thenReturn("product_id");
    when(request.params(":userId")).thenReturn("user_id");
    when(request.params(":quotaId")).thenReturn("quota_id");
    Response response = mock(Response.class);
    when(response.status()).thenReturn(200);

    GetQuotaController getQuotaController = new GetQuotaController();

    getQuotaController.handle(request, response);
    assertEquals(200, response.status());

    when(request.params(":quotaId")).thenReturn("bad_quota_id");
    when(response.status()).thenReturn(404);

    try {
      getQuotaController.handle(request, response);
    } catch (HaltException e) {
      assertEquals(404, response.status());
      return;
    }
    fail("Exception not thrown");
  }
}
