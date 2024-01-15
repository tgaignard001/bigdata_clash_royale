package bigdata;

import java.time.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SummaryCreator {
    PlayerInfoWritable player1;
    PlayerInfoWritable player2;
    Instant date;
    boolean isPlayer1Win;
    double diffForce;

    public SummaryCreator(PlayerInfoWritable player1, PlayerInfoWritable player2, Instant date, long win) {
        this.player1 = player1;
        this.player2 = player2;
        this.date = date;
        this.isPlayer1Win = win == 1;
        this.diffForce = player1.deck - player2.deck;
    }

    public ArrayList<DeckSummary> generateSummaries() {
        ArrayList<DeckSummary> summaryList = new ArrayList<>();
        for (SummaryDateType dateType : SummaryDateType.values()) {
            DeckSummary deckSummary1 = new DeckSummary(player1.cards, date, dateType);
            DeckSummary deckSummary2 = new DeckSummary(player2.cards, date, dateType);

            if (isPlayer1Win) {
                deckSummary1.incVictories();
            } else {
                deckSummary2.incVictories();
            }

            deckSummary1.incUses();
            deckSummary2.incUses();

            deckSummary1.setHighestClanLevel(player1.clanTr);
            deckSummary2.setHighestClanLevel(player2.clanTr);

            deckSummary1.addDiffForce(diffForce);
            deckSummary2.addDiffForce(-diffForce);

            deckSummary1.incNbDiffForce();
            deckSummary2.incNbDiffForce();

            summaryList.add(deckSummary1);
            summaryList.add(deckSummary2);
        }
        return summaryList;
    }
}
