import com.fasterxml.jackson.databind.JsonNode;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.time.Instant;
import java.util.*;

public class InputFields {
    public static final String PLAYER1 = "player";
    public static final String ALL_DECK1 = "all_deck";
    public static final String DECK1 = "deck";
    public static final String CARDS1 = "cards";
    public static final String CLAN1 = "clan";
    public static final String CLAN_TR1 = "clanTr";
    public static final String TOUCH1 = "touch";
    public static final String PLAYER2 = "player";
    public static final String ALL_DECK2 = "all_deck2";
    public static final String DECK2 = "deck2";
    public static final String CARDS2 = "cards2";
    public static final String CLAN2 = "clan2";
    public static final String CLAN_TR2 = "clanTr2";
    public static final String TOUCH2 = "touch2";
    public static final String DATE = "date";
    public static final String ROUND = "round";
    public static final String WIN = "win";

    public static boolean checkFields(JsonNode game) throws JSONException {
        int touch1 = game.has(InputFields.TOUCH1) ? game.get(InputFields.TOUCH1).asInt() : 0;
        int touch2 = game.has(InputFields.TOUCH2) ? game.get(InputFields.TOUCH2).asInt() : 0;
        return touch1 == 1 && touch2 == 1
                && game.has(InputFields.PLAYER1) && game.has(InputFields.PLAYER1)
                && game.has(InputFields.ALL_DECK1) && game.has(InputFields.ALL_DECK2)
                && game.has((InputFields.DECK1)) && game.has(InputFields.DECK2)
                && game.has(InputFields.CARDS1) && game.has(InputFields.CARDS2)
                && game.has(InputFields.CLAN1) && game.has(InputFields.CLAN2)
                && game.has(InputFields.DATE)
                && game.has(InputFields.ROUND)
                && game.has(InputFields.WIN)
                && checkCardsInput(game.get(InputFields.CARDS1).asText())
                && checkCardsInput(game.get(InputFields.CARDS2).asText());
    }

    private static boolean checkCardsInput(String cards) {
        return cards.length() == 16 || (cards.length() == 18 && cards.startsWith("6E", 16));
    }

    public static String getCardsChecked(String cards) {
        if (cards.length() == 16) {
            return cards;
        } else if (cards.length() == 18 && cards.startsWith("6E", 16)) {
            return cards.substring(0, 17);
        } else {
            throw new IllegalArgumentException("Deck not valid");
        }
    }

    public static String sortCards(String cards) {
        ArrayList<String> cardList = new ArrayList<>();
        for (int i = 0; i < cards.length() / 2; ++i) {
            String card = cards.substring(i * 2, i * 2 + 2);
            cardList.add(card);
        }

        Collections.sort(cardList);
        return String.join("", cardList);
    }

    private static PlayerInfo createPlayer(JsonNode game, String playerKey, String allDeckKey, String deckKey, String cardsKey, String clanTrKey, String clanKey) throws JSONException {
        long clanTr = game.has(clanTrKey) ? game.get(clanTrKey).asLong() : 0;
        return new PlayerInfo(
                game.get(playerKey).asText(),
                game.get(allDeckKey).asDouble(),
                game.get(deckKey).asDouble(),
                InputFields.getCardsChecked(game.get(cardsKey).asText()),
                clanTr,
                game.get(clanKey).asText()
        );
    }

    private static PlayerInfo createPlayer1(JsonNode game) throws JSONException {
        return createPlayer(game, InputFields.PLAYER1, InputFields.ALL_DECK1, InputFields.DECK1, InputFields.CARDS1, InputFields.CLAN_TR1, InputFields.CLAN1);
    }

    private static PlayerInfo createPlayer2(JsonNode game) throws JSONException {
        return createPlayer(game, InputFields.PLAYER2, InputFields.ALL_DECK2, InputFields.DECK2, InputFields.CARDS2, InputFields.CLAN_TR2, InputFields.CLAN2);
    }

    public static Game createGame(JsonNode game) throws JSONException {
        return new Game(
                Instant.parse(game.get(InputFields.DATE).asText()),
                game.get(InputFields.ROUND).asLong(),
                game.get(InputFields.WIN).asLong(),
                createPlayer1(game),
                createPlayer2(game)
        );
    }
}
