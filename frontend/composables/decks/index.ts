import { getISOWeek, getMonth, getYear } from "date-fns";
import { readFileSync } from "fs";
import type { DeckSummary } from "~/models/deckSummary";
import { DateType, MAXDECKLISTSIZE } from "~/models/deckSummary";
import type { DateData } from "~/models/topk";
import { getDateType } from "~/composables/date";

const default_path = "utils/top500_wr.txt";

export function getAllLines(filePath = default_path): DeckSummary[][]{
    const allLines = readFileSync(filePath, 'utf-8').split("\n").map(extractDeckSummary) as DeckSummary[][];

    return allLines;
}

export function extractDeckSummary(line: string): DeckSummary[] {
    const jsonLine = JSON.parse(line);
    return jsonLine.map((item: any) =>{
        return {
            sortedCards: item.sortedCards,
            date: new Date(item.date),
            dateType: getDateType(item.dateType),
            victories: item.victories,
            uses: item.uses,
            uniquePlayers: item.uniquePlayers,
            highestClanLevel: item.highestClanLevel,
            MeanDiffForce: item.MeanDiffForce,
        }
    })
}

export function getDecksPagination(deckList: DeckSummary[]) {
    const nbLines = deckList.length;
    const nbPages = nbLines/MAXDECKLISTSIZE;

    let res = Array(Math.floor(nbPages));

    for (let i =0; i < nbPages; i++){
        const initIndex = i*MAXDECKLISTSIZE;
        if (initIndex + MAXDECKLISTSIZE >= nbLines){
            res[i] = deckList.slice(initIndex);
        }else{
            res[i] = deckList.slice(initIndex, initIndex + MAXDECKLISTSIZE);
        }
    }

    return res;
}

export function getDatesData(firstLine: DeckSummary[]){
    let datesData: DateData[] = []
    firstLine.map((deck) => {
        const year = getYear(deck.date);
        const isNewYear = datesData.find((value) => {if (value.year == year) return value}) == undefined;
        if (isNewYear){
            datesData.push({weeks: [], months: [], year: year})
        }
        const data = datesData.find((value) => {if (value.year == year) return value});
        if (!data) return;
        switch(deck.dateType){
            case DateType.MONTHLY:
                data.months.push(getMonth(deck.date))
                break;
            case DateType.WEEKLY:
                data.weeks.push(getISOWeek(deck.date))
                break;
        }
    })
    datesData.map((data) => {
        data.weeks = [...new Set(data.weeks)].sort();
        data.months = [...new Set(data.months)].sort();
    })

    return datesData;
}

function getDeckIndex(year: number, dateType: DateType, value: number, firstLine: DeckSummary[]){
    let resIndex = -1;
    firstLine.map((deck, index) =>{
        const isGoodYear = getYear(deck.date) == year;
        const isGoodDateType = dateType == deck.dateType;
        switch(deck.dateType){
            case DateType.NONE:
                if (isGoodDateType) resIndex = index;
                break;
            case DateType.MONTHLY:
                if (isGoodDateType && isGoodYear && getMonth(deck.date) == value) resIndex = index;
                break;
            case DateType.WEEKLY:
                if (isGoodDateType && isGoodYear && getISOWeek(deck.date) == value) resIndex = index;
                break;
        }
    })
    return resIndex;
}

export function getTopK(dateType: DateType, year: number, value: number, sortBy: string){
    const allLines = getAllLines("utils/top500_" + sortBy + ".txt");
    const index = getDeckIndex(year, dateType, value, allLines[0]);

    if (index < 0) return [];

    let top: DeckSummary[] = [];
    allLines.map((deckList) => {
        if (deckList[index].sortedCards != "0000000000000000") top.push(deckList[index]);
    });
    return top;
}