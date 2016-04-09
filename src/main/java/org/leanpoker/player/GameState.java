package org.leanpoker.player;

import com.google.gson.JsonArray;

/**
 * Created by wissam on 09/04/16.
 */
public class GameState {
    public String tournament_id;
    public String game_id;
    public String round;
    public String bet_index;
    public String small_blind;
    public String current_buy_in;
    public String pot;
    public String minimum_raise;
    public String dealer;
    public String orbits;
    public JsonArray players;
    public JsonArray community_cards;

    public void setTournament_id(String tournament_id) {
        this.tournament_id = tournament_id;
    }

    public void setGame_id(String game_id) {
        this.game_id = game_id;
    }

    public void setRound(String round) {
        this.round = round;
    }

    public void setBet_index(String bet_index) {
        this.bet_index = bet_index;
    }

    public void setSmall_blind(String small_blind) {
        this.small_blind = small_blind;
    }

    public void setCurrent_buy_in(String current_buy_in) {
        this.current_buy_in = current_buy_in;
    }

    public void setPot(String pot) {
        this.pot = pot;
    }

    public void setMinimum_raise(String minimum_raise) {
        this.minimum_raise = minimum_raise;
    }

    public void setDealer(String dealer) {
        this.dealer = dealer;
    }

    public void setOrbits(String orbits) {
        this.orbits = orbits;
    }

    public void setPlayers(JsonArray players) {
        this.players = players;
    }

    public void setCommunity_cards(JsonArray community_cards) {
        this.community_cards = community_cards;
    }
}
