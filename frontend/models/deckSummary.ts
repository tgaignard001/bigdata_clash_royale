export const MAXDECKLISTSIZE = 20;

export type DeckSummary = {
    cards: string;
    year: number;
    month: number;
    dateType: string;
    victories: number;
    uses: number;
    uniquePlayers: number;
    highestClanLevel: number;
    sumDiffForce: number;
    nbDiffForce: number;
}

export const EMPTY_DECK_SUMMARY: DeckSummary = {
    cards: "0000000000000000",
    year: 2023,
    month: 1,
    dateType: "YEARLY",
    victories: -1,
    uses: -1,
    uniquePlayers: -1,
    highestClanLevel: -1,
    sumDiffForce: -1,
    nbDiffForce: -1
}