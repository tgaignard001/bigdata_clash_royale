import { getTopK } from "~/composables/decks"
import { DeckSummary } from "~/models/deckSummary";
import { DateType }from "~/models/deckSummary";

export default defineEventHandler(() => {
    return {
        content: getBetterDeck(),
    }
})

function getBetterDeck(): DeckSummary {
    return getTopK(DateType.NONE, 2023, 0, "wr")[0];
}