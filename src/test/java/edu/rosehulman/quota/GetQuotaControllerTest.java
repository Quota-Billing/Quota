package edu.rosehulman.quota;

import com.google.gson.Gson;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import spark.Request;
import spark.Response;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Database.class, Request.class, Response.class})
public class GetQuotaControllerTest {

  @Test
  public void testGetQuota() throws Exception {
    mockStatic(Database.class);
    Database database = Mockito.mock(Database.class);
    when(Database.getInstance()).thenReturn(database);

    Quota expectedQuota = new Quota();
    expectedQuota.setId("the_quota_id");
    expectedQuota.setName("the_quota_name");
    expectedQuota.setType("numerical_recurring");
    List<Tier> tiers = new ArrayList<>();
    Tier tier = new Tier();
    tier.setId("the_tier_id");
    tier.setName("the_tier_name");
    tier.setMax(1000);
    tier.setValue(500);
    tier.setPrice(99.99);
    tiers.add(tier);
    expectedQuota.setTiers(tiers);
    when(database.getQuota("the_partner_id", "the_product_id", "the_user_id", "the_quota_id")).thenReturn(expectedQuota);

    GetQuotaController getQuotaController = new GetQuotaController();

    Request request = mock(Request.class);
    when(request.params(":partnerId")).thenReturn("the_partner_id");
    when(request.params(":productId")).thenReturn("the_product_id");
    when(request.params(":userId")).thenReturn("the_user_id");
    when(request.params(":quotaId")).thenReturn("the_quota_id");
    Response response = mock(Response.class);

    Quota actualQuota = new Gson().fromJson((String) getQuotaController.handle(request, response), Quota.class);

    assertEquals(expectedQuota.getId(), actualQuota.getId());
    assertEquals(expectedQuota.getName(), actualQuota.getName());
    assertEquals(expectedQuota.getType(), actualQuota.getType());
    assertEquals(expectedQuota.getTiers().get(0).getId(), actualQuota.getTiers().get(0).getId());
    assertEquals(expectedQuota.getTiers().get(0).getName(), actualQuota.getTiers().get(0).getName());
    assertEquals(expectedQuota.getTiers().get(0).getMax(), actualQuota.getTiers().get(0).getMax());
    assertEquals(expectedQuota.getTiers().get(0).getValue(), actualQuota.getTiers().get(0).getValue());
    assertEquals(expectedQuota.getTiers().get(0).getPrice(), actualQuota.getTiers().get(0).getPrice(), 0.01d);
  }
}
