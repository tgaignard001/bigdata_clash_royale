import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

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

    public static boolean checkFields(JSONObject game) throws JSONException {
        int touch1 = game.has(InputFields.TOUCH1) ? game.getInt(InputFields.TOUCH1) : 0;
        int touch2 = game.has(InputFields.TOUCH2) ? game.getInt(InputFields.TOUCH2) : 0;
        return touch1 == 1 && touch2 == 1
                && game.has(InputFields.PLAYER1) && game.has(InputFields.PLAYER1)
                && game.has(InputFields.ALL_DECK1) && game.has(InputFields.ALL_DECK2)
                && game.has((InputFields.DECK1)) && game.has(InputFields.DECK2)
                && game.has(InputFields.CARDS1) && game.has(InputFields.CARDS2)
                && game.has(InputFields.CLAN1) && game.has(InputFields.CLAN2)
                && game.has(InputFields.DATE)
                && game.has(InputFields.ROUND)
                && game.has(InputFields.WIN)
                && checkCardsInput(game.getString(InputFields.CARDS1))
                && checkCardsInput(game.getString(InputFields.CARDS2));
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
}
