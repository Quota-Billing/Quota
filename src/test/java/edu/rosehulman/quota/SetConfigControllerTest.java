package edu.rosehulman.quota;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

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
import static org.junit.Assert.fail;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Database.class, Request.class, Response.class })
public class SetConfigControllerTest {

	@Test
  public void testSetConfig() throws Exception {
    mockStatic(Database.class);
    Database database = Mockito.mock(Database.class);
    when(Database.getInstance()).thenReturn(database);

    
    // create config
    JsonObject tier1 = new JsonObject();
    tier1.addProperty("id", "1");
    tier1.addProperty("name", "free");
    tier1.addProperty("max", "1000");
    tier1.addProperty("price", "0");

    JsonObject tier2 = new JsonObject();
    tier2.addProperty("id", "2");
    tier2.addProperty("name", "premium");
    tier2.addProperty("max", "22222");
    tier2.addProperty("price", "33333");
    
    JsonArray tiers = new JsonArray();
    tiers.add(tier1);
    tiers.add(tier2);
    
    JsonObject quota1 = new JsonObject();
    quota1.addProperty("name", "num_searches");
    quota1.addProperty("id", "0");
    quota1.addProperty("type", "numerical_recurring");
    quota1.add("tiers", tiers);
    
    
    JsonObject tier3 = new JsonObject();
    tier3.addProperty("id", "3");
    tier3.addProperty("name", "free");
    tier3.addProperty("max", "1000");
    tier3.addProperty("price", "0");

    JsonObject tier4 = new JsonObject();
    tier4.addProperty("id", "4");
    tier4.addProperty("name", "premium");
    tier4.addProperty("max", "2000");
    tier4.addProperty("price", "500");
    
    JsonArray tiers2 = new JsonArray();
    tiers2.add(tier3);
    tiers2.add(tier4);
    
    JsonObject quota2 = new JsonObject();
    quota2.addProperty("name", "file_upload");
    quota2.addProperty("id", "1");
    quota2.addProperty("type", "numerical_recurring");
    quota2.add("tiers", tiers2);
    
    JsonArray quotas = new JsonArray();
    quotas.add(quota1);
    quotas.add(quota2);

    JsonObject product = new JsonObject();
    product.addProperty("name", "“product_1");
    product.addProperty("id", "1");
    product.add("quotas", quotas);
    
    JsonArray products = new JsonArray();
    products.add(product);
    
    JsonObject config = new JsonObject();
    product.addProperty("id", "0");
    product.addProperty("apiKey", "key");
    product.add("products", products);
    
    SetConfigController ctrller = new SetConfigController();
    
    Request request = mock(Request.class);
    when(request.body()).thenReturn(config.toString());
    Response response = mock(Response.class);
    when(response.status()).thenReturn(200);
    
    when(database.setConfig(config.toString())).thenReturn(true);
    
    Response actualResponse = (Response) ctrller.handle(request, response);
 
    assertEquals(200, actualResponse.status());
  }

	/*
	 * @Test public void testAddUser() throws Exception {
	 * mockStatic(Database.class); mockStatic(SharedServiceClient.class);
	 * 
	 * Database database = Mockito.mock(Database.class);
	 * when(Database.getInstance()).thenReturn(database);
	 * when(database.addUser("the_partner_id", "the_product_id",
	 * "the_user_id")).thenReturn(true); when(database.addUser("the_partner_id",
	 * "the_product_id", "shared_bad_user_id")).thenReturn(true);
	 * when(database.addUser("the_partner_id", "the_product_id",
	 * "bad_user_id")).thenReturn(false);
	 * 
	 * SharedServiceClient shared = Mockito.mock(SharedServiceClient.class);
	 * when(SharedServiceClient.getInstance()).thenReturn(shared);
	 * when(shared.addUser("the_partner_id", "the_product_id",
	 * "the_user_id")).thenReturn(true); when(shared.addUser("the_partner_id",
	 * "the_product_id", "shared_bad_user_id")).thenReturn(false);
	 * 
	 * AddUserController addUserController = new AddUserController();
	 * 
	 * Request request = mock(Request.class);
	 * when(request.params(":partnerId")).thenReturn("the_partner_id");
	 * when(request.params(":productId")).thenReturn("the_product_id");
	 * when(request.params(":userId")).thenReturn("the_user_id"); Response response
	 * = mock(Response.class); when(response.status()).thenReturn(200);
	 * 
	 * Response actualResponse = (Response) addUserController.handle(request,
	 * response); assertEquals(200, actualResponse.status());
	 * 
	 * when(request.params(":userId")).thenReturn("bad_user_id"); try {
	 * actualResponse = (Response) addUserController.handle(request, response);
	 * fail("Exception not thrown"); } catch (Exception e) { }
	 * 
	 * when(request.params(":userId")).thenReturn("shared_bad_user_id"); try {
	 * actualResponse = (Response) addUserController.handle(request, response);
	 * fail("Exception not thrown"); } catch (Exception e) { } }
	 */
}
