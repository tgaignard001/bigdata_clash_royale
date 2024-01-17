export const MAXDECKLISTSIZE = 20;

export enum DateType {
    NONE,
    MONTHLY,
    WEEKLY,
}

export type DeckSummary = {
    sortedCards: string;
    date: Date;
    dateType: DateType;
    victories: number;
    uses: number;
    uniquePlayers: number;
    highestClanLevel: number;
    MeanDiffForce: number;
}

export const EMPTY_DECK_SUMMARY: DeckSummary = {
    sortedCards: "0000000000000000",
    date: new Date(),
    dateType: DateType.NONE,
    victories: -1,
    uses: -1,
    uniquePlayers: -1,
    highestClanLevel: -1,
    MeanDiffForce: -1,
}