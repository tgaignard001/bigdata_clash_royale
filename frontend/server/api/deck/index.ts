import { getTopK } from "~/composables/decks";
import { DateType } from "~/models/deckSummary";

export default defineEventHandler(() => {
    return {
        content: getTopK(DateType.NONE, 2023, 0, "wr"),
    }
})