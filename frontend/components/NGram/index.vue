<script setup lang="ts">
import  { cr_cards, type Card } from "~/models/cards";
const card_list = ref([cr_cards[0]]);

function getNGram(list: Card[]){
    let deck = new Deck();
    list.map((value) => deck.addCard(value.id));
    return deck.toString();
}
</script>
<template>
    <div class="flex flex-col items-center h-[81vh] w-full gap-10 p-10">
        <NGramCards :cards="card_list" />
        <NGramChart :key="getNGram(card_list)" :ngram="getNGram(card_list)"/>
    </div>
</template>