import { getDecks } from "~/composables/decks";

export default defineEventHandler(() => {
    console.log("Call to api/deck/index");

    return {
        content: getDecks(),
    }
})