package org.leanpoker.player;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Player {

    static final String VERSION = "Default Java kick-ass player";


    public String id;
    public String Name;
    public String status;
    public String version;
    public String stack;
    public String bet;


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

    public static Map<Integer, Integer> analyzeHand(Card[] hand, Card[] community) {
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

        Map<Integer, Integer> result = new HashMap<Integer, Integer>();
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            Integer key = (Integer) pair.getKey();
            ArrayList<Card> cards = (ArrayList<Card>) pair.getValue();
            int size = cards.size();
            if(size > 1){
                Integer current = result.get(size);
                if(current == null) current = 0;
                current++;
                result.put(size, current);
            }
            it.remove();
        }

        return result;
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
        } catch (Exception e) {
            StackTraceElement[] stackTrace = e.getStackTrace();
            System.out.printf("we got an exception: %s\n", e.toString());
            for (StackTraceElement elem : stackTrace) {
                System.out.printf("    %s\n", elem.toString());
            }
        }

        return 0;
    }

    public static void showdown(JsonElement game) {
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setStack(String stack) {
        this.stack = stack;
    }

    public void setBet(String bet) {
        this.bet = bet;
    }

    public String getBet() {

        return bet;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return Name;
    }

    public String getStatus() {
        return status;
    }

    public String getVersion() {
        return version;
    }

    public String getStack() {
        return stack;
    }

}
