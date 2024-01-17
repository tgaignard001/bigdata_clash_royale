import type { DateType } from "./deckSummary";

export type NGramSummary = {
    "_1": string;
    "_2": {
        date: {
            seconds: number,
            nanos: number
        }
        victories: number;
        uses: number;
        uniquePlayers: number;
        highestClanLevel: number;
        sumDiffForce: number;
        nbDiffForce: number;
    };
}

export type KeyData = {
    cards: string,
    type: DateType,
    year: number,
    value: number
}