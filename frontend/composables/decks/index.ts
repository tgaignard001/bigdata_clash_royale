import { readFileSync } from "fs";
import type { DeckSummary } from "~/models/deckSummary";
import { MAXDECKLISTSIZE } from "~/models/deckSummary";

const default_path = "utils/top500.txt";

export function getAllLines(filePath = default_path){
    const allLines = readFileSync(filePath, 'utf-8').split("\n").map(extractDeckSummary) as DeckSummary[];

    return allLines.reverse();
}

// Fonction pour extraire les données d'une ligne
export function extractDeckSummary(line: string): DeckSummary | null {
    return JSON.parse(line);
}

export function getDecks(filePath = default_path) {
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