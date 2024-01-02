import { getAllLines } from "~/composables/decks"
import { DeckSummary } from "~/models/deckSummary";

export default defineEventHandler(() => {
    return {
        content: getBetterDeck(),
    }
})

function getBetterDeck(): DeckSummary {
    return getAllLines()[0];
}