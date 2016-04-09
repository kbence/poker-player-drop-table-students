package org.leanpoker.player;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Player {

    static final String VERSION = "Default Java kick-ass player";

    static class Card {
        String suit;
        String rank;
    }

    static class PairLike {
        Integer rank;
        Integer number;
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

    public static PairLike[] analyzeHand(Card[] hand, Card[] community) {
        Map<Integer, ArrayList<Card>> map = new HashMap<Integer, ArrayList<Card>>();
        for(Card card : community){
            int index = getCardValue(card);
            ArrayList<Card> current = map.get(index);
            if(current == null) current = new ArrayList<Card>();
            current.add(card);
            map.put(index, current);
        }
        for(Card card : hand){
            int index = getCardValue(card);
            ArrayList<Card> current = map.get(index);
            if(current == null) current = new ArrayList<Card>();
            current.add(card);
            map.put(index, current);
        }

        PairLike first = new PairLike();
        first.rank = getCardValue(hand[0]);
        first.number = map.get(first.rank).size();
        if(hand[0].rank.equals(hand[1].rank)){
            if(first.number > 1) return new PairLike[]{first};
            else return new PairLike[]{};
        }else {
            PairLike second = new PairLike();
            second.rank = getCardValue(hand[1]);
            second.number = map.get(second.rank).size();

            if (first.number <= 1 && second.number <= 1)
                return new PairLike[]{};
            else if (first.number > 1 && second.number > 1)
                return new PairLike[]{first, second};
            else if (first.number > 1) {
                return new PairLike[]{first};
            } else {
                return new PairLike[]{second};
            }
        }
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

            // Calc position
            int position = 0;
            PairLike[] analyzeResult = analyzeHand(hand, cardsOnTable);
            if(analyzeResult.length == 1){
                position = analyzeResult[0].number * analyzeResult[0].rank;
            }else if(analyzeResult.length == 2){
                position = analyzeResult[0].number * analyzeResult[0].rank + analyzeResult[1].number * analyzeResult[1].rank;
            }

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
