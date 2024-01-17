import { getDecksPagination, getTopK } from "~/composables/decks";
import { DateType } from "~/models/deckSummary";

export default defineEventHandler((event) => {
    const params = event.context.params?.dateType;
    const parts = params?.split("_");
    if (!parts) return {content: "No params"};
    const dateType = (parts[0] == "M") ? DateType.MONTHLY : ((parts[0] == "W") ? DateType.WEEKLY : DateType.NONE);
    const year = (parts[1] != "") ? parseInt(parts[1]) : 2023;
    const value = (parts[2] != "") ? parseInt(parts[2]) : 0;
    const sortName = (parts[3] != "") ? parts[3] : "wr";
    console.log("Params from /api/deck/getDecks: ", dateType, year, value, sortName);

    return {
        content: getDecksPagination(getTopK(dateType, year, value, sortName)),
    }
})





