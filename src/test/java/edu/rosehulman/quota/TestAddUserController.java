package edu.rosehulman.quota;

//@RunWith(PowerMockRunner.class)
//@PrepareForTest({Database.class, Request.class, Response.class, SharedServiceClient.class})
public class TestAddUserController {

//  @Test
//  public void testAddUser() throws Exception {
//    mockStatic(Database.class);
//    mockStatic(SharedServiceClient.class);
//
//    Database database = Mockito.mock(Database.class);
//    when(Database.getInstance()).thenReturn(database);
//    when(database.addUser("the_partner_id", "the_product_id", "the_user_id")).thenReturn(true);
//    when(database.addUser("the_partner_id", "the_product_id", "shared_bad_user_id")).thenReturn(true);
//    when(database.addUser("the_partner_id", "the_product_id", "bad_user_id")).thenReturn(false);
//
//    SharedServiceClient shared = Mockito.mock(SharedServiceClient.class);
//    when(SharedServiceClient.getInstance()).thenReturn(shared);
//    when(shared.addUser("the_partner_id", "the_product_id", "the_user_id")).thenReturn(true);
//    when(shared.addUser("the_partner_id", "the_product_id", "shared_bad_user_id")).thenReturn(false);
//
//    AddUserController addUserController = new AddUserController();
//
//    Request request = mock(Request.class);
//    when(request.params(":partnerId")).thenReturn("the_partner_id");
//    when(request.params(":productId")).thenReturn("the_product_id");
//    when(request.params(":userId")).thenReturn("the_user_id");
//    Response response = mock(Response.class);
//    when(response.status()).thenReturn(200);
//
//    Response actualResponse = (Response) addUserController.handle(request, response);
//    assertEquals(200, actualResponse.status());
//
//    when(request.params(":userId")).thenReturn("bad_user_id");
//    try {
//      actualResponse = (Response) addUserController.handle(request, response);
//      fail("Exception not thrown");
//    } catch (Exception e) {
//    }
//
//    when(request.params(":userId")).thenReturn("shared_bad_user_id");
//    try {
//      actualResponse = (Response) addUserController.handle(request, response);
//      fail("Exception not thrown");
//    } catch (Exception e) {
//    }
//  }
}
