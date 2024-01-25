import type { KeyData, NGramSummary } from "~/models/nGramSummary";
import { readFileSync } from "fs";
import { DateType } from "~/models/deckSummary";

const default_path = "utils/spark_ngrams_2_3_big.txt";

export function getAllLines(filePath = default_path): NGramSummary[]{
    const allLines = readFileSync(filePath, 'utf-8').split("\n").map(extractNGramSummary) as NGramSummary[];

    return allLines;
}

function extractNGramSummary(line: string, index: number) {
    try{
        let maxLog = 0;
        const jsonLine = JSON.parse(line) as NGramSummary;
        return jsonLine;
    }catch (e){
        console.log("error of deserialisation on line nb ", index, ": ", line, "...");
    }
}

export function getNGramSummaries(ngram: string){
    const lines = getAllLines();
    let res: NGramSummary[] = [];

    lines.map((value) => {
        const data = extractDataFromKey(value._1);
        if (data.type == DateType.WEEKLY && data.cards == sortNGram(ngram)){
            res.push(value);
            return value;
        }
    })
    return res;
}

function extractDataFromKey(key: string){
    const parts = key.split('-');
    const len = parts.length;
    let res: KeyData = {
        cards: parts[0],
        type: DateType.NONE,
        year: 0,
        value: 0
    }
    if (len == 3){
        const dateType = parts[1][0];
        res.year = parseInt(parts[1].slice(1));
        res.value = parseInt(parts[2]);
        switch(dateType){
            case "M":
                res.type = DateType.MONTHLY;
                break;
            case "W":
                res.type = DateType.WEEKLY;
                break;
        }
    }
    return res;
}

function sortNGram(ngram: string){
    let tmp = [];
    for (let i = 0; i < ngram.length / 2; ++i) {
        let v = ngram.substring(i * 2, i * 2 + 2);
        tmp.push(v);
    }
    tmp.sort((a, b) => {
        if (a < b) return -1;
        if (a > b) return 1;
        return 0;
    });

    let res = "";
    for (let x in tmp){
        res += tmp[x];
    }
    return res;
}