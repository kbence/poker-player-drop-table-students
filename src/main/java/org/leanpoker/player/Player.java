package org.leanpoker.player;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sun.deploy.util.ArrayUtil;

import java.util.ArrayList;
import java.util.Map;

public class Player {

    static final String VERSION = "Default Java kick-ass player";

    static class Card {
        String suit;
        String rank;
    }

    private static Card[] getCommunityCards(JsonElement request) {
        // Cards on table
        JsonArray cardsOnTableJson = request.getAsJsonObject().get("community_cards").getAsJsonArray();
        ArrayList<Card> cardsOnTable = new ArrayList<Card>();

        for (JsonElement item : cardsOnTableJson) {
            Card card = new Card();
            card.suit = item.getAsJsonObject().get("suit").getAsString();
            card.rank = item.getAsJsonObject().get("rank").getAsString();
            cardsOnTable.add(card);
        }

        return cardsOnTable.toArray(new Card[cardsOnTable.size()]);
    }

    private static Card[] getOwnCards(JsonArray cards) {
        Card[] hand = new Card[2];
        int c = 0;

        for (JsonElement item : cards) {
            Card card = new Card();

            card.suit = item.getAsJsonObject().get("suit").getAsString();
            card.rank = item.getAsJsonObject().get("rank").getAsString();
            hand[c++] = card;
        }
        return hand;
    }

    private static void analyzeHand(Card[] hand, Card[] community) {
    }

    private static int getCardValue(Card card) {
        String[] values = new String[] {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
        for (int n = 0; n < values.length; n++) {
            if (card.rank.equals(values[n]))
                return n + 2;
        }

        return -1;
    }

    public static int betRequest(JsonElement request) {
        try {
            int inAction = request.getAsJsonObject().get("in_action").getAsInt();
            JsonObject player = request.getAsJsonObject().get("players").getAsJsonArray().get(inAction).getAsJsonObject();
            JsonArray cards = player.get("hole_cards").getAsJsonArray();
            Card[] cardsOnTable = getCommunityCards(request);
            Card[] hand = getOwnCards(cards);

            int currentBet = request.getAsJsonObject().get("players").getAsJsonObject().get("bet").getAsInt();
            int currentStack = request.getAsJsonObject().get("players").getAsJsonObject().get("stack").getAsInt();
            int minimumRaise = request.getAsJsonObject().get("minimum_raise").getAsInt();
            int currentBuyIn = request.getAsJsonObject().get("current_buy_in").getAsInt();

            int amountToHold = currentBuyIn - currentBet;
            int amountToRaise = amountToHold + minimumRaise;

            if (hand[0].rank.equals(hand[1].rank)) {
                return Math.max(amountToRaise, amountToHold + currentStack / 10);
            }

            if (getCardValue(hand[0]) + getCardValue(hand[1]) > 20) {
                return Math.max(amountToRaise, amountToHold + currentStack / 15);
            }
        } catch (Exception e) {}

        return 0;
    }

    public static void showdown(JsonElement game) {
    }
}
