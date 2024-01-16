import { getDecks } from "~/composables/decks";

export default defineEventHandler((event) => {
    const dateType = event.context.params?.dateType;
    const filePath = "utils/top500" + ((dateType) ? "_" + dateType : "2023") + ".txt"
    // console.log("Params from /api/deck/getDecks: ", dateType);

    return {
        content: getDecks(filePath),
    }
})





