import { readFileSync } from "fs";
import type { DeckSummary } from "~/models/deckSummary";

const filePath = "utils/top500.txt"

export function getAllLines(){
    const allLines = readFileSync(filePath, 'utf-8').split("\n").map(extractDeckSummary) as DeckSummary[];

    console.log(allLines.length);

    return allLines.reverse();
}

// Fonction pour extraire les données d'une ligne
export function extractDeckSummary(line: string): DeckSummary | null {
    return JSON.parse(line);
}
