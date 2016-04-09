package org.leanpoker.player;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.junit.Test;

import org.leanpoker.player.Player.Card;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class PlayerTest {

    @Test
    public void testBetRequest() throws Exception {

        JsonElement jsonElement = new JsonParser().parse("{\"key1\": \"value1\", \"key2\": \"value2\"}");

        assertEquals(0, Player.betRequest(jsonElement));

    }

    @Test
    public void testAnalyzeHand() throws Exception {

        Card[] hand = new Card[2];
        hand[0] = new Card();
        hand[0].suit = "diamonds";
        hand[0].rank = "1";

        hand[1] = new Card();
        hand[1].suit = "diamonds";
        hand[1].rank = "2";

        Card[] community = new Card[2];
        community[0] = new Card();
        community[0].suit = "diamonds";
        community[0].rank = "2";

        community[1] = new Card();
        community[1].suit = "diamonds";
        community[1].rank = "3";

        Map<Integer, Integer> map = Player.analyzeHand(hand, community);
        assertEquals(null, map.get(1));
        assertEquals(null, map.get(3));
        assertEquals(1, map.get(2).intValue());
    }
}
