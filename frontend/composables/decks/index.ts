import { readFileSync } from "fs";
import type { DeckSummary } from "~/models/deckSummary";


export function getAllLines(filePath = "utils/top500_2023.txt"){
    const allLines = readFileSync(filePath, 'utf-8').split("\n").map(extractDeckSummary) as DeckSummary[];


    return allLines.reverse();
}

// Fonction pour extraire les données d'une ligne
export function extractDeckSummary(line: string): DeckSummary | null {
    return JSON.parse(line);
}
