import  { cr_cards } from "../models/cards";
import type { Card } from "../models/cards";


export class Deck {
    static _card2Int: {[id: string]: number;} | undefined = undefined;
    static _int2Card: Card[] = [];

    private _deck: string[];
    private _nbCard: number;
/*
* constructor from a alpanumerical string that encodes cards.
*/
constructor(deckstr: string | undefined = undefined) {
    this._deck = [];
    this._nbCard = 0;

    if (Deck._card2Int === undefined) {
        Deck._card2Int = {};
        for (let i = 0; i < cr_cards.length; ++i) {
            Deck._card2Int[cr_cards[i].id] = i;
            Deck._int2Card[i] = cr_cards[i];
        }
    }
    if (deckstr !== undefined) {
        let firstCard = deckstr.substring(0, 2);
        let fc = Deck._int2Card[parseInt(firstCard, 16)];
        let evo = 0;
        if (fc.iconUrls.evo !== undefined)
            evo = 1;
        let tmp = [];
        for (let i = evo; i < deckstr.length / 2; ++i) {
            let v = deckstr.substring(i * 2, i * 2 + 2);
            tmp.push(v);
            ++this._nbCard;
        }

        tmp.sort((a, b) => {
            if (a < b) return -1;
            if (a > b) return 1;
            return 0;
        });

        if (evo === 1) {
            let v = deckstr.substring(0, 2);
            this._deck.push(v);
            ++this._nbCard;
        }
        for (let x in tmp){
            this._deck.push(tmp[x]);
        }
    }
}

sort() {
    this._deck.sort((a, b) => {
        if (a < b) return -1;
        if (a > b) return 1;
        return 0;
    });
}

/*
return the number of cards in that deck
*/
nbCards() {
    return this._nbCard;
}

/*
* Add a card into the DEcks
*/
addCard(c: number) {
    /*
    if (this._nbCard > 7) {
        console.log("[ERROR] deck is full " + c + " nbcard: " + this._nbCard);
        return;
    }
    */
    if (Deck._card2Int === undefined) {
        console.log("[ERROR] card2Int not initialised");
        return;
    }
    let cc = Deck._card2Int[c];
    if (cc === undefined) {
        console.log("[ERROR] not a valid card : " + c);
        return;
    }
    let str = cc.toString(16);
    if (str.length != 2) {
        str = "0" + str;
    }
    this._deck.push(str);
    ++this._nbCard;
}

addHexCard(hex: string) {
    let str = hex;
    this._deck.push(str);
    ++this._nbCard;
}

/*
return arrays of card names
*/
scards() {
    let result = [];
    for (let i = 0; i < this._nbCard; ++i) {
        let c = Deck._int2Card[parseInt(this._deck[i], 16)];
        if (c !== undefined)
            result.push(c.name);
        else {
            console.log(this._deck[i]);
        }
    }
    return result;
}

/*
return arrays of couple name, imageurl
*/
cards() {
    let result = [];
    for (let i = 0; i < this._nbCard; ++i) {
        let c = Deck._int2Card[parseInt(this._deck[i], 16)]
        result.push([c.name, c.iconUrls.medium, c.iconUrls.evo]);
    }
    return result;
}

/*
return arrays of indexes
*/
icards() {
    let result = [];
    for (let i = 0; i < this._nbCard; ++i) {
        result.push(parseInt(this._deck[i], 16));
    }
    return result;
}
/*
return alphanumerical encoding of the deck
*/
toString() {
    return this._deck.join("");
}
}

let _mapN2U: {[name: string]: string[]} | undefined = undefined;
/*
* return a link to the image of a card  from its english name.
*/
function nameToUrl(name: string, evo = false) {
if (_mapN2U === undefined) {
    _mapN2U = {};
    for (let x in cr_cards) {
        let c = cr_cards[x];
        if (c.iconUrls.medium){
            _mapN2U[c.name].push(c.iconUrls.medium)
        }
        if (c.iconUrls.evo){
            _mapN2U[c.name].push(c.iconUrls.evo)
        }
    }
}
if (evo)
    return _mapN2U[name][1];
else
    return _mapN2U[name][0];
}

let _mapN2Id: undefined | {[name: string]: number} = undefined;
/*
* return the supercell Id of a card from its english name.
*/
function nameToId(name: string) {
    if (_mapN2Id === undefined) {
        _mapN2Id = {};
        for (let x in cr_cards) {
            let c = cr_cards[x];
            _mapN2Id[c.name] = c.id;
        }
    }
    return _mapN2Id[name];
}


let _mapId2Card: undefined | {[id: number]: Card} = undefined;
/*
* return a card from its SuperCell id
*/
function idToCard(id: number) {
    if (_mapId2Card === undefined) {
        _mapId2Card = {};
        for (let x in cr_cards) {
            let c = cr_cards[x];
            _mapId2Card[c.id] = c;
        }
    }
    return _mapId2Card[id];
}

module.exports = {
    Deck,
    nameToUrl,
    nameToId,
    idToCard
}