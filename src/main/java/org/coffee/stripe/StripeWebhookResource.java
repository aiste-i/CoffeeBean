package org.coffee.stripe;


import com.stripe.model.Event;
import com.stripe.net.Webhook;
import org.coffee.rest.OrderApiClient;


import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONObject;


@Path("/payment/webhook")
public class StripeWebhookResource {



    @Inject
    private OrderApiClient orderApiClient;

    private static final String STRIPE_WEBHOOK_SECRET = null; // ideti webhook secret



    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response handleStripeWebhook(String payload, @HeaderParam("Stripe-Signature") String sigHeader) {
        try {

            Event event = Webhook.constructEvent(payload, sigHeader, STRIPE_WEBHOOK_SECRET);

            if ("checkout.session.completed".equals(event.getType())) {

                String rawSessionJson = event.getDataObjectDeserializer()
                        .getRawJson();

                if (rawSessionJson == null) {
                    return Response.status(400).entity("Missing session data").build();
                }


                JSONObject sessionObject = new JSONObject(rawSessionJson);


                if (sessionObject.has("metadata")) {
                    JSONObject metadata = sessionObject.getJSONObject("metadata");


                    if (metadata.has("orderId")) {
                        String orderIdStr = metadata.getString("orderId");
                        Long orderId = Long.parseLong(orderIdStr);
                        orderApiClient.updateOrder(orderId);
                    }
                }
            }

            return Response.ok().build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(400).build();
        }
    }

}

