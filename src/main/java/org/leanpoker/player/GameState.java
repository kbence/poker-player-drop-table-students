package org.leanpoker.player;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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
    public String in_action;
    public Player[]  players;
    public JsonArray community_cards;

    public String getIn_action() {
        return in_action;
    }

    public void setIn_action(String in_action) {

        this.in_action = in_action;
    }

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

    public String getTournament_id() {
        return tournament_id;
    }

    public String getGame_id() {
        return game_id;
    }

    public String getRound() {
        return round;
    }

    public String getBet_index() {
        return bet_index;
    }

    public String getSmall_blind() {
        return small_blind;
    }

    public String getCurrent_buy_in() {
        return current_buy_in;
    }

    public String getPot() {
        return pot;
    }

    public String getMinimum_raise() {
        return minimum_raise;
    }

    public String getDealer() {
        return dealer;
    }

    public String getOrbits() {
        return orbits;
    }

    public Player[] getPlayers() {
        return players;
    }

    public JsonArray getCommunity_cards() {
        return community_cards;
    }

    public void setPlayers(Player[] players) {
        this.players = players;
    }

    public void setCommunity_cards(JsonArray community_cards) {
        this.community_cards = community_cards;
    }

}
