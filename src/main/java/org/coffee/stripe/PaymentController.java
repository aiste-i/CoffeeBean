package org.coffee.stripe;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Path("/payment")
public class PaymentController {



    @POST
    @Path("/create-session")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createStripeSession(Map<String, Object> data) {
        String customerName = (String) data.get("customerName");
        String customerEmail = (String) data.get("customerEmail");
        Double orderTotal = ((BigDecimal) data.get("orderTotal")).doubleValue();
        Long orderId = Long.parseLong(data.get("orderId").toString());


        if (orderTotal == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Field 'orderTotal' is required and must not be null."))
                    .build();
        }


        try {

            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setCancelUrl("http://localhost:9080/coffee-1.0-SNAPSHOT/user/checkout-confirm.xhtml")
                    .setSuccessUrl("http://localhost:9080/coffee-1.0-SNAPSHOT/user/order-confirmation-page.xhtml")
                    .setCustomerEmail(customerEmail)
                    .putMetadata("orderId", String.valueOf(orderId))
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency("eur")
                                                    .setUnitAmount(Math.round(orderTotal * 100))
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName(customerName != null && !customerName.isEmpty()
                                                                            ? customerName
                                                                            : "Order Payment")
                                                                    .build()
                                                    )
                                                    .build()
                                    )
                                    .build()
                    )
                    .build();


            Session session = Session.create(params);

            Map<String, String> responseData = new HashMap<>();
            responseData.put("sessionId", session.getId());
            return Response.ok(responseData).build();

        } catch (StripeException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}
