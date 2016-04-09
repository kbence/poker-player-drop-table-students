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

    static final String VERSION = "Not so default ass-kicking Java player";


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

    public static int getCardValue(Card card) {
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

            int currentBet = player.get("bet").getAsInt();
            int currentStack = player.get("stack").getAsInt();
            int minimumRaise = request.getAsJsonObject().get("minimum_raise").getAsInt();
            int currentBuyIn = request.getAsJsonObject().get("current_buy_in").getAsInt();

            int amountToHold = currentBuyIn - currentBet;
            int amountToRaise = amountToHold + minimumRaise;

            // Calc position
            int position = 0;
            PairLike[] analyzeResult = analyzeHand(hand, cardsOnTable);
            if(analyzeResult.length == 1){
                position = analyzeResult[0].number * analyzeResult[0].rank;
            }else if(analyzeResult.length == 2) {
                if (analyzeResult[0].number == 2 && analyzeResult[1].number == 2) {
                    int pairRank = Math.max(analyzeResult[0].rank, analyzeResult[1].rank);
                    if (pairRank > 10) {
                        position = 200 + 2 * analyzeResult[0].rank + 2 * analyzeResult[1].rank;
                    } else {
                        position = 200 + (3 * pairRank);
                    }
                } else if(analyzeResult[0].number == 3 && analyzeResult[1].number == 3){
                    int pairRank = Math.max(analyzeResult[0].rank, analyzeResult[1].rank);
                    if (pairRank > 10) {
                        position = 200 + 3 * analyzeResult[0].rank + 3 * analyzeResult[1].rank;
                    } else {
                        position = 250 + (3 * pairRank);
                    }
                }else{
                    position = 200 + analyzeResult[0].number * analyzeResult[0].rank + analyzeResult[1].number * analyzeResult[1].rank;
                }

            }

            if (position > 0) {
                double positionMult = 1 + position / 400.0f;

                if (currentBet > ((positionMult / 2) * currentStack) / 3) {
                    System.out.printf("Holding because currentBet (%d) > currentStack / 3 (%d)\n",
                            currentBet, currentStack / 2);
                    return amountToHold;
                }

                if (currentBet > ((positionMult - 1.0f) * currentStack) / 2) {
                    System.out.printf("Folding because currentBet (%d) > currentStack / 2 (%d)\n",
                            currentBet, currentStack / 2);
                    return 0;
                }


                int bet = Math.max(amountToRaise, amountToHold + (int)((positionMult * currentStack) / 10));
                System.out.printf("Betting %d for pair\n", bet);
                return bet;
            }

            if (hand[0].suit.equals(hand[1].suit) && getCardValue(hand[0]) + getCardValue(hand[1]) > 20) {
                int bet = Math.max(amountToRaise, amountToHold + currentStack / 15);

                if (bet > 500) {
                    return 0;
                }

                System.out.printf("Betting %d for high card (%d %d)\n", bet, getCardValue(hand[0]), getCardValue(hand[1]));
                return bet;
            }
        } catch (Exception e) {
            StackTraceElement[] stackTrace = e.getStackTrace();
            System.out.printf("we got an exception: %s\n", e.toString());
            for (StackTraceElement elem : stackTrace) {
                System.out.printf("    %s\n", elem.toString());
            }
        }

        System.out.println("Folding by default");
        int randBet = 0;
        if(Math.random() < 0.1) randBet = Math.round(Math.round(Math.random() * 200));
        System.out.printf("Folding by default (%d)\n", randBet);
        return randBet;
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
