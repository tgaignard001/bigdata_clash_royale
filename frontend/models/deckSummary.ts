export const MAXDECKLISTSIZE = 20;

export type DeckSummary = {
    strDeck: string;
    victories: number;
    uses: number;
    uniquePlayers: number;
    highestClanLevel: number;
    sumDiffForce: number;
    nbDiffForce: number;
}

export const EMPTY_DECK_SUMMARY: DeckSummary = {
    strDeck: "0000000000000000",
    victories: -1,
    uses: -1,
    uniquePlayers: -1,
    highestClanLevel: -1,
    sumDiffForce: -1,
    nbDiffForce: -1
}