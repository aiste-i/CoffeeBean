package org.coffee.stripe;


import com.stripe.model.Event;
import com.stripe.net.Webhook;
import org.coffee.rest.OrderApiClient;


import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.coffee.web.OrderBean;
import org.json.JSONObject;


@Path("/payment/webhook")
public class StripeWebhookResource {



    @Inject
    private OrderApiClient orderApiClient;

    @Inject
    private OrderBean orderBean;

    private static final String STRIPE_WEBHOOK_SECRET = "whsec_5b2646f0b0071125d862bb3ba50d3f4c253a7d082a843e43de012a9ad0ff7a38";

    // stripe listen --forward-to localhost:9080/coffee-1.0-SNAPSHOT/api/payment/webhook

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

