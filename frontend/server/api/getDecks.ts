import { getAllLines } from "~/composables/decks";
import { MAXDECKLISTSIZE } from "~/models/deckSummary";

export default defineEventHandler(() => {
    return {
        content: getDecks(),
    }
})

function getDecks() {
    const selectedLines = getAllLines();
    const nbLines = selectedLines.length;
    const nbPages = nbLines/MAXDECKLISTSIZE;
    console.log(nbPages);

    let res = Array(Math.floor(nbPages));
    console.log(res.length);

    for (let i =0; i < nbPages; i++){
        const initIndex = i*MAXDECKLISTSIZE;
        if (initIndex + MAXDECKLISTSIZE >= nbLines){
            res[i] = selectedLines.slice(initIndex);
        }else{
            res[i] = selectedLines.slice(initIndex, initIndex + MAXDECKLISTSIZE);
        }
    }
    console.log(res[0]);

    return res;
}



