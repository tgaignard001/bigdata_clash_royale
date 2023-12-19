import { readFileSync } from "fs";
import type { DeckSummary } from "~/models/deckSummary";
import { MAXDECKLISTSIZE } from "~/models/deckSummary";


const filePath = "utils/summary_small.txt"

export default defineEventHandler((event) => {
    return {
        content: getDecks(),
    }
})

function getAllLines(){
    return readFileSync(filePath, 'utf-8').split("\n").map(extractDeckSummary).filter((summary) => summary !== null) as DeckSummary[]
}

function getNbPages(){
    console.log("nb lines:" + getAllLines().length);

    return getAllLines().length/MAXDECKLISTSIZE;
}

function getDecks() {
    const selectedLines = getAllLines();
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



// Fonction pour extraire les données d'une ligne
function extractDeckSummary(line: string): DeckSummary | null {
    const match = line.match(/(\w+)\s*DeckSummary{victories=(\d+), uses=(\d+), uniquePlayers=(\d+), highestClanLevel=(\d+), sumDiffForce=([\d.]+), nbDiffForce=(\d+)}/);

    if (match) {
        const [_, strDeck, victories, uses, uniquePlayers, highestClanLevel, sumDiffForce, nbDiffForce] = match;
        return {
            strDeck,
            victories: parseInt(victories, 10),
            uses: parseInt(uses, 10),
            uniquePlayers: parseInt(uniquePlayers, 10),
            highestClanLevel: parseInt(highestClanLevel, 10),
            sumDiffForce: parseFloat(sumDiffForce),
            nbDiffForce: parseInt(nbDiffForce, 10),
        };
    } else {
        // console.warn(`La ligne suivante ne correspond pas au format attendu : ${line}`);
        return null;
    }
}