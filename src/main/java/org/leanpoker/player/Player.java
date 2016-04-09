package org.leanpoker.player;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Map;

public class Player {

    static final String VERSION = "Default Java kick-ass player";

    static class Card {
        String suit;
        String rank;
    }

    public static int betRequest(JsonElement request) {
        try {
            int inAction = request.getAsJsonObject().get("in_action").getAsInt();
            JsonObject player = request.getAsJsonObject().get("players").getAsJsonArray().get(inAction).getAsJsonObject();
            JsonArray cards = player.get("hole_cards").getAsJsonArray();

            // Cards on table
            JsonArray cardsOnTableJson = request.getAsJsonObject().get("community_cards").getAsJsonArray();
            ArrayList<Card> cardsOnTable = new ArrayList<Card>();
            for (JsonElement item : cardsOnTableJson) {
                Card card = new Card();
                card.suit = item.getAsJsonObject().get("suit").getAsString();
                card.rank = item.getAsJsonObject().get("rank").getAsString();
                cardsOnTable.add(card);
            }

            int currentBet = request.getAsJsonObject().get("players").getAsJsonObject().get("bet").getAsInt();
            int minimumRaise = request.getAsJsonObject().get("minimum_raise").getAsInt();
            int currentBuyIn = request.getAsJsonObject().get("current_buy_in").getAsInt();

            int amountToHold = currentBuyIn - currentBet;
            int amountToRaise = amountToHold + minimumRaise;

            Card[] hand = new Card[2];
            int c = 0;

            for (JsonElement item : cards) {
                Card card = new Card();

                card.suit = item.getAsJsonObject().get("suit").getAsString();
                card.rank = item.getAsJsonObject().get("rank").getAsString();
                hand[c++] = card;
            }

            if (hand[0].rank.equals(hand[1].rank)) {
                return amountToRaise;
            }

            return amountToHold;
        } catch (Exception e) {}

        return 1000;
    }

    public static void showdown(JsonElement game) {
    }
}
