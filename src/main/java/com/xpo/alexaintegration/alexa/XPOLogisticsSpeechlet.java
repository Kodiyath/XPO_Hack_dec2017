package com.xpo.alexaintegration.alexa;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.speechlet.SpeechletV2;
import com.xpo.alexaintegration.business.LogisticsService;
import com.xpo.alexaintegration.business.Order;
import com.xpo.alexaintegration.business.OrderStatus;

@Component
public class XPOLogisticsSpeechlet implements SpeechletV2 {
	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(XPOLogisticsSpeechlet.class);
    /**
     * If this flag is set in the session, the user wants a conversation and no one-shot intent.
     */
    private static final String SESSION_CONVERSATION_FLAG = "conversation";

    /**
     * String constants.
     */
    private static class Strings {
        static final String PROMPT_USER = "how may I help you?";
        static final String WELCOME = "Welcome!";
        static final String GOODBYE = "Have a nice day...";

        /**
         * No instances allowed.
         */
        private Strings() {
        }
    }

    private final LogisticsService logisticsService;

    /**
     * Constructor.
     *
     * @param warehouseService Warehouse service.
     */
    @Autowired
    public XPOLogisticsSpeechlet(LogisticsService warehouseService) {
        this.logisticsService = warehouseService;
    }

    @Override
    public void onSessionStarted(SpeechletRequestEnvelope<SessionStartedRequest> requestEnvelope) {
        LOGGER.info("onSessionStarted()");
    }

    @Override
    public SpeechletResponse onLaunch(SpeechletRequestEnvelope<LaunchRequest> requestEnvelope) {
        LOGGER.info("onLaunch()");
        requestEnvelope.getSession().setAttribute(SESSION_CONVERSATION_FLAG, "true");
        return SpeechletResponse.newAskResponse(AlexaHelper.speech(Strings.WELCOME + " " + Strings.PROMPT_USER), AlexaHelper.repromt(Strings.PROMPT_USER));
    }

    @Override
	public SpeechletResponse onIntent(SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {
		LOGGER.info("onIntent()");

		Intent intent = requestEnvelope.getRequest().getIntent();
		switch (intent.getName()) {
		case "QueryAllOrders":
			return handleAllQueryOrders(requestEnvelope);
		case "QueryOrder": 
			return handleQueryOrders(requestEnvelope);
		// case "OrderWare":
		// return handleOrderWare(requestEnvelope);
		// case "LocateWare":
		// return handleLocateWare(requestEnvelope);
		case "Quit":
			return handleQuit();
		default:
			throw new IllegalArgumentException("Unknown intent: " + intent.getName());
		}
	}

    @Override
    public void onSessionEnded(SpeechletRequestEnvelope<SessionEndedRequest> requestEnvelope) {
        LOGGER.info("onSessionEnded()");
    }

    /**
     * Handles the Quit intent.
     *
     * @return Response.
     */
    private SpeechletResponse handleQuit() {
        return SpeechletResponse.newTellResponse(AlexaHelper.speech(Strings.GOODBYE));
    }

	private SpeechletResponse handleAllQueryOrders(SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {
		List<Order> orders = logisticsService.getOrders();
		
		/*
		 * You have 4 orders, with 2 orders in transit, 1 in shipped state
		 */
		
		final StringBuilder sb = new StringBuilder();
		if (CollectionUtils.isEmpty(orders)) {
			return SpeechletResponse
					.newTellResponse(AlexaHelper.speech("You do not have any orders currently"));
		} 
		
		sb.append(String.format("You have %d orders, with", orders.size()));
		
		
		return SpeechletResponse
				.newTellResponse(AlexaHelper.speech(String.format("You have %d orders", orders.size())));
		// Optional<String> ware =
		// AlexaHelper.getSlotValue(requestEnvelope.getRequest().getIntent(),
		// "ware");
		// LOGGER.debug("handleQueryInventory({})", ware);
		// if (!ware.isPresent()) {
		// return createMissingWareResponse(requestEnvelope);
		// }
		//
		// try {
		// int amount = warehouseService.getAmount(normalizeWare(ware.get()));
		//
		// if (isConversation(requestEnvelope.getSession())) {
		// return SpeechletResponse.newAskResponse(AlexaHelper.speech(
		// String.format(Strings.WARE_AMOUNT, amount, ware.get()) + " " +
		// Strings.PROMPT_USER),
		// AlexaHelper.repromt(Strings.PROMPT_USER)
		// );
		// } else {
		// return SpeechletResponse.newTellResponse(AlexaHelper.speech(
		// String.format(Strings.WARE_AMOUNT, amount, ware.get())
		// ));
		// }
		// } catch (UnknownWareException e) {
		// return createUnknownWareResponse(requestEnvelope);
		// }
	}
	
    private SpeechletResponse handleQueryOrders(SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {
        Optional<String> orderStatus = AlexaHelper.getSlotValue(requestEnvelope.getRequest().getIntent(), "orderStatus");
        LOGGER.debug("handleQueryOrders({})", orderStatus);
//        if (!ware.isPresent()) {
//            return createMissingWareResponse(requestEnvelope);
//        }

//        try {
        	List<Order> allOrders = logisticsService.getOrders();
        	List<Order> ordersByStatus = logisticsService.getOrders(allOrders, OrderStatus.getStatus(orderStatus.get()));
        	
        	if (ordersByStatus != null && !ordersByStatus.isEmpty()) {
        		StringBuilder sb = new StringBuilder();
        		sb.append("Here are your orders,");
        		for (Order o: ordersByStatus) {
        			sb.append(String.format("order id %s, eta %s", o.getOrderId(), o.getCurrentEta()));
        		}
        		
        		return SpeechletResponse.newTellResponse(AlexaHelper.speech(sb.toString()));
        	} else {
        		return SpeechletResponse.newTellResponse(AlexaHelper.speech(String.format("There is no %s orders", orderStatus.get())));
        	}
        	
//            int amount = logisticsService.getAmount(normalizeWare(orderStatus.get()));
//
//            if (isConversation(requestEnvelope.getSession())) {
//                return SpeechletResponse.newAskResponse(AlexaHelper.speech(
//                        String.format(Strings.WARE_AMOUNT, amount, ware.get()) + " " + Strings.PROMPT_USER),
//                        AlexaHelper.repromt(Strings.PROMPT_USER)
//                );
//            } else {
//                return SpeechletResponse.newTellResponse(AlexaHelper.speech(
//                        String.format(Strings.WARE_AMOUNT, amount, ware.get())
//                ));
//            }
//        } catch (UnknownWareException e) {
//            return createUnknownWareResponse(requestEnvelope);
//        }
    }

    /**
     * Handles the OrderWare intent.
     *
     * @param requestEnvelope Request.
     * @return Response.
     */
//    private SpeechletResponse handleOrderWare(SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {
//        Optional<String> ware = AlexaHelper.getSlotValue(requestEnvelope.getRequest().getIntent(), "ware");
//        LOGGER.debug("handleOrderWare({})", ware);
//        if (!ware.isPresent()) {
//            return createMissingWareResponse(requestEnvelope);
//        }
//
//        try {
//            warehouseService.orderWare(normalizeWare(ware.get()));
//            if (isConversation(requestEnvelope.getSession())) {
//                return SpeechletResponse.newAskResponse(AlexaHelper.speech(
//                        String.format(Strings.WARE_ORDERED, ware.get()) + " " + Strings.PROMPT_USER),
//                        AlexaHelper.repromt(Strings.PROMPT_USER)
//                );
//            } else {
//                return SpeechletResponse.newTellResponse(AlexaHelper.speech(
//                        String.format(Strings.WARE_ORDERED, ware.get()))
//                );
//            }
//        } catch (UnknownWareException e) {
//            return createUnknownWareResponse(requestEnvelope);
//        }
//    }

    /**
     * Handles the LocateWare intent.
     *
     * @param requestEnvelope Request.
     * @return Response.
     */
//    private SpeechletResponse handleLocateWare(SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {
//        Optional<String> ware = AlexaHelper.getSlotValue(requestEnvelope.getRequest().getIntent(), "ware");
//        LOGGER.debug("handleLocateWare({})", ware);
//        if (!ware.isPresent()) {
//            return createMissingWareResponse(requestEnvelope);
//        }
//
//        try {
//            Location location = warehouseService.locateWare(normalizeWare(ware.get()));
//            if (isConversation(requestEnvelope.getSession())) {
//                return SpeechletResponse.newAskResponse(AlexaHelper.speechSsml(
//                        String.format("<speak>" + Strings.WARE_LOCATION + "<break strength=\"medium\"/>" + Strings.PROMPT_USER + "</speak>", ware.get(), location.getRow(), location.getRegal(), location.getShelf())),
//                        AlexaHelper.repromt(Strings.PROMPT_USER)
//                );
//            } else {
//                return SpeechletResponse.newTellResponse(
//                        AlexaHelper.speech(String.format(Strings.WARE_LOCATION, ware.get(), location.getRow(), location.getRegal(), location.getShelf()))
//                );
//            }
//        } catch (UnknownWareException e) {
//            return createUnknownWareResponse(requestEnvelope);
//        }
//    }

    /**
     * Creates the response if the ware is missing in the request.
     *
     * @param requestEnvelope Request.
     * @return Response.
     */
//    private SpeechletResponse createMissingWareResponse(SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {
//        if (isConversation(requestEnvelope.getSession())) {
//            return SpeechletResponse.newAskResponse(AlexaHelper.speech(
//                    Strings.MISSING_WARE + " " + Strings.PROMPT_USER), AlexaHelper.repromt(Strings.PROMPT_USER)
//            );
//        } else {
//            return SpeechletResponse.newTellResponse(AlexaHelper.speech(
//                    Strings.MISSING_WARE
//            ));
//        }
//    }

    /**
     * Creates the response if the ware in the request is unknown.
     *
     * @param requestEnvelope Request.
     * @return Response.
     */
//    private SpeechletResponse createUnknownWareResponse(SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {
//        if (isConversation(requestEnvelope.getSession())) {
//            return SpeechletResponse.newAskResponse(AlexaHelper.speech(
//                    Strings.UNKNOWN_WARE + " " + Strings.PROMPT_USER),
//                    AlexaHelper.repromt(Strings.PROMPT_USER)
//            );
//        } else {
//            return SpeechletResponse.newTellResponse(AlexaHelper.speech(
//                    Strings.UNKNOWN_WARE
//            ));
//        }
//    }

    /**
     * Determines if the user wants a conversation or a one-shot intent.
     *
     * @param session Session.
     * @return True if the user wants a conversation. False if the user issued a one-shot intent.
     */
    private boolean isConversation(Session session) {
        return session.getAttribute(SESSION_CONVERSATION_FLAG) != null;
    }
}
