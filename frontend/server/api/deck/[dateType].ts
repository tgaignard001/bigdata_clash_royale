import { getAllLines } from "~/composables/decks";
import { MAXDECKLISTSIZE } from "~/models/deckSummary";

export default defineEventHandler((event) => {
    const dateType = event.context.params?.dateType;
    const filePath = "utils/top500_" + ((dateType) ? dateType : "2023") + ".txt"
    console.log("Params from /api/getDecks: ", dateType);

    return {
        content: getDecks(filePath),
    }
})

function getDecks(filePath: string) {
    const selectedLines = getAllLines(filePath);
    const nbLines = selectedLines.length;
    const nbPages = nbLines/MAXDECKLISTSIZE;

    let res = Array(Math.floor(nbPages));

    for (let i =0; i < nbPages; i++){
        const initIndex = i*MAXDECKLISTSIZE;
        if (initIndex + MAXDECKLISTSIZE >= nbLines){
            res[i] = selectedLines.slice(initIndex);
        }else{
            res[i] = selectedLines.slice(initIndex, initIndex + MAXDECKLISTSIZE);
        }
    }

    return res;
}



